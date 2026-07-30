/*
 * Copyright (c) Modular Open Source Identity Platform
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.mosip.registration_client.ocr.models;

import androidx.annotation.NonNull;

/**
 * Standardized in-band provider error codes (HLD §6.2.1). Every OCR
 * provider implementation — remote or on-device (§4.1) — must return one
 * of these where applicable; a provider must not repurpose a standard
 * code for a different meaning. 5xx is reserved for provider-defined
 * custom conditions not covered below.
 *
 * The wire format is a string (e.g. {@code "errorCode": "101"}), not a
 * number — kept as int here for readable constants/switches, converted
 * at the boundary.
 *
 * Distinct from {@link OcrError.ErrorCode}: these are reasons the
 * *provider* gives after receiving a request and returning a response.
 * {@code OcrError.ErrorCode} covers cases where the client never got a
 * well-formed response at all, or rejected the frame/result before or
 * after the fact (§8) — e.g. TIMEOUT, MALFORMED_RESPONSE,
 * NO_FIELDS_EXTRACTED, quality_check_failed/exhausted.
 */
public enum OcrErrorCode {
    SUCCESS(0, "Success"),
    DOCUMENT_NOT_DETECTED(101, "Document not detected in frame"),
    TECHNICAL_ERROR(102, "Technical error during extraction"),
    UNRECOGNIZED_DOCUMENT_TYPE(103, "Unrecognized or unsupported document type"),
    SERVICE_UNREACHABLE(104, "Unable to connect to OCR service (remote provider only)"),
    IMAGE_ORIENTATION_ERROR(105, "Image orientation/skew error"),
    IMAGE_QUALITY_TOO_LOW(106, "Image quality too low for extraction (blur, glare, low resolution)"),
    CREDENTIALS_EXPIRED(107, "OCR provider license/credentials expired"),
    INVALID_FIELD_SPEC(108, "Invalid or missing field spec in request"),
    FIELDS_NOT_SUPPORTED(109, "Requested field(s) not supported for this document type"),
    PROVIDER_NOT_READY(110, "OCR provider not ready"),
    PROVIDER_BUSY(111, "OCR provider busy");

    public final int code;
    public final String defaultMessage;

    OcrErrorCode(int code, String defaultMessage) {
        this.code = code;
        this.defaultMessage = defaultMessage;
    }

    @NonNull
    public String asString() {
        return String.valueOf(code);
    }

    public static boolean isCustomRange(int code) {
        return code >= 500 && code < 600;
    }

    /** Whether a rescan is worth offering for this code. Accepts the raw
     *  wire string; an unparsable/unknown code defaults to retryable
     *  (favor letting the operator try again over a dead end). */
    public static boolean isRetryable(@NonNull String errorCode) {
        int code;
        try {
            code = Integer.parseInt(errorCode.trim());
        } catch (NumberFormatException e) {
            return true;
        }

        switch (code) {
            case 101: // document not detected
            case 102: // technical error - may be transient
            case 104: // service unreachable - may be transient
            case 105: // orientation/skew
            case 106: // quality too low
            case 110: // provider not ready
            case 111: // provider busy
                return true;
            case 103: // unrecognized type - rescanning won't change the model's answer
            case 107: // credentials expired - a config/deployment issue
            case 108: // invalid field spec - a config/deployment issue
            case 109: // fields not supported for this doc type
                return false;
            default:
                // 5xx custom codes are provider-defined; default to retryable
                // since we don't know what they mean.
                return isCustomRange(code);
        }
    }
}