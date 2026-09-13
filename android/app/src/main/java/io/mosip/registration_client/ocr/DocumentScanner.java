/*
 * Copyright (c) Modular Open Source Identity Platform
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.mosip.registration_client.ocr;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.view.Surface;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.CameraState;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.core.SurfaceRequest;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import com.google.common.util.concurrent.ListenableFuture;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DocumentScanner {
    private static final long GOOD_QUALITY_HOLD_DURATION_MS = 4000L;
    private static final long UPDATE_THROTTLE_MS            = 200L;
    private static final long BAD_QUALITY_GRACE_MS         = 200L;

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
    private final SurfaceTexture           surfaceTexture;

    private ProcessCameraProvider                     cameraProvider;
    private androidx.camera.core.CameraInfo           cameraInfo;
    private androidx.lifecycle.Observer<CameraState>  cameraStateObserver;
    private volatile boolean                          isRunning              = false;
    private volatile boolean                          hasCaptured            = false;
    private long                                      goodQualityStart       = 0L;
    private int                                       badQualityStreak       = 0;
    private volatile boolean                          forceCaptureRequested  = false;
    private long                                      lastUpdateSentTime     = 0L;
    private QualityAnalyzer.QualityResult             lastSentQuality        = null;

    public DocumentScanner(
            @NonNull Activity activity,
            @Nullable SurfaceTexture surfaceTexture,
            @NonNull QualityAnalyzer qualityAnalyzer,
            int maxQualityRetries,
            @NonNull QualityUpdateCallback qualityCallback,
            @NonNull CaptureCallback captureCallback,
            @NonNull ErrorCallback errorCallback,
            @NonNull QualityExhaustedCallback qualityExhaustedCallback) {
        this.activity                 = activity;
        this.surfaceTexture           = surfaceTexture;
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
        goodQualityStart      = 0L;
        forceCaptureRequested = false;
        lastUpdateSentTime    = 0L;
        lastSentQuality       = null;
        qualityAnalyzer.resetFrameTracking();

        ListenableFuture<ProcessCameraProvider> cameraProviderFuture =
                ProcessCameraProvider.getInstance(activity);

        cameraProviderFuture.addListener(() -> {
            if (!isRunning) return;
            try {
                cameraProvider = cameraProviderFuture.get();
                bindCamera(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                if (isRunning) {
                    isRunning = false;
                    errorCallback.onError("Camera provider failed: " + e.getMessage());
                }
            }
        }, ContextCompat.getMainExecutor(activity));
    }

    public void stop() {
        boolean wasExhausted = badQualityStreak >= maxQualityRetries;
        isRunning = false;
        if (cameraInfo != null && cameraStateObserver != null) {
            final androidx.camera.core.CameraInfo info = cameraInfo;
            final androidx.lifecycle.Observer<CameraState> observer = cameraStateObserver;
            ContextCompat.getMainExecutor(activity).execute(() -> {
                try {
                    info.getCameraState().removeObserver(observer);
                } catch (Exception ignored) {}
            });
            cameraStateObserver = null;
            cameraInfo = null;
        }

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
        if (!isRunning) return;
        if (!(activity instanceof LifecycleOwner)) {
            isRunning = false;
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

        Preview preview = null;
        if (surfaceTexture != null) {
            preview = new Preview.Builder().build();
            final android.graphics.SurfaceTexture st = surfaceTexture;
            preview.setSurfaceProvider(request -> {
                android.util.Size resolution = request.getResolution();
                st.setDefaultBufferSize(resolution.getWidth(), resolution.getHeight());
                Surface surface = new Surface(st);
                request.provideSurface(surface, ContextCompat.getMainExecutor(activity), result -> {
                    surface.release();
                });
            });
        }

        try {
            provider.unbindAll();
            Camera camera;
            if (preview != null) {
                camera = provider.bindToLifecycle(
                        (LifecycleOwner) activity, cameraSelector, preview, imageAnalysis);
            } else {
                camera = provider.bindToLifecycle(
                        (LifecycleOwner) activity, cameraSelector, imageAnalysis);
            }

            this.cameraInfo = camera.getCameraInfo();
            this.cameraStateObserver = cameraState -> {
                if (!isRunning || cameraState == null) return;

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
            };

            cameraInfo.getCameraState().observe(
                    (LifecycleOwner) activity, cameraStateObserver);
        } catch (Exception e) {
            if (isRunning) {
                isRunning = false;
                errorCallback.onError("Failed to bind camera: " + e.getMessage());
            }
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
        long now = System.currentTimeMillis();

        // Dispatch throttled / state-change updates to Flutter
        dispatchQualityUpdateIfNeeded(quality, now);

        if (quality.isAcceptable) {
            badQualityStreak = 0;

            if (goodQualityStart == 0L) {
                goodQualityStart = now;
            } else if (now - goodQualityStart >= GOOD_QUALITY_HOLD_DURATION_MS) {
                OcrAuditLogger.logQualityAttempt(0, quality, false);
                capture(imageProxy);
                return;
            }
        } else {
            // Grace window for momentary sensor fluctuations while holding steady
            if (goodQualityStart != 0L) {
                if (now - goodQualityStart > BAD_QUALITY_GRACE_MS) {
                    goodQualityStart = 0L;
                }
            } else {
                badQualityStreak++;
                OcrAuditLogger.logClientFailure("quality_check_failed",
                        quality.guidanceMessage + " (attempt " + badQualityStreak + "/" + maxQualityRetries + ")");

                if (badQualityStreak == maxQualityRetries) {
                    qualityExhaustedCallback.onQualityRetriesExhausted();
                }
            }
        }

        imageProxy.close();
    }

    private void dispatchQualityUpdateIfNeeded(@NonNull QualityAnalyzer.QualityResult quality, long now) {
        boolean isStateChange = lastSentQuality == null
                || lastSentQuality.isAcceptable != quality.isAcceptable
                || !lastSentQuality.guidanceMessage.equals(quality.guidanceMessage);

        boolean isTimeElapsed = (now - lastUpdateSentTime) >= UPDATE_THROTTLE_MS;

        if (isStateChange || isTimeElapsed) {
            lastSentQuality = quality;
            lastUpdateSentTime = now;
            qualityCallback.onQualityUpdate(quality);
        }
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