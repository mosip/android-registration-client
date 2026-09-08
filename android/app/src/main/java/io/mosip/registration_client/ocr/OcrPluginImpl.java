/*
 * Copyright (c) Modular Open Source Identity Platform
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.mosip.registration_client.ocr;

import android.util.Log;
import android.app.Activity;
import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.SurfaceTexture;
import android.net.Uri;
import io.flutter.view.TextureRegistry;
import android.os.Handler;
import android.os.Looper;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import io.flutter.embedding.engine.FlutterEngine;
import io.mosip.registration.clientmanager.repository.GlobalParamRepository;
import io.mosip.registration_client.ocr.engine.DocumentClassifier;
import io.mosip.registration_client.ocr.engine.OcrProvider;
import io.mosip.registration_client.ocr.engine.OcrProviderFactory;
import io.mosip.registration_client.ocr.extraction.ExtractionConfig;
import io.mosip.registration_client.ocr.extraction.OcrUiSpecProvider;
import io.mosip.registration_client.ocr.models.FieldSpec;
import io.mosip.registration_client.ocr.models.OcrError;
import io.mosip.registration_client.ocr.models.OcrErrorCode;
import io.mosip.registration_client.ocr.OcrPluginApi.OcrHostApi;
import io.mosip.registration_client.ocr.OcrPluginApi.OcrFlutterApi;
import io.mosip.registration_client.ocr.OcrPluginApi.ImageQualityMessage;
import io.mosip.registration_client.ocr.OcrPluginApi.OcrResultMessage;
import io.mosip.registration_client.ocr.OcrPluginApi.OcrErrorMessage;

public class OcrPluginImpl implements OcrHostApi {

    private static final String TAG = "OCR_DEBUG";

    private volatile Activity       activity;
    private final OcrFlutterApi     flutterApi;
    private final OcrProvider       provider;
    private final Handler           mainThreadHandler;
    private final OcrConfig         config;
    private final OcrUiSpecProvider uiSpecProvider;
    private final AtomicInteger     scanGeneration = new AtomicInteger(0);
    @Nullable
    private DocumentScanner activeScanner;
    private final TextureRegistry textureRegistry;
    @Nullable
    private TextureRegistry.SurfaceTextureEntry textureEntry;
    private final ExecutorService fileDecodeExecutor = Executors.newSingleThreadExecutor();

    private OcrPluginImpl(
            @NonNull Activity activity,
            @NonNull OcrFlutterApi flutterApi,
            @NonNull OcrProvider provider,
            @NonNull OcrConfig config,
            @NonNull OcrUiSpecProvider uiSpecProvider,
            @NonNull TextureRegistry textureRegistry) {
        this.activity          = activity;
        this.flutterApi        = flutterApi;
        this.provider           = provider;
        this.mainThreadHandler = new Handler(Looper.getMainLooper());
        this.config            = config;
        this.uiSpecProvider    = uiSpecProvider;
        this.textureRegistry   = textureRegistry;
    }
    public static void register(
            @NonNull FlutterEngine flutterEngine,
            @NonNull Activity activity,
            @NonNull GlobalParamRepository globalParamRepository) {

        OcrFlutterApi flutterApi = new OcrFlutterApi(
                flutterEngine.getDartExecutor().getBinaryMessenger());

        // OcrTestDataSeeder.seedIfEmpty(globalParamRepository);  //ONLY FOR TESTING

        OcrConfig config = OcrConfig.from(globalParamRepository);
        OcrUiSpecProvider uiSpecProvider = new OcrUiSpecProvider(globalParamRepository);
        ExtractionConfig extractionConfig = new ExtractionConfig(globalParamRepository);

        DocumentClassifier documentClassifier = null;
        try {
            documentClassifier = new DocumentClassifier(activity.getApplicationContext());
        } catch (IOException e) {
            Log.e(TAG, "Failed initializing DocumentClassifier (only affects mlkit provider)", e);
        }

        OcrProvider provider = OcrProviderFactory.create(config, documentClassifier, extractionConfig);

        TextureRegistry textureRegistry = flutterEngine.getRenderer();

        OcrPluginImpl impl = new OcrPluginImpl(activity, flutterApi, provider, config, uiSpecProvider, textureRegistry);
        OcrHostApi.setup(flutterEngine.getDartExecutor().getBinaryMessenger(), impl);

        // Log UiSpec once on app startup
        List<FieldSpec> startupSpec = uiSpecProvider.getSpec();
        Log.i(TAG, "OCR UiSpec at startup: " + startupSpec.size() + " fields");
        for (FieldSpec f : startupSpec) {
            Log.d(TAG, "  field: id=" + f.getId() + " type=" + f.getType()
                    + " controlType=" + f.getControlType() + " subType=" + f.getSubType());
        }
    }

    public void updateActivity(@NonNull Activity activity) {
        this.activity = activity;
    }

    @NonNull
    @Override
    public Long startDocumentScan() {
        if (!config.enabled) {
            Log.w(TAG, "startDocumentScan called while mosip.registration.ocr.enabled=false, ignoring");
            return -1L;
        }

        if (activity == null) {
            Log.e(TAG, "startDocumentScan: activity is null (possibly detached), ignoring");
            return -1L;
        }

        cancelScan();

        textureEntry = textureRegistry.createSurfaceTexture();
        SurfaceTexture surfaceTexture = textureEntry.surfaceTexture();

        QualityAnalyzer qualityAnalyzer = new QualityAnalyzer(
                config.qualityBlurVariance, config.qualityBrightnessMin, config.qualityBrightnessMax);

        activeScanner = new DocumentScanner(
                activity,
                surfaceTexture,
                qualityAnalyzer,
                config.qualityMaxRetries,

                quality -> {
                    ImageQualityMessage msg = new ImageQualityMessage();
                    msg.setIsAcceptable(quality.isAcceptable);
                    msg.setGuidanceMessage(quality.guidanceMessage);
                    mainThreadHandler.post(() ->
                            flutterApi.onQualityUpdate(msg, reply -> {}));
                },

                bitmap -> runOcr(bitmap),

                errorMessage -> {
                    OcrAuditLogger.logClientFailure("camera_error", errorMessage);
                    OcrErrorMessage msg = buildErrorMessage(
                            OcrError.ErrorCode.CAMERA_ERROR.name(),
                            errorMessage,
                            false);
                    mainThreadHandler.post(() ->
                            flutterApi.onOcrError(msg, reply -> {}));
                },

                () -> {
                    ImageQualityMessage msg = new ImageQualityMessage();
                    msg.setIsAcceptable(false);
                    msg.setGuidanceMessage("Quality retry limit reached — capture anyway or cancel");
                    mainThreadHandler.post(() ->
                            flutterApi.onQualityUpdate(msg, reply -> {}));
                }
        );

        activeScanner.start();
        return textureEntry.id();
    }

    @Override
    public void cancelScan() {
        if (activeScanner != null) {
            activeScanner.stop();
            activeScanner = null;
        }
        if (textureEntry != null) {
            textureEntry.release();
            textureEntry = null;
        }
    }

    @Override
    public void forceCapture() {
        if (activeScanner != null) {
            activeScanner.forceCapture();
        }
    }

    public void release() {
        if (activeScanner != null) {
            activeScanner.stop();
            activeScanner = null;
        }
        if (textureEntry != null) {
            textureEntry.release();
            textureEntry = null;
        }
        fileDecodeExecutor.shutdownNow();
        provider.release();
    }

    @NonNull
    @Override
    public ImageQualityMessage checkImageQuality() {
        ImageQualityMessage msg = new ImageQualityMessage();
        msg.setIsAcceptable(false);
        msg.setGuidanceMessage("No active scan");
        return msg;
    }

    @Override
    public void processImageFile(@NonNull String filePath) {
        if (!config.enabled) {
            Log.w(TAG, "processImageFile: ocr.enabled=false, ignoring");
            return;
        }
        if (activity == null) {
            Log.e(TAG, "processImageFile: activity is null, ignoring");
            return;
        }

        // Cancel any active camera session first
        cancelScan();

        fileDecodeExecutor.execute(() -> {
            Bitmap bitmap = null;
            try {
                bitmap = decodeImagePath(filePath);
            } catch (Exception e) {
                Log.e(TAG, "processImageFile: decode failed for " + filePath, e);
            }

            if (bitmap == null || bitmap.isRecycled()) {
                OcrAuditLogger.logClientFailure("image_decode_failed",
                        "Could not decode image: " + filePath);
                OcrErrorMessage msg = buildErrorMessage(
                        "IMAGE_DECODE_FAILED",
                        "Could not decode the selected image. Please choose a valid JPG, PNG, or WebP file.",
                        true);
                mainThreadHandler.post(() -> flutterApi.onOcrError(msg, r -> {}));
                return;
            }

            // Downsample very large images (cap longest side at 2048px) before OCR
            bitmap = downsampleIfNeeded(bitmap, 2048);

            // Feed into the shared OCR pipeline (same as camera capture path)
            runOcr(bitmap);
        });
    }

    /**
     * Decodes a file-system path or content:// URI to a Bitmap.
     * Handles both SAF URIs and absolute file paths.
     */
    private Bitmap decodeImagePath(@NonNull String filePath) throws IOException {
        if (filePath.startsWith("content://")) {
            ContentResolver cr = activity.getContentResolver();
            try (InputStream is = cr.openInputStream(Uri.parse(filePath))) {
                if (is == null) throw new IOException("ContentResolver returned null stream for: " + filePath);
                return BitmapFactory.decodeStream(is);
            }
        } else {
            return BitmapFactory.decodeFile(filePath);
        }
    }

    /**
     * Downsamples a Bitmap so its longest side is at most {@code maxPx}.
     * Returns the original if already within bounds.
     */
    private static Bitmap downsampleIfNeeded(@NonNull Bitmap src, int maxPx) {
        int w = src.getWidth();
        int h = src.getHeight();
        if (w <= maxPx && h <= maxPx) return src;
        float scale = (float) maxPx / Math.max(w, h);
        int newW = Math.round(w * scale);
        int newH = Math.round(h * scale);
        Bitmap scaled = Bitmap.createScaledBitmap(src, newW, newH, true);
        src.recycle();
        return scaled;
    }

    private void runOcr(@NonNull Bitmap bitmap) {
        final int thisGeneration = scanGeneration.incrementAndGet();

        Log.d(TAG, "runOcr: starting gen=" + thisGeneration + ", bitmap valid=" + !bitmap.isRecycled());
        mainThreadHandler.post(() -> {
            if (activeScanner != null) {
                activeScanner.stop();
                activeScanner = null;
            }
        });

        ImageQualityMessage processingMsg = new ImageQualityMessage();
        processingMsg.setIsAcceptable(true);
        processingMsg.setGuidanceMessage("Reading document...");
        mainThreadHandler.post(() ->
                flutterApi.onQualityUpdate(processingMsg, reply -> {}));

        List<FieldSpec> spec = uiSpecProvider.getSpec();

        provider.extract(bitmap, spec, new OcrProvider.OcrProviderCallback() {

            @Override
            public void onSuccess(@NonNull String documentType, float confidence, @NonNull Map<String, String> data) {
                if (scanGeneration.get() != thisGeneration) {
                    Log.w(TAG, "onSuccess: stale generation " + thisGeneration + ", dropping");
                    mainThreadHandler.post(bitmap::recycle);
                    return;
                }
                Log.d(TAG, "onSuccess: documentType=" + documentType + " fields=" + data.size());
                OcrAuditLogger.logSuccess(documentType, data.size());

                OcrResultMessage msg = new OcrResultMessage();
                msg.setDocumentType(documentType);
                msg.setConfidence((double) confidence);
                msg.setData(data);

                mainThreadHandler.post(() ->
                        flutterApi.onOcrSuccess(msg, reply ->
                                Log.d(TAG, "onSuccess: flutter replied")));
                mainThreadHandler.post(bitmap::recycle);
            }

            @Override
            public void onProviderError(@NonNull String errorCode, @Nullable String errorInfo) {
                if (scanGeneration.get() != thisGeneration) {
                    Log.w(TAG, "onProviderError: stale generation " + thisGeneration + ", dropping");
                    mainThreadHandler.post(bitmap::recycle);
                    return;
                }
                Log.d(TAG, "onProviderError: errorCode=" + errorCode + " errorInfo=" + errorInfo);
                String message = errorInfo != null ? errorInfo : "Provider returned errorCode " + errorCode;
                OcrAuditLogger.logProviderError(errorCode, message);

                OcrErrorMessage msg = buildErrorMessage(
                        errorCode,
                        message,
                        OcrErrorCode.isRetryable(errorCode));
                mainThreadHandler.post(() -> flutterApi.onOcrError(msg, reply -> {}));
                mainThreadHandler.post(bitmap::recycle);
            }

            @Override
            public void onFailure(@NonNull String errorCode, @NonNull String message, boolean isRetryable) {
                if (scanGeneration.get() != thisGeneration) {
                    Log.w(TAG, "onFailure: stale generation " + thisGeneration + ", dropping");
                    mainThreadHandler.post(bitmap::recycle);
                    return;
                }
                Log.d(TAG, "onFailure: errorCode=" + errorCode + " message=" + message);
                OcrAuditLogger.logClientFailure(errorCode, message);

                OcrErrorMessage msg = buildErrorMessage(errorCode, message, isRetryable);
                mainThreadHandler.post(() -> flutterApi.onOcrError(msg, reply -> {}));
                mainThreadHandler.post(bitmap::recycle);
            }
        });
    }

    @NonNull
    private OcrErrorMessage buildErrorMessage(
            @NonNull String errorCode,
            @NonNull String message,
            boolean isRetryable) {
        OcrErrorMessage msg = new OcrErrorMessage();
        msg.setErrorCode(errorCode);
        msg.setMessage(message);
        msg.setIsRetryable(isRetryable);
        return msg;
    }
}