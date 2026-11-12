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

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import io.mosip.registration_client.ocr.models.FieldSpec;
import io.mosip.registration_client.ocr.models.OcrError;

public class TimeoutEnforcingOcrProvider implements OcrProvider {

    private static final long LEAK_PREVENTION_DELAY_MS = 10000L;

    private final OcrProvider delegate;
    private final int timeoutMs;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Set<Bitmap> retainedBitmaps = Collections.newSetFromMap(new ConcurrentHashMap<>());

    public TimeoutEnforcingOcrProvider(@NonNull OcrProvider delegate, int timeoutMs) {
        this.delegate = delegate;
        this.timeoutMs = timeoutMs > 0 ? timeoutMs : 10000;
    }

    @Override
    public void extract(
            @NonNull Bitmap bitmap,
            @NonNull List<FieldSpec> spec,
            @NonNull OcrProviderCallback callback) {

        retainedBitmaps.add(bitmap);
        AtomicBoolean completed = new AtomicBoolean(false);

        Runnable timeoutRunnable = () -> {
            if (completed.compareAndSet(false, true)) {
                delegate.cancel();
                callback.onFailure(
                        OcrError.ErrorCode.TIMEOUT.name(),
                        "OCR provider did not respond within " + timeoutMs + "ms",
                        true);

                // Backstop against hung work: if delegate never reports stopping, recycle after grace period
                handler.postDelayed(() -> {
                    if (retainedBitmaps.remove(bitmap)) {
                        recycleBitmapSafely(bitmap);
                    }
                }, LEAK_PREVENTION_DELAY_MS);
            }
        };
        handler.postDelayed(timeoutRunnable, timeoutMs);

        delegate.extract(bitmap, spec, new OcrProviderCallback() {
            @Override
            public void onSuccess(@NonNull String documentType, float confidence, @NonNull Map<String, String> data) {
                handler.removeCallbacks(timeoutRunnable);
                if (completed.compareAndSet(false, true)) {
                    retainedBitmaps.remove(bitmap);
                    callback.onSuccess(documentType, confidence, data);
                } else {
                    // Late completion after timeout: clean up the retained bitmap safely
                    if (retainedBitmaps.remove(bitmap)) {
                        recycleBitmapSafely(bitmap);
                    }
                }
            }

            @Override
            public void onProviderError(@NonNull String errorCode, @Nullable String errorInfo) {
                handler.removeCallbacks(timeoutRunnable);
                if (completed.compareAndSet(false, true)) {
                    retainedBitmaps.remove(bitmap);
                    callback.onProviderError(errorCode, errorInfo);
                } else {
                    if (retainedBitmaps.remove(bitmap)) {
                        recycleBitmapSafely(bitmap);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull String errorCode, @NonNull String message, boolean isRetryable) {
                handler.removeCallbacks(timeoutRunnable);
                if (completed.compareAndSet(false, true)) {
                    retainedBitmaps.remove(bitmap);
                    callback.onFailure(errorCode, message, isRetryable);
                } else {
                    if (retainedBitmaps.remove(bitmap)) {
                        recycleBitmapSafely(bitmap);
                    }
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
        for (Bitmap b : retainedBitmaps) {
            recycleBitmapSafely(b);
        }
        retainedBitmaps.clear();
    }

    @Override
    public void release() {
        cancel();
        delegate.release();
    }
}
