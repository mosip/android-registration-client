/*
 * Copyright (c) Modular Open Source Identity Platform
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.mosip.registration_client.ocr.engine;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;

import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

public class MlKitEngine implements OcrEngine {

    public interface RichTextCallback {
        void onSuccess(@NonNull Text visionText, float avgConfidence);
        void onFailure(@NonNull String errorCode, @NonNull String message, boolean isRetryable);
    }
    private TextRecognizer recognizer;

    public MlKitEngine() {
        this.recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
    }

    @Override
    public void extractText(
            @NonNull Bitmap bitmap,
            @NonNull String fileType,
            @NonNull OcrEngineCallback callback) {

        extractRich(bitmap, new RichTextCallback() {
            @Override
            public void onSuccess(@NonNull Text visionText, float avgConfidence) {
                callback.onSuccess(visionText.getText().trim(), avgConfidence);
            }

            @Override
            public void onFailure(@NonNull String errorCode,
                                  @NonNull String message,
                                  boolean isRetryable) {
                callback.onFailure(errorCode, message, isRetryable);
            }
        });
    }

    public void extractRich(@NonNull Bitmap bitmap, @NonNull RichTextCallback callback) {
        if (bitmap.isRecycled()) {
            callback.onFailure(
                    "IMAGE_NULL",
                    "Bitmap was recycled before ML Kit could process it",
                    false);
            return;
        }

        TextRecognizer localRecognizer = recognizer;
        if (localRecognizer == null) {
            callback.onFailure(
                    "ML_KIT_FAILURE",
                    "ML Kit recognizer has been released",
                    false);
            return;
        }

        InputImage image = InputImage.fromBitmap(bitmap, 0);

        localRecognizer.process(image)
                .addOnSuccessListener(visionText -> {
                    String rawText = visionText.getText();
                    if (rawText == null || rawText.trim().isEmpty()) {
                        callback.onFailure(
                                "NO_TEXT_FOUND",
                                "ML Kit processed the image but found no text",
                                true);
                        return;
                    }
                    float confidence = calculateAverageConfidence(visionText);
                    callback.onSuccess(visionText, confidence);
                })
                .addOnFailureListener(e -> callback.onFailure(
                        "ML_KIT_FAILURE",
                        "ML Kit threw an exception: " + e.getMessage(),
                        false));
    }
    @Override
    public void release() {
        if (recognizer != null) {
            recognizer.close();
            recognizer = null;
        }
    }
    private float calculateAverageConfidence(@NonNull Text visionText) {
        float total = 0f;
        int   count = 0;

        for (Text.TextBlock block : visionText.getTextBlocks()) {
            for (Text.Line line : block.getLines()) {
                for (Text.Element element : line.getElements()) {
                    Float conf = element.getConfidence();
                    if (conf != null) {
                        total += conf;
                        count++;
                    }
                }
            }
        }

        return count > 0 ? total / count : 0f;
    }
}
