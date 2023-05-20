package io.mosip.registration.keymanager.exception;

import lombok.Getter;

public class KeymanagerServiceException extends RuntimeException {

    @Getter
    private String errorCode;

    @Getter
    private String message;

    public KeymanagerServiceException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.message = message;
    }

    public KeymanagerServiceException(String errorCode, String message, Throwable throwable) {
        super(message, throwable);
        this.errorCode = errorCode;
        this.message = message;
    }
}
