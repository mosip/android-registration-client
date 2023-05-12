package io.mosip.registration.packetmanager.exception;


import java.io.Serializable;
import lombok.Generated;

class InfoItem implements Serializable {
    private static final long serialVersionUID = -779695043380592601L;
    public String errorCode = null;
    public String errorText = null;

    @Generated
    public InfoItem(String errorCode, String errorText) {
        this.errorCode = errorCode;
        this.errorText = errorText;
    }

    @Generated
    public InfoItem() {
    }

    @Generated
    public String getErrorCode() {
        return this.errorCode;
    }

    @Generated
    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    @Generated
    public String getErrorText() {
        return this.errorText;
    }

    @Generated
    public void setErrorText(String errorText) {
        this.errorText = errorText;
    }
}
