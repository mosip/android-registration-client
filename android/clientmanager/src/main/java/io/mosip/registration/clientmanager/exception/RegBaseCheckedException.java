package io.mosip.registration.clientmanager.exception;

/**
 * The class to handle all the checked exception in REG
 *
 * @author Balaji Sridharan
 * @since 1.0.0
 *
 */
public class RegBaseCheckedException extends ClientCheckedException {

    /**
     * Serializable Version Id
     */
    private static final long serialVersionUID = 7381314129809012005L;

    /**
     * Constructs a new checked exception
     */
    public RegBaseCheckedException() {
        super();
    }

    /**
     * Constructs a new checked exception with the specified detail message and
     * error code.
     *
     * @param errorCode
     *            the error code
     * @param errorMessage
     *            the detail message.
     */
    public RegBaseCheckedException(String errorCode, String errorMessage) {
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
    public RegBaseCheckedException(String errorCode, String errorMessage, Throwable throwable) {
        super(errorCode, errorMessage, throwable);
    }
}
