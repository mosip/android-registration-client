package io.mosip.registration.clientmanager.exception;



import io.mosip.registration.clientmanager.exception.ClientCheckedException;

/**
 * Class for handling the REG unchecked exception
 *
 * @author Balaji Sridharan
 * @since 1.0.0
 *
 */
public class RegBaseUncheckedException extends ClientCheckedException {

    /**
     * Serializable Version Id
     */
    private static final long serialVersionUID = 276197701640260133L;

    /**
     * Constructs a new unchecked exception
     */
    public RegBaseUncheckedException() {
        super();
    }

    /**
     * Constructor
     *
     * @param errorCode
     *            the Error Code Corresponds to Particular Exception
     * @param errorMessage
     *            the Message providing the specific context of the error
     */
    public RegBaseUncheckedException(String errorCode, String errorMessage) {
        super(errorCode, errorMessage);
    }

    /**
     * Constructor
     *
     * @param errorCode
     *            the Error Code Corresponds to Particular Exception
     * @param errorMessage
     *            the Message providing the specific context of the error
     * @param throwable
     *            the Cause of exception
     */
    public RegBaseUncheckedException(String errorCode, String errorMessage, Throwable throwable) {
        super(errorCode, errorMessage, throwable);
    }
}
