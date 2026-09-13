/*
 * Copyright (c) Modular Open Source Identity Platform
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.mosip.registration_client.ocr.engine;

import android.graphics.Bitmap;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.mlkit.vision.text.Text;

import java.util.List;
import java.util.Map;

import io.mosip.registration_client.ocr.ImagePreprocessor;
import io.mosip.registration_client.ocr.extraction.DemographicFieldExtractor;
import io.mosip.registration_client.ocr.extraction.ExtractionConfig;
import io.mosip.registration_client.ocr.models.FieldSpec;
import io.mosip.registration_client.ocr.models.OcrError;
import io.mosip.registration_client.ocr.models.OcrErrorCode;


public class MlKitOcrProvider implements OcrProvider {

    private static final String TAG         = "MlKitOcrProvider";
    private static final String RAW_LOG_TAG = "OCR_RAW_TEXT";

    @Nullable
    private final DocumentClassifier     documentClassifier;
    private final MlKitEngine            mlKitEngine;
    private final DemographicFieldExtractor fieldExtractor;

    public MlKitOcrProvider(@Nullable DocumentClassifier documentClassifier,
                             @NonNull ExtractionConfig extractionConfig) {
        this.documentClassifier = documentClassifier;
        this.mlKitEngine        = new MlKitEngine();
        this.fieldExtractor     = new DemographicFieldExtractor(extractionConfig);
    }

    // ── OcrProvider

    @Override
    public void extract(
            @NonNull Bitmap bitmap,
            @NonNull List<FieldSpec> spec,
            @NonNull OcrProviderCallback callback) {

        // Document classification on the ORIGINAL color bitmap (TFLite model expects RGB)
        String documentType           = "UNKNOWN";
        float  classificationConfidence = 0f;

        if (documentClassifier != null) {
            try {
                DocumentClassifier.ClassificationResult result =
                        documentClassifier.classify(bitmap);
                if (result != null) {
                    documentType            = result.documentType;
                    classificationConfidence = result.confidence;
                }
            } catch (Exception e) {
                Log.w(TAG, "DocumentClassifier threw, continuing with UNKNOWN type", e);
            }
        }

        // Image pre-processing (grayscale + contrast boost + deskew) for ML Kit
        Bitmap processed;
        try {
            processed = ImagePreprocessor.prepare(bitmap);
        } catch (Exception e) {
            Log.w(TAG, "ImagePreprocessor failed, falling back to raw bitmap", e);
            processed = bitmap;
        }

        final String finalDocumentType   = documentType;
        final float  finalConfidence     = classificationConfidence;
        final Bitmap finalProcessed      = processed;

        // ML Kit text recognition
        mlKitEngine.extractRich(finalProcessed, new MlKitEngine.RichTextCallback() {

            @Override
            public void onSuccess(@NonNull Text visionText, float avgConfidence) {

                // ── RAW TEXT DEBUG ────────────────────────────────────────────
                Log.d(RAW_LOG_TAG,
                        "══════════ RAW OCR TEXT (avgConf=" + avgConfidence + ") ══════════\n"
                        + visionText.getText()
                        + "\n══════════════════════════════════════════════════════");

                Map<String, String> data;
                try {
                    data = fieldExtractor.extract(visionText, finalDocumentType, spec);
                } catch (Exception e) {
                    Log.e(TAG, "Field extraction threw unexpectedly", e);
                    callback.onFailure(
                            OcrError.ErrorCode.NO_FIELDS_EXTRACTED.name(),
                            "Extraction pipeline error: " + e.getMessage(),
                            true);
                    return;
                }

                if (data.isEmpty()) {
                    callback.onFailure(
                            OcrError.ErrorCode.NO_FIELDS_EXTRACTED.name(),
                            "OCR text was read but no spec fields could be matched",
                            true);
                    return;
                }

                Log.d(TAG, "Extraction complete: " + data.size() + " fields from "
                        + visionText.getTextBlocks().size() + " blocks");
                callback.onSuccess(finalDocumentType, finalConfidence, data);
            }

            @Override
            public void onFailure(@NonNull String errorCode,
                                  @NonNull String message,
                                  boolean isRetryable) {
                int mappedCode = mapMlKitFailureToProviderCode(errorCode);
                callback.onProviderError(String.valueOf(mappedCode), message);
            }
        });
    }


    @Override
    public void release() {
        mlKitEngine.release();
        if (documentClassifier != null) {
            documentClassifier.close();
        }
    }

    private static int mapMlKitFailureToProviderCode(@NonNull String mlKitErrorCode) {
        switch (mlKitErrorCode) {
            case "NO_TEXT_FOUND":
                return OcrErrorCode.DOCUMENT_NOT_DETECTED.code;
            case "IMAGE_NULL":
            case "ML_KIT_FAILURE":
            default:
                return OcrErrorCode.TECHNICAL_ERROR.code;
        }
    }
}