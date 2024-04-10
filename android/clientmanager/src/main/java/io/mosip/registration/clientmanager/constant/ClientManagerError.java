package io.mosip.registration.clientmanager.constant;

public enum ClientManagerError {

    DATE_PARSE_ERROR("MOS-REG-001", "Date parsing error"),
    REG_BIOMETRIC_DTO_NULL("REG-UOS-001","Biometric Dto is mandatory field and it is missing."),
    SBI_DISCOVER_ERROR("MOS-REG-002", "Failed to discover SBI");

    ClientManagerError(String errorCode, String errorMessage) {
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
