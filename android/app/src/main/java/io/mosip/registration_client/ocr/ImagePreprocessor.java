/*
 * Copyright (c) Modular Open Source Identity Platform
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.mosip.registration_client.ocr;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;

import androidx.annotation.NonNull;

public final class ImagePreprocessor {

    private static final int   MIN_SHORT_SIDE    = 800;
    private static final float CONTRAST_BOOST    = 1.35f;
    private static final float BRIGHTNESS_ADJUST = -12f;
    private static final float MAX_DESKEW_ANGLE  = 10f;
    private static final float ANGLE_STEP        = 0.5f;
    private static final int   STRIPE_HEIGHT     = 300;

    private ImagePreprocessor() {}

    @NonNull
    public static Bitmap prepare(@NonNull Bitmap src) {
        Bitmap result = upscaleIfNeeded(src);
        result = toGrayscale(result);
        result = boostContrast(result);
        result = deskew(result);
        return result;
    }


    @NonNull
    private static Bitmap upscaleIfNeeded(@NonNull Bitmap src) {
        int shortSide = Math.min(src.getWidth(), src.getHeight());
        if (shortSide >= MIN_SHORT_SIDE) return src;
        float scale = (float) MIN_SHORT_SIDE / shortSide;
        int newW = Math.round(src.getWidth()  * scale);
        int newH = Math.round(src.getHeight() * scale);
        Bitmap scaled = Bitmap.createScaledBitmap(src, newW, newH, true);
        if (scaled != src) src.recycle();
        return scaled;
    }


    @NonNull
    private static Bitmap toGrayscale(@NonNull Bitmap src) {
        Bitmap result = Bitmap.createBitmap(src.getWidth(), src.getHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0f);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColorFilter(new ColorMatrixColorFilter(cm));
        canvas.drawBitmap(src, 0, 0, paint);
        if (result != src) src.recycle();
        return result;
    }


    @NonNull
    private static Bitmap boostContrast(@NonNull Bitmap src) {
        float c = CONTRAST_BOOST;
        float t = 0.5f * (1f - c) * 255f + BRIGHTNESS_ADJUST;
        ColorMatrix cm = new ColorMatrix(new float[]{
                c, 0, 0, 0, t,
                0, c, 0, 0, t,
                0, 0, c, 0, t,
                0, 0, 0, 1, 0
        });
        Bitmap result = Bitmap.createBitmap(src.getWidth(), src.getHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColorFilter(new ColorMatrixColorFilter(cm));
        canvas.drawBitmap(src, 0, 0, paint);
        if (result != src) src.recycle();
        return result;
    }


    @NonNull
    private static Bitmap deskew(@NonNull Bitmap src) {
        float angle = detectSkewAngle(src);
        if (Math.abs(angle) < 0.5f) return src; // skip trivial rotation

        Matrix matrix = new Matrix();
        matrix.postRotate(-angle, src.getWidth() / 2f, src.getHeight() / 2f);
        Bitmap rotated = Bitmap.createBitmap(
                src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
        if (rotated != src) src.recycle();
        return rotated;
    }

    private static float detectSkewAngle(@NonNull Bitmap src) {
        int w = src.getWidth();
        int h = src.getHeight();

        int stripeH  = Math.min(h, STRIPE_HEIGHT);
        int startY   = (h - stripeH) / 2;
        int[] pixels = new int[w * stripeH];
        src.getPixels(pixels, 0, w, 0, startY, w, stripeH);

        float  bestAngle    = 0f;
        double bestVariance = -1.0;

        for (float angle = -MAX_DESKEW_ANGLE;
             angle <= MAX_DESKEW_ANGLE;
             angle += ANGLE_STEP) {

            double var = projectionVariance(pixels, w, stripeH, angle);
            if (var > bestVariance) {
                bestVariance = var;
                bestAngle    = angle;
            }
        }

        return bestAngle;
    }

    private static double projectionVariance(
            @NonNull int[] pixels, int w, int h, float angleDeg) {

        double angleRad = Math.toRadians(angleDeg);
        double cosA     = Math.cos(angleRad);
        double sinA     = Math.sin(angleRad);
        int    cx       = w / 2;
        int    cy       = h / 2;

        long[] rowSums   = new long[h];
        int[]  rowCounts = new int[h];

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                double ry    = -(x - cx) * sinA + (y - cy) * cosA;
                int    newY  = (int) (ry + cy);
                if (newY < 0 || newY >= h) continue;

                int luma = (pixels[y * w + x] >> 16) & 0xFF;
                rowSums[newY]  += luma;
                rowCounts[newY]++;
            }
        }

        double mean      = 0;
        int    validRows = 0;
        for (int i = 0; i < h; i++) {
            if (rowCounts[i] > 0) {
                mean += (double) rowSums[i] / rowCounts[i];
                validRows++;
            }
        }
        if (validRows == 0) return 0;
        mean /= validRows;

        // Variance
        double variance = 0;
        for (int i = 0; i < h; i++) {
            if (rowCounts[i] > 0) {
                double diff = (double) rowSums[i] / rowCounts[i] - mean;
                variance += diff * diff;
            }
        }
        return variance / validRows;
    }
}
