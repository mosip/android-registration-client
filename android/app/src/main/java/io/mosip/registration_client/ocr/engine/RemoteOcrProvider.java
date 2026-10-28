/*
 * Copyright (c) Modular Open Source Identity Platform
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.mosip.registration_client.ocr.engine;

import android.graphics.Bitmap;
import android.util.Base64;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.mosip.registration_client.BuildConfig;
import io.mosip.registration_client.ocr.models.FieldSpec;
import io.mosip.registration_client.ocr.models.OcrError;
import io.mosip.registration_client.ocr.models.OcrErrorCode;

public class RemoteOcrProvider implements OcrProvider {

    private static final String SCREEN_NAME = "DemographicDetails";

    public interface AuthTokenProvider {
        @Nullable
        String getAuthToken();
    }

    private final String serviceUrl;
    private final int    socketTimeoutMs;
    @NonNull
    private final List<String> allowedEndpoints;
    @Nullable
    private final AuthTokenProvider authTokenProvider;
    private final ExecutorService executor;
    private volatile HttpURLConnection activeConnection;
    private volatile java.util.concurrent.Future<?> activeTask;

    public RemoteOcrProvider(
            @NonNull String serviceUrl,
            int socketTimeoutMs,
            @Nullable List<String> allowedEndpoints,
            @Nullable AuthTokenProvider authTokenProvider) {
        this.serviceUrl = serviceUrl;
        this.socketTimeoutMs = socketTimeoutMs;
        this.allowedEndpoints = allowedEndpoints != null ? allowedEndpoints : Collections.emptyList();
        this.authTokenProvider = authTokenProvider;
        this.executor = Executors.newSingleThreadExecutor();
        validateServiceUrl(serviceUrl);
    }

    public RemoteOcrProvider(@NonNull String serviceUrl, int socketTimeoutMs) {
        this(serviceUrl, socketTimeoutMs, null, null);
    }

    @Override
    public void extract(
            @NonNull Bitmap bitmap,
            @NonNull List<FieldSpec> spec,
            @NonNull OcrProviderCallback callback) {

        activeTask = executor.submit(() -> {
            try {
                // Validate configuration and auth before touching resident bitmap
                validatePreconditions();

                String requestBody = buildRequestJson(bitmap, spec);
                String responseBody = postToServer(requestBody);

                if (responseBody == null || responseBody.trim().isEmpty()) {
                    callback.onFailure(
                            OcrError.ErrorCode.MALFORMED_RESPONSE.name(),
                            "OCR server returned an empty response",
                            false);
                    return;
                }

                parseResponse(responseBody, callback);

            } catch (SecurityException e) {
                callback.onFailure(
                        OcrError.ErrorCode.NETWORK_ERROR.name(),
                        "Security validation failed for remote OCR: " + e.getMessage(),
                        false);
            } catch (IOException e) {
                boolean isTimeout = e.getMessage() != null
                        && e.getMessage().toLowerCase().contains("timeout");
                callback.onFailure(
                        isTimeout ? OcrError.ErrorCode.TIMEOUT.name() : OcrError.ErrorCode.NETWORK_ERROR.name(),
                        "OCR server request failed: " + e.getMessage(),
                        true);
            } catch (Exception e) {
                callback.onFailure(
                        OcrError.ErrorCode.UNKNOWN.name(),
                        "Unexpected error during remote OCR: " + e.getMessage(),
                        false);
            }
        });
    }

    @Override
    public void cancel() {
        HttpURLConnection conn = activeConnection;
        if (conn != null) {
            try {
                conn.disconnect();
            } catch (Exception ignored) { }
            activeConnection = null;
        }
        java.util.concurrent.Future<?> task = activeTask;
        if (task != null) {
            task.cancel(true);
            activeTask = null;
        }
    }

    @Override
    public void release() {
        cancel();
        executor.shutdownNow();
    }

    public void validatePreconditions() {
        validateServiceUrl(serviceUrl);
        if (authTokenProvider == null) {
            throw new SecurityException("MOSIP authentication required: No auth token provider configured.");
        }
        String token = authTokenProvider.getAuthToken();
        if (token == null || token.trim().isEmpty()) {
            throw new SecurityException("MOSIP authentication required: No active session auth token available.");
        }
    }

    public void validateServiceUrl(@NonNull String urlString) {
        if (urlString == null || urlString.trim().isEmpty()) {
            throw new SecurityException("OCR service URL cannot be empty");
        }
        URI uri;
        try {
            uri = URI.create(urlString.trim());
        } catch (IllegalArgumentException e) {
            throw new SecurityException("Malformed OCR service URL: " + urlString, e);
        }

        String scheme = uri.getScheme();
        if (scheme == null || !"https".equalsIgnoreCase(scheme)) {
            throw new SecurityException("Insecure URL scheme '" + scheme + "'. Only HTTPS is permitted for remote OCR.");
        }

        String host = uri.getHost();
        if (host == null || host.trim().isEmpty()) {
            throw new SecurityException("OCR service URL missing valid host: " + urlString);
        }

        if (!isHostAllowlisted(host)) {
            throw new SecurityException("OCR service endpoint '" + host + "' is not in the approved endpoint allowlist.");
        }
    }

    public boolean isHostAllowlisted(@NonNull String host) {
        String normalizedHost = host.toLowerCase(Locale.ROOT);

        // Check against BuildConfig.BASE_URL host
        String baseMosipUrl = BuildConfig.BASE_URL;
        if (baseMosipUrl != null && !baseMosipUrl.trim().isEmpty()) {
            try {
                URI baseUri = URI.create(baseMosipUrl.trim());
                String baseHost = baseUri.getHost();
                if (baseHost != null && !baseHost.trim().isEmpty()) {
                    baseHost = baseHost.toLowerCase(Locale.ROOT);
                    if (normalizedHost.equals(baseHost) || normalizedHost.endsWith("." + baseHost)) {
                        return true;
                    }
                }
            } catch (Exception ignored) { }
        }

        // Check against explicit allowedEndpoints
        for (String allowed : allowedEndpoints) {
            if (allowed == null || allowed.trim().isEmpty()) continue;
            String allowedHost = allowed.trim().toLowerCase(Locale.ROOT);
            if (allowedHost.startsWith("http://") || allowedHost.startsWith("https://")) {
                try {
                    URI uri = URI.create(allowedHost);
                    if (uri.getHost() != null) {
                        allowedHost = uri.getHost().toLowerCase(Locale.ROOT);
                    }
                } catch (Exception ignored) { }
            }
            if (normalizedHost.equals(allowedHost) || normalizedHost.endsWith("." + allowedHost)) {
                return true;
            }
        }

        return false;
    }

    private void parseResponse(@NonNull String responseBody, @NonNull OcrProviderCallback callback) {
        JSONObject json;
        try {
            json = new JSONObject(responseBody);
        } catch (JSONException e) {
            callback.onFailure(
                    OcrError.ErrorCode.MALFORMED_RESPONSE.name(),
                    "OCR server response is not valid JSON: " + e.getMessage(),
                    false);
            return;
        }

        // §6.2: error is always structurally present alongside
        // metadata/data — populated on failure, null/empty on success.
        // Check error.errorCode FIRST, before touching metadata/data (§6.3).
        JSONObject errorJson = json.optJSONObject("error");
        String errorCode = null;
        String errorInfo = null;
        if (errorJson != null) {
            String rawCode = errorJson.optString("errorCode", "");
            if (!rawCode.trim().isEmpty()) {
                errorCode = rawCode.trim();
                errorInfo = errorJson.optString("errorInfo", null);
            }
        }

        JSONObject metadata = json.optJSONObject("metadata");
        JSONObject data = json.optJSONObject("data");
        boolean hasResult = metadata != null && data != null;
        boolean hasError = errorCode != null;

        if (hasError && hasResult) {
            // §6.2 note: error.errorCode populated AND metadata/data
            // populated is itself a contract violation.
            callback.onFailure(
                    OcrError.ErrorCode.MALFORMED_RESPONSE.name(),
                    "OCR server response has both error.errorCode and metadata/data populated",
                    false);
            return;
        }

        if (hasError) {
            OcrErrorCode standardCode = OcrErrorCode.fromString(errorCode);
            if (standardCode == null) {
                boolean isCustom = false;
                try {
                    isCustom = OcrErrorCode.isCustomRange(Integer.parseInt(errorCode.trim()));
                } catch (NumberFormatException ignored) { }
                if (!isCustom) {
                    callback.onProviderError(
                            OcrErrorCode.TECHNICAL_ERROR.asString(),
                            "Server returned unrecognized errorCode '" + errorCode + "': " + errorInfo);
                    return;
                }
            }
            callback.onProviderError(errorCode, errorInfo);
            return;
        }

        if (!hasResult) {
            callback.onFailure(
                    OcrError.ErrorCode.MALFORMED_RESPONSE.name(),
                    "OCR server response missing metadata/data and no error present",
                    false);
            return;
        }

        String documentType = metadata.optString("documentType", "UNKNOWN");
        double rawConf = metadata.optDouble("confidence", 0.0);
        float confidence = Double.isNaN(rawConf) ? 0f : (float) rawConf;

        Map<String, String> extracted = new LinkedHashMap<>();
        java.util.Iterator<String> keys = data.keys();
        while (keys.hasNext()) {
            String key = keys.next();
            String value = data.optString(key, null);
            if (value != null && !value.trim().isEmpty()) {
                extracted.put(key, value);
            }
        }

        if (extracted.isEmpty()) {
            callback.onFailure(
                    OcrError.ErrorCode.NO_FIELDS_EXTRACTED.name(),
                    "OCR server response contained no usable fields",
                    true);
            return;
        }

        callback.onSuccess(documentType, confidence, extracted);
    }

    @NonNull
    private String buildRequestJson(@NonNull Bitmap bitmap, @NonNull List<FieldSpec> spec) throws JSONException {
        JSONObject json = new JSONObject();
        json.put("image", bitmapToBase64(bitmap));

        JSONObject specJson = new JSONObject();
        specJson.put("screen", SCREEN_NAME);

        JSONArray fields = new JSONArray();
        for (FieldSpec field : spec) {
            JSONObject fieldJson = new JSONObject();
            fieldJson.put("id", field.getId());
            fieldJson.put("type", field.getType());
            fieldJson.put("controlType", field.getControlType());
            fieldJson.put("subType", field.getSubType());
            fields.put(fieldJson);
        }
        specJson.put("fields", fields);
        json.put("spec", specJson);

        return json.toString();
    }

    @NonNull
    private String bitmapToBase64(@NonNull Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        return Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP);
    }

    @NonNull
    private String postToServer(@NonNull String requestBody) throws IOException {
        validatePreconditions();

        HttpURLConnection connection = null;
        try {
            URL url = URI.create(serviceUrl).toURL();
            connection = (HttpURLConnection) url.openConnection();
            activeConnection = connection;
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");

            // Attach MOSIP authentication credentials
            if (authTokenProvider != null) {
                String token = authTokenProvider.getAuthToken();
                if (token != null && !token.trim().isEmpty()) {
                    connection.setRequestProperty("Cookie", "Authorization=" + token);
                    connection.setRequestProperty("Authorization", "Bearer " + token);
                }
            }

            connection.setDoOutput(true);
            connection.setConnectTimeout(socketTimeoutMs);
            connection.setReadTimeout(socketTimeoutMs);

            byte[] bodyBytes = requestBody.getBytes(StandardCharsets.UTF_8);
            try (OutputStream os = connection.getOutputStream()) {
                os.write(bodyBytes);
            }

            int responseCode = connection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                // Ensure sensitive request body and error response containing resident data are never logged or leaked
                throw new IOException("OCR server returned HTTP " + responseCode);
            }

            try (java.io.InputStream is = connection.getInputStream();
                 java.util.Scanner scanner = new java.util.Scanner(is, StandardCharsets.UTF_8.name())) {
                scanner.useDelimiter("\\A");
                return scanner.hasNext() ? scanner.next() : "";
            }
        } finally {
            activeConnection = null;
            if (connection != null) connection.disconnect();
        }
    }
}
