/*
 * Copyright (c) Modular Open Source Identity Platform
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.mosip.registration_client.ocr;

import androidx.annotation.NonNull;
import androidx.camera.core.ImageProxy;
import java.nio.ByteBuffer;

public class QualityAnalyzer {

    private static final int SAMPLE_STEP = 4;

    private final double blurThreshold;
    private final float  brightnessMin;
    private final float  brightnessMax;

    public QualityAnalyzer(double blurThreshold, float brightnessMin, float brightnessMax) {
        this.blurThreshold = blurThreshold;
        this.brightnessMin = brightnessMin;
        this.brightnessMax = brightnessMax;
    }

    public static class QualityResult {
        public final boolean isAcceptable;
        public final String  guidanceMessage;
        public final float   brightness;
        public final double  blurVariance;

        public QualityResult(boolean isAcceptable, String guidanceMessage, float brightness, double blurVariance) {
            this.isAcceptable    = isAcceptable;
            this.guidanceMessage = guidanceMessage;
            this.brightness      = brightness;
            this.blurVariance    = blurVariance;
        }
    }

    @NonNull
    public QualityResult analyze(@NonNull ImageProxy image) {
        ImageProxy.PlaneProxy yPlane = image.getPlanes()[0];
        ByteBuffer buffer = yPlane.getBuffer();

        QualityResult lightingResult = checkLighting(buffer, image.getWidth(), image.getHeight(), yPlane.getRowStride(), yPlane.getPixelStride());
        if (!lightingResult.isAcceptable) return lightingResult;

        buffer.rewind();
        QualityResult blurResult = checkBlur(buffer, image.getWidth(), image.getHeight(), yPlane.getRowStride(), yPlane.getPixelStride(), lightingResult.brightness);
        if (!blurResult.isAcceptable) return blurResult;

        return new QualityResult(true, "Hold steady", lightingResult.brightness, blurResult.blurVariance);
    }

    @NonNull
    private QualityResult checkLighting(ByteBuffer buffer, int width, int height, int rowStride, int pixelStride) {
        float total = 0f;
        int count = 0;

        for (int y = 0; y < height; y += SAMPLE_STEP) {
            for (int x = 0; x < width; x += SAMPLE_STEP) {
                int index = (y * rowStride) + (x * pixelStride);
                int luminance = buffer.get(index) & 0xFF;
                total += luminance;
                count++;
            }
        }

        float brightness = count > 0 ? total / count : 0f;

        if (brightness < brightnessMin) {
            return new QualityResult(false, "Too dark — move to better lighting", brightness, Double.NaN);
        }
        if (brightness > brightnessMax) {
            return new QualityResult(false, "Too bright — reduce glare", brightness, Double.NaN);
        }
        return new QualityResult(true, "Good", brightness, Double.NaN);
    }

    @NonNull
    private QualityResult checkBlur(ByteBuffer buffer, int width, int height, int rowStride, int pixelStride, float brightness) {
        if (width < 3 || height < 3) return new QualityResult(false, "Image too small", brightness, Double.NaN);

        double sum = 0.0;
        double sumSq = 0.0;
        int count = 0;

        for (int y = 1; y < height - 1; y += SAMPLE_STEP) {
            for (int x = 1; x < width - 1; x += SAMPLE_STEP) {
                // Laplacian kernel
                int center = getLuminance(buffer, x, y, rowStride, pixelStride);
                int top    = getLuminance(buffer, x, y - 1, rowStride, pixelStride);
                int bottom = getLuminance(buffer, x, y + 1, rowStride, pixelStride);
                int left   = getLuminance(buffer, x - 1, y, rowStride, pixelStride);
                int right  = getLuminance(buffer, x + 1, y, rowStride, pixelStride);

                double lap = (4.0 * center) - top - bottom - left - right;
                sum += lap;
                sumSq += lap * lap;
                count++;
            }
        }

        double mean = sum / count;
        double variance = (sumSq / count) - (mean * mean);

        if (variance < blurThreshold) {
            return new QualityResult(false, "Hold steady — image is blurry", brightness, variance);
        }
        return new QualityResult(true, "Good", brightness, variance);
    }

    private int getLuminance(ByteBuffer buffer, int x, int y, int rowStride, int pixelStride) {
        int index = (y * rowStride) + (x * pixelStride);
        return buffer.get(index) & 0xFF;
    }
}