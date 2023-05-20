package io.mosip.registration.clientmanager.spi;

import io.mosip.registration.clientmanager.constant.Modality;
import io.mosip.registration.clientmanager.constant.RegistrationConstants;
import io.mosip.registration.clientmanager.constant.SBIError;
import io.mosip.registration.clientmanager.exception.BiometricsServiceException;
import io.mosip.registration.clientmanager.repository.GlobalParamRepository;
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
    public void validateJWTResponse(final String signedData, final String domain)
            throws BiometricsServiceException {
        /*JWTSignatureVerifyRequestDto jwtSignatureVerifyRequestDto = new JWTSignatureVerifyRequestDto();
        jwtSignatureVerifyRequestDto.setValidateTrust(true);
        jwtSignatureVerifyRequestDto.setDomain(domain);
        jwtSignatureVerifyRequestDto.setJwtSignatureData(signedData);

        JWTSignatureVerifyResponseDto jwtSignatureVerifyResponseDto = signatureService.jwtVerify(jwtSignatureVerifyRequestDto);
        if(!jwtSignatureVerifyResponseDto.isSignatureValid())
            throw new DeviceException(MDMError.MDM_INVALID_SIGNATURE.getErrorCode(), MDMError.MDM_INVALID_SIGNATURE.getErrorMessage());

        if (jwtSignatureVerifyRequestDto.getValidateTrust() && !jwtSignatureVerifyResponseDto.getTrustValid().equals(SignatureConstant.TRUST_VALID)) {
            throw new DeviceException(MDMError.MDM_CERT_PATH_TRUST_FAILED.getErrorCode(), MDMError.MDM_CERT_PATH_TRUST_FAILED.getErrorMessage());
        }*/
    }

    public String getJWTPayLoad(String jwt) throws BiometricsServiceException {
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
}
