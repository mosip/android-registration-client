package io.mosip.registration.clientmanager.spi;

import io.mosip.registration.clientmanager.constant.SBIError;
import io.mosip.registration.clientmanager.exception.BiometricsServiceException;
import io.mosip.registration.packetmanager.util.DateUtils;

import java.time.LocalDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * All the SBI implementation classes should extend from this abstract class
 * @SBIFacade will be injected with List<BiometricsService> instances.
 * Facade will iterate through all the available instances to discover connected
 * biometric devices.
 *
 * All the available biometric devices will be cached in the facade and refreshed
 * after every configured interval.
 *
 * on every stream request device available with the highest version will be chosen
 * to start the streaming. The Rcapture is invoked on the same device. *
 */
public abstract class BiometricsService {

    //TODO Need to take these values from properties
    private String rCaptureTrustDomain = "DEVICE";
    private String digitalIdTrustDomain = "DEVICE";
    private String deviceInfoTrustDomain = "DEVICE";
    private int allowedResponseLagMins = 5;
    public static final String BIOMETRIC_SEPARATOR = "(?<=\\.)(.*)(?=\\.)";
    private static final String DATETIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss'Z'";

    /**
     * Validates JWT response from DeviceInfo and Rcapture responses.
     * if the validation fails, throws BiometricsServiceException with below errors
     * SBI_INVALID_SIGNATURE
     * SBI_CERT_PATH_TRUST_FAILED
     * @param signedData
     * @param domain
     */
    private void validateJWTResponse(final String signedData, final String domain)
            throws BiometricsServiceException {
        //TODO
    }

    private String getJWTPayLoad(String jwt) throws BiometricsServiceException {
        if (jwt == null || jwt.isEmpty()) {
            throw new BiometricsServiceException(SBIError.SBI_JWT_INVALID.getErrorCode(),
                    SBIError.SBI_JWT_INVALID.getErrorMessage());
        }
        Pattern pattern = Pattern.compile(BIOMETRIC_SEPARATOR);
        Matcher matcher = pattern.matcher(jwt);
        if (matcher.find()) {
            return matcher.group(1);
        }
        throw new BiometricsServiceException(SBIError.SBI_PAYLOAD_EMPTY.getErrorCode(),
                SBIError.SBI_PAYLOAD_EMPTY.getErrorMessage());
    }

    public String getJWTSignatureWithHeader(String jwt) throws BiometricsServiceException {
        if (jwt == null || jwt.isEmpty()) {
            throw new BiometricsServiceException(SBIError.SBI_JWT_INVALID.getErrorCode(),
                    SBIError.SBI_JWT_INVALID.getErrorMessage());
        }
        Pattern pattern = Pattern.compile(BIOMETRIC_SEPARATOR);
        Matcher matcher = pattern.matcher(jwt);
        if(matcher.find()) {
            //returns header..signature
            return jwt.replace(matcher.group(1),"");
        }

        throw new BiometricsServiceException(SBIError.SBI_SIGNATURE_EMPTY.getErrorCode(),
                SBIError.SBI_SIGNATURE_EMPTY.getErrorMessage());
    }

    public void validateResponseTimestamp(String responseTime) throws BiometricsServiceException {
        if(responseTime != null) {
            try {
                LocalDateTime ts = DateUtils.parseUTCToLocalDateTime(responseTime, DATETIME_PATTERN);
                if(ts.isAfter(LocalDateTime.now().minusMinutes(allowedResponseLagMins))
                        && ts.isBefore(LocalDateTime.now().plusMinutes(allowedResponseLagMins))) {
                    return;
                }
            } catch (Exception ex) {
                throw new BiometricsServiceException(SBIError.SBI_CAPTURE_INVALID_TIME.getErrorCode(),
                        SBIError.SBI_CAPTURE_INVALID_TIME.getErrorMessage(), ex);
            }
        }

        throw new BiometricsServiceException(SBIError.SBI_CAPTURE_INVALID_TIME.getErrorCode(),
                SBIError.SBI_CAPTURE_INVALID_TIME.getErrorMessage());
    }

    public void validateQualityScore(String qualityScore) throws BiometricsServiceException {
        if (qualityScore == null || qualityScore.isEmpty()) {
            throw new BiometricsServiceException(SBIError.SBI_RCAPTURE_ERROR.getErrorCode(),
                    SBIError.SBI_RCAPTURE_ERROR.getErrorMessage()
                            + " Identified Quality Score for capture biometrics is null or Empty");
        }
    }

}
