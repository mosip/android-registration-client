package io.mosip.registration_client.ocr.models;

public class OcrError {
    public enum ErrorCode {
        IMAGE_NULL, IMAGE_BLURRY, IMAGE_POOR_LIGHTING,
        NO_TEXT_FOUND, NO_FIELDS_EXTRACTED, ML_KIT_FAILURE,
        UNRECOGNIZED_DOCUMENT_TYPE, TIMEOUT, NETWORK_ERROR,
        MALFORMED_RESPONSE, CAMERA_ERROR, UNKNOWN
    }

    private final ErrorCode errorCode;
    private final String    message;

    public OcrError(ErrorCode errorCode, String message) {
        this.errorCode = errorCode;
        this.message   = message;
    }

    public ErrorCode getErrorCode() { return errorCode; }
    public String getMessage() { return message; }

    public boolean isRetryable() {
        switch (errorCode) {
            case IMAGE_BLURRY:
            case IMAGE_POOR_LIGHTING:
            case NO_TEXT_FOUND:
            case IMAGE_NULL:
            case NO_FIELDS_EXTRACTED:
            case UNRECOGNIZED_DOCUMENT_TYPE:
            case TIMEOUT:
            case NETWORK_ERROR:
                return true;
            default:
                return false;
        }
    }
}