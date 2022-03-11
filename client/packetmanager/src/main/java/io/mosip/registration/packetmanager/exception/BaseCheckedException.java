package io.mosip.registration.packetmanager.exception;


import java.util.ArrayList;
import java.util.List;

public class BaseCheckedException extends Exception {
    private static final long serialVersionUID = -924722202100630614L;
    private final List<InfoItem> infoItems = new ArrayList();

    public BaseCheckedException() {
    }

    public BaseCheckedException(String errorMessage) {
        super(errorMessage);
    }

    public BaseCheckedException(String errorCode, String errorMessage) {
        super(errorCode + " --> " + errorMessage);
        this.addInfo(errorCode, errorMessage);
    }

    public BaseCheckedException(String errorCode, String errorMessage, Throwable rootCause) {
        super(errorCode + " --> " + errorMessage, rootCause);
        this.addInfo(errorCode, errorMessage);
        if (rootCause instanceof BaseCheckedException) {
            BaseCheckedException bce = (BaseCheckedException)rootCause;
            this.infoItems.addAll(bce.infoItems);
        }

    }

    public BaseCheckedException addInfo(String errorCode, String errorText) {
        this.infoItems.add(new InfoItem(errorCode, errorText));
        return this;
    }

    public String getMessage() {
        String message = super.getMessage();
        Throwable cause = this.getCause();

        if (cause != null) {
            StringBuilder sb = new StringBuilder();
            if (message != null) {
                sb.append(message).append("; ");
            }

            sb.append("\n");
            sb.append("nested exception is ").append(cause);
            return sb.toString();
        } else {
            return message;
        }
    }

    public List<String> getCodes() {
        List<String> codes = new ArrayList();

        for(int i = this.infoItems.size() - 1; i >= 0; --i) {
            codes.add(((InfoItem)this.infoItems.get(i)).errorCode);
        }

        return codes;
    }

    public List<String> getErrorTexts() {
        List<String> errorTexts = new ArrayList();

        for(int i = this.infoItems.size() - 1; i >= 0; --i) {
            errorTexts.add(((InfoItem)this.infoItems.get(i)).errorText);
        }

        return errorTexts;
    }

    public String getErrorCode() {
        return ((InfoItem)this.infoItems.get(0)).errorCode;
    }

    public String getErrorText() {
        return ((InfoItem)this.infoItems.get(0)).errorText;
    }
}