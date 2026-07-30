/*
 * Copyright (c) Modular Open Source Identity Platform
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.mosip.registration_client.ocr;

import android.util.Log;
import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

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


    private OcrPluginImpl(
            @NonNull Activity activity,
            @NonNull OcrFlutterApi flutterApi,
            @NonNull OcrProvider provider,
            @NonNull OcrConfig config,
            @NonNull OcrUiSpecProvider uiSpecProvider) {
        this.activity          = activity;
        this.flutterApi        = flutterApi;
        this.provider           = provider;
        this.mainThreadHandler = new Handler(Looper.getMainLooper());
        this.config            = config;
        this.uiSpecProvider    = uiSpecProvider;
    }
    public static void register(
            @NonNull FlutterEngine flutterEngine,
            @NonNull Activity activity,
            @NonNull GlobalParamRepository globalParamRepository) {

        OcrFlutterApi flutterApi = new OcrFlutterApi(
                flutterEngine.getDartExecutor().getBinaryMessenger());

        OcrTestDataSeeder.seedIfEmpty(globalParamRepository);  //ONLY FOR TESTING

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

        OcrHostApi.setup(
                flutterEngine.getDartExecutor().getBinaryMessenger(),
                new OcrPluginImpl(activity, flutterApi, provider, config, uiSpecProvider));
    }

    public void updateActivity(@NonNull Activity activity) {
        this.activity = activity;
    }

    @Override
    public void startDocumentScan() {
        if (!config.enabled) {
            Log.w(TAG, "startDocumentScan called while mosip.registration.ocr.enabled=false, ignoring");
            return;
        }

        if (activity == null) {
            Log.e(TAG, "startDocumentScan: activity is null (possibly detached), ignoring");
            return;
        }

        cancelScan();

        QualityAnalyzer qualityAnalyzer = new QualityAnalyzer(
                config.qualityBlurVariance, config.qualityBrightnessMin, config.qualityBrightnessMax);

        activeScanner = new DocumentScanner(
                activity,
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
    }

    @Override
    public void cancelScan() {
        if (activeScanner != null) {
            activeScanner.stop();
            activeScanner = null;
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