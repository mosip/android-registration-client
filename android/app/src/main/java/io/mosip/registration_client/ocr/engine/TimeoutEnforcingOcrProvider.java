/*
 * Copyright (c) Modular Open Source Identity Platform
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.mosip.registration_client.ocr.engine;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import io.mosip.registration_client.ocr.models.FieldSpec;
import io.mosip.registration_client.ocr.models.OcrError;

public class TimeoutEnforcingOcrProvider implements OcrProvider {

    private final OcrProvider delegate;
    private final int timeoutMs;
    private final Handler handler = new Handler(Looper.getMainLooper());

    public TimeoutEnforcingOcrProvider(@NonNull OcrProvider delegate, int timeoutMs) {
        this.delegate = delegate;
        this.timeoutMs = timeoutMs > 0 ? timeoutMs : 10000;
    }

    @Override
    public void extract(
            @NonNull Bitmap bitmap,
            @NonNull List<FieldSpec> spec,
            @NonNull OcrProviderCallback callback) {

        AtomicBoolean completed = new AtomicBoolean(false);

        Runnable timeoutRunnable = () -> {
            if (completed.compareAndSet(false, true)) {
                delegate.cancel();
                callback.onFailure(
                        OcrError.ErrorCode.TIMEOUT.name(),
                        "OCR provider did not respond within " + timeoutMs + "ms",
                        true);
                // Safety fallback: if delegate never calls back after cancel, recycle bitmap after grace period
                handler.postDelayed(() -> recycleBitmapSafely(bitmap), 3000);
            }
        };
        handler.postDelayed(timeoutRunnable, timeoutMs);

        delegate.extract(bitmap, spec, new OcrProviderCallback() {
            @Override
            public void onSuccess(@NonNull String documentType, float confidence, @NonNull Map<String, String> data) {
                handler.removeCallbacks(timeoutRunnable);
                if (completed.compareAndSet(false, true)) {
                    callback.onSuccess(documentType, confidence, data);
                } else {
                    // Late completion after timeout: clean up the retained bitmap safely
                    recycleBitmapSafely(bitmap);
                }
            }

            @Override
            public void onProviderError(@NonNull String errorCode, @Nullable String errorInfo) {
                handler.removeCallbacks(timeoutRunnable);
                if (completed.compareAndSet(false, true)) {
                    callback.onProviderError(errorCode, errorInfo);
                } else {
                    recycleBitmapSafely(bitmap);
                }
            }

            @Override
            public void onFailure(@NonNull String errorCode, @NonNull String message, boolean isRetryable) {
                handler.removeCallbacks(timeoutRunnable);
                if (completed.compareAndSet(false, true)) {
                    callback.onFailure(errorCode, message, isRetryable);
                } else {
                    recycleBitmapSafely(bitmap);
                }
            }
        });
    }

    private void recycleBitmapSafely(@Nullable Bitmap bitmap) {
        if (bitmap != null) {
            handler.post(() -> {
                try {
                    if (!bitmap.isRecycled()) {
                        bitmap.recycle();
                    }
                } catch (Exception ignored) { }
            });
        }
    }

    @Override
    public void cancel() {
        handler.removeCallbacksAndMessages(null);
        delegate.cancel();
    }

    @Override
    public void release() {
        cancel();
        delegate.release();
    }
}
