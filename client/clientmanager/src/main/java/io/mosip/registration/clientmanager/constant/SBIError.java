package io.mosip.registration.clientmanager.constant;

public enum SBIError {

    PARSE_ERROR("REG-SBI-101", "JSON parsing error"),
    DEVICE_NOT_REGISTERED("REG-SBI-102", "Device not registered"),
    UNSUPPORTED_SPEC("REG-SBI-103", "Unsupported SpecVersion"),
    DEVICE_NOT_FOUND("REG-SBI-104", "Device not found"),
    SBI_REQUEST_FAILED("REG-SBI-105", "SBI request Failed : "),
    SBI_INVALID_SIGNATURE("REG-SBI-106", "Device response with invalid signature"),
    SBI_JWT_INVALID("REG-SBI-107","Invalid JWT value (Header.Payload.Signature)"),
    SBI_CERT_PATH_TRUST_FAILED("REG-SBI-108", "Certificate path trust validation failed"),
    SBI_PAYLOAD_EMPTY("REG-SBI-109","Payload is Empty"),
    SBI_SIGNATURE_EMPTY("REG-SBI-110","Signature is Empty"),
    SBI_CAPTURE_INVALID_TIME("REG-SBI-111","RCapture Time was Invalid"),
    SBI_RCAPTURE_INVALID_SCORE("REG-SBI-112","RCapture Failed! Invalid quality score"),
    SBI_DINFO_INVALID_REPSONSE("REG-SBI-113","Device Info Failed! Invalid response"),
    SBI_DISC_INVALID_REPSONSE("REG-SBI-114","Discovery Failed! Invalid response"),
    SBI_RCAPTURE_ERROR("REG-SBI-115","RCapture Failed! ");

    SBIError(String errorCode, String errorMessage) {
        this.setErrorCode(errorCode);
        this.setErrorMessage(errorMessage);
    }

    private String errorCode;
    private String errorMessage;

    public String getErrorCode() {
        return errorCode;
    }
    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
    public String getErrorMessage() {
        return errorMessage;
    }
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
