package io.mosip.registration.clientmanager.exception;

public class BiometricsServiceException extends ClientCheckedException {

     /**
     * Constructs a new checked exception with the specified detail message and
     * error code.
     *
     * @param errorCode
     *            the error code
     * @param errorMessage
     *            the detail message.
     */
    public BiometricsServiceException(String errorCode, String errorMessage) {
        super(errorCode, errorMessage);
    }

    /**
     * Constructs a new checked exception with the specified detail message and
     * error code.
     *
     * @param errorCode
     *            the error code
     * @param errorMessage
     *            the detail message
     * @param throwable
     *            the specified cause
     */
    public BiometricsServiceException(String errorCode, String errorMessage, Throwable throwable) {
        super(errorCode, errorMessage, throwable);
    }
}
