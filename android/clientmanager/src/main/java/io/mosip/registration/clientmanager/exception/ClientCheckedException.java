package io.mosip.registration.clientmanager.exception;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

public class ClientCheckedException extends Exception {

    private final List<InfoItem> infoItems = new ArrayList<>();

    /**
     * Constructs a new checked exception
     */
    public ClientCheckedException() {
        super();
    }

    /**
     * Constructs a new checked exception with errorMessage
     *
     * @param errorMessage the detail message.
     */
    public ClientCheckedException(String errorMessage) {
        super(errorMessage);
    }

    public ClientCheckedException(Context context, int errorResId) {
        super(context.getString(errorResId));
    }

    /**
     * Constructs a new checked exception with the specified detail message and
     * error code.
     *
     * @param errorCode    the error code
     * @param errorMessage the detail message.
     */
    public ClientCheckedException(String errorCode, String errorMessage) {
        super(errorCode + " --> " + errorMessage);
        addInfo(errorCode, errorMessage);
    }

    /**
     * Constructs a new checked exception with the specified detail message and
     * error code and specified cause.
     *
     * @param errorCode    the error code
     * @param errorMessage the detail message.
     * @param rootCause    the specified cause
     *
     */
    public ClientCheckedException(String errorCode, String errorMessage, Throwable rootCause) {
        super(errorCode + " --> " + errorMessage, rootCause);
        addInfo(errorCode, errorMessage);
        if (rootCause instanceof ClientCheckedException) {
            ClientCheckedException bce = (ClientCheckedException) rootCause;
            infoItems.addAll(bce.infoItems);
        }
    }

    /**
     * This method add error code and error message.
     *
     * @param errorCode the error code
     * @param errorText the detail message.
     *
     * @return the current instance of BaseCheckedException
     */
    public ClientCheckedException addInfo(String errorCode, String errorText) {
        this.infoItems.add(new InfoItem(errorCode, errorText));
        return this;
    }

    /*
     * Returns a String object that can be used to get the exception message.
     *
     * @see java.lang.Throwable#getMessage()
     */
    @Override
    public String getMessage() {
        return buildMessage(super.getMessage(), getCause());
    }

    /**
     * Returns the list of exception codes.
     *
     * @return list of exception codes
     */
    public List<String> getCodes() {
        List<String> codes = new ArrayList<>();
        for (int i = this.infoItems.size() - 1; i >= 0; i--)
            codes.add(this.infoItems.get(i).errorCode);
        return codes;
    }

    /**
     * Returns the list of exception messages.
     *
     * @return list of exception messages
     */
    public List<String> getErrorTexts() {
        List<String> errorTexts = new ArrayList<>();
        for (int i = this.infoItems.size() - 1; i >= 0; i--)
            errorTexts.add(this.infoItems.get(i).errorText);
        return errorTexts;
    }

    /**
     * Return the last error code.
     *
     * @return the last error code
     */
    public String getErrorCode() {
        return infoItems.get(0).errorCode;
    }

    /**
     * Return the last exception message.
     *
     * @return the last exception message
     */
    public String getErrorText() {
        return infoItems.get(0).errorText;
    }

    private String buildMessage(String message, Throwable cause) {
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
}
