/*
 * Copyright (c) Modular Open Source Identity Platform
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.mosip.registration_client.ocr;

public final class OcrParamKeys {

    public static final String OCR_ENABLED = "mosip.registration.ocr.enabled";
    public static final String OCR_PROVIDER = "mosip.registration.ocr.provider";
    public static final String OCR_RESPONSE_TIMEOUT = "mosip.registration.ocr.response.timeout";
    public static final String OCR_SERVICE_URL = "mosip.registration.ocr.service.url";

    public static final String OCR_QUALITY_MAX_RETRIES = "mosip.registration.ocr.quality.max_retries";

    public static final String OCR_QUALITY_THRESHOLD_BRIGHTNESS_MIN ="mosip.registration.ocr.quality.thresholds.brightness_min";
    public static final String OCR_QUALITY_THRESHOLD_BRIGHTNESS_MAX ="mosip.registration.ocr.quality.thresholds.brightness_max";
    public static final String OCR_QUALITY_THRESHOLD_BLUR_VARIANCE ="mosip.registration.ocr.quality.thresholds.blur_variance";

    public static final String OCR_UI_SPEC = "mosip.registration.ocr.ui.spec";

    public static final String OCR_EXTRACTION_LABELS = "mosip.registration.ocr.extraction.labels";
    public static final String OCR_EXTRACTION_GENDER_VALUES = "mosip.registration.ocr.extraction.gender_values";
    public static final String OCR_EXTRACTION_MONTH_NAMES = "mosip.registration.ocr.extraction.month_names";
    public static final String OCR_EXTRACTION_HEADER_PATTERNS = "mosip.registration.ocr.extraction.header_patterns";
    public static final String OCR_EXTRACTION_NOISE_CHARS = "mosip.registration.ocr.extraction.noise_chars";

    public static final String OCR_EXTRACTION_FUZZY_THRESHOLD =
            "mosip.registration.ocr.extraction.fuzzy_threshold";

    public static final String OCR_EXTRACTION_FIELD_REGEXES =
            "mosip.registration.ocr.extraction.field_regexes";

    private OcrParamKeys() { }
}