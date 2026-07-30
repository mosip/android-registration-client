/*
 * Copyright (c) Modular Open Source Identity Platform
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.mosip.registration_client.ocr;

import android.app.Activity;
import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.CameraState;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import com.google.common.util.concurrent.ListenableFuture;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DocumentScanner {
    private static final long GOOD_QUALITY_HOLD_DURATION_MS = 1500L;

    public interface QualityUpdateCallback {
        void onQualityUpdate(QualityAnalyzer.QualityResult result);
    }

    public interface CaptureCallback {
        void onCapture(@NonNull Bitmap bitmap);
    }

    public interface ErrorCallback {
        void onError(@NonNull String message);
    }

    public interface QualityExhaustedCallback {
        void onQualityRetriesExhausted();
    }

    private final Activity                 activity;
    private final QualityUpdateCallback    qualityCallback;
    private final CaptureCallback          captureCallback;
    private final ErrorCallback            errorCallback;
    private final QualityExhaustedCallback qualityExhaustedCallback;
    private final QualityAnalyzer          qualityAnalyzer;
    private final int                      maxQualityRetries;
    private final ExecutorService          analysisExecutor;

    private ProcessCameraProvider cameraProvider;
    private volatile boolean      isRunning              = false;
    private volatile boolean      hasCaptured            = false;
    private long                  goodQualityStart       = 0L;
    private int                   badQualityStreak       = 0;
    private volatile boolean      forceCaptureRequested  = false;

    public DocumentScanner(
            @NonNull Activity activity,
            @NonNull QualityAnalyzer qualityAnalyzer,
            int maxQualityRetries,
            @NonNull QualityUpdateCallback qualityCallback,
            @NonNull CaptureCallback captureCallback,
            @NonNull ErrorCallback errorCallback,
            @NonNull QualityExhaustedCallback qualityExhaustedCallback) {
        this.activity                 = activity;
        this.qualityAnalyzer          = qualityAnalyzer;
        this.maxQualityRetries        = maxQualityRetries;
        this.qualityCallback          = qualityCallback;
        this.captureCallback          = captureCallback;
        this.errorCallback            = errorCallback;
        this.qualityExhaustedCallback = qualityExhaustedCallback;
        this.analysisExecutor         = Executors.newSingleThreadExecutor();
    }

    public void start() {
        if (isRunning) return;
        isRunning             = true;
        hasCaptured           = false;
        badQualityStreak       = 0;
        forceCaptureRequested = false;

        ListenableFuture<ProcessCameraProvider> cameraProviderFuture =
                ProcessCameraProvider.getInstance(activity);

        cameraProviderFuture.addListener(() -> {
            try {
                cameraProvider = cameraProviderFuture.get();
                bindCamera(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                errorCallback.onError("Camera provider failed: " + e.getMessage());
            }
        }, ContextCompat.getMainExecutor(activity));
    }

    public void stop() {
        boolean wasExhausted = badQualityStreak >= maxQualityRetries;
        isRunning = false;
        if (cameraProvider != null) {
            cameraProvider.unbindAll();
            cameraProvider = null;
        }
        analysisExecutor.shutdownNow();
        if (wasExhausted && !hasCaptured) {
            OcrAuditLogger.logClientFailure("quality_check_exhausted",
                    "Operator cancelled after " + maxQualityRetries + " failed quality checks");
        }
    }

    public void forceCapture() {
        forceCaptureRequested = true;
    }

    private void bindCamera(@NonNull ProcessCameraProvider provider) {
        if (!(activity instanceof LifecycleOwner)) {
            errorCallback.onError("Activity must be a LifecycleOwner to use CameraX");
            return;
        }

        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build();

        imageAnalysis.setAnalyzer(analysisExecutor, this::analyzeFrame);

        try {
            provider.unbindAll();
            Camera camera = provider.bindToLifecycle(
                    (LifecycleOwner) activity, cameraSelector, imageAnalysis);

            // R-1: observe CameraState for hardware disconnects / errors
            camera.getCameraInfo().getCameraState().observe(
                    (LifecycleOwner) activity, cameraState -> {
                        if (cameraState == null) return;

                        CameraState.StateError error = cameraState.getError();
                        if (error != null) {
                            String reason;
                            switch (error.getCode()) {
                                case CameraState.ERROR_CAMERA_IN_USE:
                                    reason = "Camera is in use by another application";
                                    break;
                                case CameraState.ERROR_CAMERA_FATAL_ERROR:
                                    reason = "Fatal camera error — device may need restart";
                                    break;
                                case CameraState.ERROR_DO_NOT_DISTURB_MODE_ENABLED:
                                    reason = "Do-not-disturb mode is blocking camera";
                                    break;
                                default:
                                    reason = "Camera error (code " + error.getCode() + ")";
                                    break;
                            }
                            if (isRunning) {
                                isRunning = false;
                                errorCallback.onError(reason);
                            }
                        }

                        if (cameraState.getType() == CameraState.Type.CLOSED && isRunning) {
                            isRunning = false;
                            errorCallback.onError("Camera was unexpectedly closed");
                        }
                    });
        } catch (Exception e) {
            errorCallback.onError("Failed to bind camera: " + e.getMessage());
        }
    }

    private void analyzeFrame(@NonNull ImageProxy imageProxy) {
        if (!isRunning || hasCaptured) {
            imageProxy.close();
            return;
        }

        if (forceCaptureRequested) {
            QualityAnalyzer.QualityResult quality = qualityAnalyzer.analyze(imageProxy);
            OcrAuditLogger.logQualityAttempt(badQualityStreak + 1, quality, true);
            capture(imageProxy);
            return;
        }

        QualityAnalyzer.QualityResult quality = qualityAnalyzer.analyze(imageProxy);

        qualityCallback.onQualityUpdate(quality);

        if (quality.isAcceptable) {
            badQualityStreak = 0;

            long now = System.currentTimeMillis();
            if (goodQualityStart == 0L) {
                goodQualityStart = now;
            } else if (now - goodQualityStart >= GOOD_QUALITY_HOLD_DURATION_MS) {
                OcrAuditLogger.logQualityAttempt(0, quality, false);
                capture(imageProxy);
                return;
            }
        } else {
            goodQualityStart = 0L;
            badQualityStreak++;
            OcrAuditLogger.logClientFailure("quality_check_failed",
                    quality.guidanceMessage + " (attempt " + badQualityStreak + "/" + maxQualityRetries + ")");

            if (badQualityStreak == maxQualityRetries) {
                // Notify once per streak, then wait for the operator
                // (forceCapture() or stop()) — don't keep re-firing every
                // frame or auto-fail/auto-capture on their behalf.
                qualityExhaustedCallback.onQualityRetriesExhausted();
            }
        }

        imageProxy.close();
    }

    private void capture(@NonNull ImageProxy imageProxy) {
        hasCaptured = true;
        forceCaptureRequested = false;

        Bitmap finalBitmap = BitmapUtils.fromImageProxy(imageProxy);
        imageProxy.close();

        if (finalBitmap != null) {
            captureCallback.onCapture(finalBitmap);
        } else {
            errorCallback.onError("Failed to capture frame.");
        }
    }
}