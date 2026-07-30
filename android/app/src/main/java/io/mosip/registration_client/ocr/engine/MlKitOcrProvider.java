/*
 * Copyright (c) Modular Open Source Identity Platform
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.mosip.registration_client.ocr.engine;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;
import java.util.Map;

import io.mosip.registration_client.ocr.extraction.DemographicFieldExtractor;
import io.mosip.registration_client.ocr.extraction.ExtractionConfig;
import io.mosip.registration_client.ocr.models.FieldSpec;
import io.mosip.registration_client.ocr.models.OcrError;
import io.mosip.registration_client.ocr.models.OcrErrorCode;

/**
 * On-device provider (mosip.registration.ocr.provider = "mlkit").
 *
 * ML Kit's raw output is unstructured text, not spec-matched id:value
 * pairs the way a remote OCR service is expected to return per §6.2 — so
 * this provider is responsible for its own field-matching (classify, then
 * regex/label extraction against the spec) to still produce the same
 * {documentType, confidence, data} shape the rest of the pipeline expects.
 *
 * §6.2.1 requires every provider — on-device included — to report failures
 * using the standardized errorCode table where applicable. ML Kit itself
 * doesn't produce those codes, so its internal failure reasons are mapped
 * onto the closest standard code below. "No fields survived validation"
 * is NOT mapped here — that's a client-side §8 reason
 * (NO_FIELDS_EXTRACTED), not a provider errorCode, since matching happens
 * after a well-formed result was already obtained.
 */
public class MlKitOcrProvider implements OcrProvider {

    @Nullable
    private final DocumentClassifier documentClassifier;
    private final MlKitEngine mlKitEngine;
    private final DemographicFieldExtractor fieldExtractor;

    public MlKitOcrProvider(@Nullable DocumentClassifier documentClassifier,
                            @NonNull ExtractionConfig extractionConfig) {
        this.documentClassifier = documentClassifier;
        this.mlKitEngine = new MlKitEngine();
        this.fieldExtractor = new DemographicFieldExtractor(extractionConfig);
    }

    @Override
    public void extract(
            @NonNull Bitmap bitmap,
            @NonNull List<FieldSpec> spec,
            @NonNull OcrProviderCallback callback) {

        String documentType = "UNKNOWN";
        float classificationConfidence = 0f;

        if (documentClassifier != null) {
            DocumentClassifier.ClassificationResult result = documentClassifier.classify(bitmap);
            if (result != null) {
                documentType = result.documentType;
                classificationConfidence = result.confidence;
            }
            // A null result means the classifier pipeline itself threw —
            // that's a technical extraction error (102), not a reason to
            // abort; text extraction can still proceed with UNKNOWN.
        }

        final String finalDocumentType = documentType;
        final float finalConfidence = classificationConfidence;

        mlKitEngine.extractText(bitmap, "image/png", new OcrEngine.OcrEngineCallback() {

            @Override
            public void onSuccess(@NonNull String rawText, float avgConfidence) {
                // ── RAW TEXT DEBUG ────────────────────────────────────────
                // Filter: adb logcat -s OCR_RAW_TEXT
                android.util.Log.d("OCR_RAW_TEXT",
                        "══════════ RAW OCR TEXT (avgConf=" + avgConfidence + ") ══════════\n"
                        + rawText
                        + "\n══════════════════════════════════════════════════════");

                Map<String, String> data = fieldExtractor.extract(rawText, finalDocumentType, spec);


                if (data.isEmpty()) {
                    // Client-side reason (matching failed post-extraction),
                    // not a provider errorCode.
                    callback.onFailure(
                            OcrError.ErrorCode.NO_FIELDS_EXTRACTED.name(),
                            "OCR text was read but no spec fields could be matched",
                            true);
                    return;
                }

                callback.onSuccess(finalDocumentType, finalConfidence, data);
            }

            @Override
            public void onFailure(@NonNull String errorCode, @NonNull String message, boolean isRetryable) {
                // A response WAS obtained from the recognizer (it ran and
                // reported a reason), so this maps to an in-band provider
                // errorCode rather than the client-side channel.
                int mappedCode = mapMlKitFailureToProviderCode(errorCode);
                callback.onProviderError(String.valueOf(mappedCode), message);
            }
        });
    }

    private int mapMlKitFailureToProviderCode(@NonNull String mlKitErrorCode) {
        switch (mlKitErrorCode) {
            case "NO_TEXT_FOUND":
                // Recognizer ran clean but found nothing at all — closest
                // to "document not detected in frame".
                return OcrErrorCode.DOCUMENT_NOT_DETECTED.code;
            case "IMAGE_NULL":
            case "ML_KIT_FAILURE":
            default:
                return OcrErrorCode.TECHNICAL_ERROR.code;
        }
    }

    @Override
    public void release() {
        mlKitEngine.release();
        if (documentClassifier != null) {
            documentClassifier.close();
        }
    }
}