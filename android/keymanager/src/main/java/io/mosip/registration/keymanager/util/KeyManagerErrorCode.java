package io.mosip.registration.keymanager.util;


/**
 * @Author George T Abraham
 * @Author Eric John
 */
public enum KeyManagerErrorCode {
    /**
     *
     */
    NO_SUCH_ALGORITHM_EXCEPTION("KER-CRY-001", "No Such algorithm is supported"),
    /**
     *
     */
    INVALID_SPEC_PUBLIC_KEY("KER-CRY-002", "public key is invalid"),
    /**
     *
     */
    INVALID_DATA_WITHOUT_KEY_BREAKER("KER-CRY-003", "data sent to decrypt is without key splitter or invalid"),
    /**
     *
     */
    INVALID_DATA("KER-CRY-003", " or not base64 encoded"),
    /**
     *
     */
    INVALID_REQUEST("KER-CRY-004", "should not be null or empty"),
    /**
     *
     */
    CANNOT_CONNECT_TO_KEYMANAGER_SERVICE("KER-CRY-005", "cannot connect to keymanager service or response is null"),
    /**
     *
     */
    KEYMANAGER_SERVICE_ERROR("KER-CRY-006", "Keymanager Service has replied with following error"),
    /**
     *
     */
    RESPONSE_PARSE_ERROR("KER-CRY-008", "Error occur while parsing response "),
    /**
     *
     */
    DATE_TIME_PARSE_EXCEPTION("KER-CRY-007", "timestamp should be in ISO 8601 format yyyy-MM-ddTHH::mm:ss.SZ"),
    /**
     *
     */
    HEX_DATA_PARSE_EXCEPTION("KER-CRY-009", "Invalid Hex Data"),

    CERTIFICATE_THUMBPRINT_ERROR("KER-CRY-010", "Error in generating Certificate Thumbprint."),

    ENCRYPT_NOT_ALLOWED_ERROR("KER-CRY-011", "Not Allowed to preform encryption with Master Key. Use Base to encrypt data."),

//NEWLY ADDED ERROR CODES

    NO_SUCH_PROVIDER_EXCEPTION("KER-CRY-012","Specified provider is not registered in the security provider list"),

    INVALID_ALGORITHM_PARAMETER_EXCEPTION("KER-CRY-013","Invalid ALgorithm Parameter provided for function call"),

    ILLEGAL_ARGUMENT_EXCEPTION("KER-CRY-013","Illegal argument provided to function(NULL or EMPTY)"),

    KEY_STORE_EXCEPTION("KER-CRY-014","implementation for the specified type is not available from the specified provider"),

    CERTIFICATE_EXCEPTION("KER-CRY-015","Some of the certificates included in the keystore data could not be stored"),

    IO_EXCEPTION("KER-CRY-016","There was an I/O problem with data"),

    UNRECOVERABLE_ENTRY_EXCEPTION("KER-CRY-017","the specified protection Parameter for Keystore were insufficient or invalid"),

    SIGNATURE_EXCEPTION("KER-CRY-018","Signature object not initialized properly / signature algorithm unable to process input data"),

    INVALID_KEY_EXCEPTION("KER-CRY-019","Provided Key is Invalid"),

    BAD_PADDING_EXCEPTION("KER-CRY-020","The decrypted data is not bounded by the appropriate padding bytes"),

    NO_SUCH_PADDING_EXCEPTION("KER-CRY-021","Padding scheme is not available"),

    ILLEGAL_BLOCKSIZE_EXCEPTION("KER-CRY-022"," Input Data length processed by cipher is not multiple of block size"),
    CRYPTO_EXCEPTION("KER-CRY-023","Crypto operation failed"),
    CERTIFICATE_PARSING_ERROR("KER-KMS-013", "Certificate Parsing Error."),
    INVALID_CERTIFICATE("KER-PCM-001", "Invalid Certificate uploaded."),
    INVALID_PARTNER_DOMAIN("KER-PCM-011", "Invalid Partner Domain."),
    CERTIFICATE_EXIST_ERROR("KER-PCM-003", "Certificate already exists in store."),
    CERTIFICATE_DATES_NOT_VALID("KER-PCM-004", "Certificate Dates are not valid."),
    ROOT_CA_NOT_FOUND("KER-PCM-005", "Root CA Certificate not found."),

    INTERNAL_SERVER_ERROR("KER-CRY-500", "Internal server error");


    /**
     * The errorCode
     */
    private final String errorCode;
    /**
     * The errorMessage
     */
    private final String errorMessage;

    /**
     * {@link KeyManagerErrorCode} constructor
     *
     * @param errorCode    error code
     * @param errorMessage error message
     */
    private KeyManagerErrorCode(final String errorCode, final String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    /**
     * Getter for errorCode
     *
     * @return errorCode
     */
    public String getErrorCode() {
        return errorCode;
    }

    /**
     * Getter for errorMessage
     *
     * @return errorMessage
     */
    public String getErrorMessage() {
        return errorMessage;
    }
}
