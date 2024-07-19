package io.mosip.registration.clientmanager.service;

import static io.mosip.registration.clientmanager.constant.RegistrationConstants.EXCEPTION_PHOTO_ATTR;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import javax.inject.Inject;


import io.mosip.kernel.biometrics.spi.IBioApiV2;
import io.mosip.registration.clientmanager.constant.AuditEvent;
import io.mosip.registration.clientmanager.constant.Components;
import io.mosip.registration.clientmanager.constant.Modality;
import io.mosip.registration.clientmanager.constant.RegistrationConstants;
import io.mosip.registration.clientmanager.constant.SBIError;
import io.mosip.registration.clientmanager.dto.registration.BiometricsDto;
import io.mosip.registration.clientmanager.dto.sbi.CaptureBioDetail;
import io.mosip.registration.clientmanager.dto.sbi.CaptureDto;
import io.mosip.registration.clientmanager.dto.sbi.CaptureRequest;
import io.mosip.registration.clientmanager.dto.sbi.CaptureRespDetail;
import io.mosip.registration.clientmanager.dto.sbi.CaptureResponse;
import io.mosip.registration.clientmanager.dto.sbi.DeviceDto;
import io.mosip.registration.clientmanager.dto.sbi.DigitalId;
import io.mosip.registration.clientmanager.dto.sbi.InfoResponse;
import io.mosip.registration.clientmanager.exception.BiometricsServiceException;
import io.mosip.registration.clientmanager.repository.GlobalParamRepository;
import io.mosip.registration.clientmanager.repository.UserBiometricRepository;
import io.mosip.registration.clientmanager.spi.AuditManagerService;
import io.mosip.registration.clientmanager.spi.BiometricsService;
import io.mosip.registration.clientmanager.util.MatchUtil;
import io.mosip.registration.keymanager.dto.JWTSignatureVerifyRequestDto;
import io.mosip.registration.keymanager.dto.JWTSignatureVerifyResponseDto;
import io.mosip.registration.keymanager.spi.ClientCryptoManagerService;
import io.mosip.registration.keymanager.util.KeyManagerConstant;
import io.mosip.registration.matchsdk.impl.MatchSDK;

public class Biometrics095Service extends BiometricsService {

    private static final String TAG = Biometrics095Service.class.getSimpleName();

    private String rCaptureTrustDomain = "DEVICE";
    private String deviceInfoTrustDomain = "DEVICE";
    private String digitalIdTrustDomain = "DEVICE";

    private Context context;
    private ObjectMapper objectMapper;
    private AuditManagerService auditManagerService;
    private GlobalParamRepository globalParamRepository;

    private ClientCryptoManagerService clientCryptoManagerService;

    private final UserBiometricRepository userBiometricRepository;
    private IBioApiV2 iBioApiV2;


    @Inject
    public Biometrics095Service(Context context, ObjectMapper objectMapper,
                                AuditManagerService auditManagerService, GlobalParamRepository globalParamRepository, ClientCryptoManagerService clientCryptoManagerService, UserBiometricRepository userBiometricRepository) {
        this.context = context;
        this.objectMapper = objectMapper;
        this.auditManagerService = auditManagerService;
        this.globalParamRepository = globalParamRepository;
        this.clientCryptoManagerService = clientCryptoManagerService;
        this.userBiometricRepository = userBiometricRepository;
        this.iBioApiV2 = new MatchSDK();
    }

    public CaptureRequest getRCaptureRequest(Modality modality, String deviceId, List<String> exceptionAttributes) {
        CaptureRequest captureRequest = new CaptureRequest();
        captureRequest.setEnv("Developer");
        captureRequest.setPurpose("Registration");
        captureRequest.setTimeout(10000);
        captureRequest.setSpecVersion("0.9.5");
        List<CaptureBioDetail> list = new ArrayList<>();
        CaptureBioDetail detail = new CaptureBioDetail();
        detail.setType(modality == Modality.EXCEPTION_PHOTO ? Modality.FACE.getSingleType().value() : modality.getSingleType().value());
        detail.setException(modality == Modality.EXCEPTION_PHOTO ? exceptionAttributes.toArray(new String[0]) :
                Modality.getSpecBioSubType(exceptionAttributes).toArray(new String[0]));
        detail.setBioSubType(new String[]{});
        detail.setCount(modality == Modality.EXCEPTION_PHOTO ? 1 : modality.getAttributes().size()-exceptionAttributes.size());
        detail.setDeviceId(deviceId);
        detail.setRequestedScore(getModalityThreshold(modality));
        detail.setDeviceSubId(String.valueOf(modality.getDeviceSubId()));
        detail.setPreviousHash("");
        list.add(detail);
        captureRequest.setBio(list);
        return captureRequest;
    }


    public List<BiometricsDto> handleRCaptureResponse(Modality modality, InputStream response, List<String> exceptionAttributes)
            throws BiometricsServiceException {
        List<BiometricsDto> biometricsDtoList = new ArrayList<>();
        try {
            CaptureResponse captureResponse = objectMapper.readValue(response, new TypeReference<CaptureResponse>(){});

            List<String> exemptedBioSubTypes = Modality.getSpecBioSubType(exceptionAttributes);
            for (CaptureRespDetail bio : captureResponse.getBiometrics()) {
                //On error, even for one attribute fail the RCapture
                if (bio.getError() != null && !"0".equals(bio.getError().getErrorCode()))
                    throw new BiometricsServiceException(bio.getError().getErrorCode(), bio.getError().getErrorInfo());

                if(bio.getData() == null || bio.getData().trim().isEmpty())
                    throw new BiometricsServiceException(SBIError.SBI_RCAPTURE_ERROR.getErrorCode(),
                            SBIError.SBI_RCAPTURE_ERROR.getErrorMessage());

                validateJWTResponse(bio.getData(), rCaptureTrustDomain);
                String payload = getJWTPayLoad(bio.getData());
                String signature = getJWTSignatureWithHeader(bio.getData());
                byte[] decodedPayload = Base64.getUrlDecoder().decode(payload);
                CaptureDto captureDto = objectMapper.readValue(decodedPayload, new TypeReference<CaptureDto>() {});
                validateResponseTimestamp(captureDto.getTimestamp());
                //TODO need request transaction id to validate response transaction id
                //TODO need requested spec version to validate response spec version

                biometricsDtoList.add(new BiometricsDto(
                        modality == Modality.EXCEPTION_PHOTO ? modality.getSingleType().value() : captureDto.getBioType(),
                        modality == Modality.EXCEPTION_PHOTO ? EXCEPTION_PHOTO_ATTR.get(0) : captureDto.getBioSubType(),
                        captureDto.getBioValue(),
                        bio.getSpecVersion(),
                        exemptedBioSubTypes.contains(captureDto.getBioSubType()),
                        new String(decodedPayload),
                        signature,
                        false,
                        1, 0,
                        captureDto.getQualityScore()));

                if(RegistrationConstants.DISABLE.equalsIgnoreCase(this.globalParamRepository
                        .getCachedStringGlobalParam(RegistrationConstants.DEDUPLICATION_ENABLE_FLAG))) {
                    boolean isMatched = MatchUtil.validateBiometricData(modality, captureDto, biometricsDtoList, userBiometricRepository, iBioApiV2);
                    if(isMatched){
                        Log.i(TAG, "Biometrics Matched With Operator Biometrics, Please Try Again");
                        return null;
                    }
                }
            }
        } catch (BiometricsServiceException e) {
            auditManagerService.audit(AuditEvent.R_CAPTURE_PARSE_FAILED, Components.REGISTRATION, e.getMessage());
            throw e;
        } catch (Exception e) {
            auditManagerService.audit(AuditEvent.R_CAPTURE_PARSE_FAILED, Components.REGISTRATION, e.getMessage());
            Log.e(TAG, "Failed to parse 095 RCapture response", e);
            throw new BiometricsServiceException(SBIError.SBI_RCAPTURE_ERROR.getErrorCode(),
                    SBIError.SBI_RCAPTURE_ERROR.getErrorMessage());
        }
        return biometricsDtoList;
    }


    public String[] handleDeviceInfoResponse(Modality modality, byte[] response) throws BiometricsServiceException {
        String callbackId = null;
        String serialNo = null;
        try {
            List<InfoResponse> list = objectMapper.readValue(response, new TypeReference<List<InfoResponse>>() {});

            if (list.isEmpty())
                throw new BiometricsServiceException(SBIError.SBI_DINFO_INVALID_REPSONSE.getErrorCode(),
                        SBIError.SBI_DINFO_INVALID_REPSONSE.getErrorMessage());

            InfoResponse infoResponse = list.get(0);
            if (infoResponse.getError() != null && !"0".equals(infoResponse.getError().getErrorCode()))
                throw new BiometricsServiceException(infoResponse.getError().getErrorCode(),
                        infoResponse.getError().getErrorInfo());

            validateJWTResponse(infoResponse.getDeviceInfo(), deviceInfoTrustDomain);
            String payload = getJWTPayLoad(infoResponse.getDeviceInfo());
            byte[] decodedPayload = Base64.getUrlDecoder().decode(payload);
            DeviceDto deviceDto = objectMapper.readValue(decodedPayload, DeviceDto.class);
            callbackId = deviceDto.getCallbackId().replace(".info", "");
            String digitalIdPayload = getJWTPayLoad(deviceDto.getDigitalId());
            byte[] decodedDigitalIdPayload = Base64.getUrlDecoder().decode(digitalIdPayload);
            DigitalId digitalId = objectMapper.readValue(decodedDigitalIdPayload, DigitalId.class);
            serialNo = digitalId.getSerialNo();
        } catch (BiometricsServiceException e) {
            auditManagerService.audit(AuditEvent.DEVICE_INFO_PARSE_FAILED, Components.REGISTRATION, e.getMessage());
            Toast.makeText(context, "No SBI found!", Toast.LENGTH_LONG).show();
            throw e;
        } catch (Exception e) {
            auditManagerService.audit(AuditEvent.DEVICE_INFO_PARSE_FAILED, Components.REGISTRATION, e.getMessage());
            Log.e(TAG, "Failed to parse 095 Device info response", e);
            throw new BiometricsServiceException(SBIError.SBI_DINFO_INVALID_REPSONSE.getErrorCode(),
                    SBIError.SBI_DINFO_INVALID_REPSONSE.getErrorMessage());
        }
        return new String[] { callbackId, serialNo };
    }

    public String handleDiscoveryResponse(Modality modality, byte[] response) throws BiometricsServiceException {
        String callbackId = null;
        try {
            List<DeviceDto> list = objectMapper.readValue(response, new TypeReference<List<DeviceDto>>() {});
            if (list.isEmpty())
                throw new BiometricsServiceException(SBIError.SBI_DISC_INVALID_REPSONSE.getErrorCode(),
                        SBIError.SBI_DISC_INVALID_REPSONSE.getErrorMessage());

            DeviceDto deviceDto = list.get(0);
            if (deviceDto.getError() != null && !"0".equals(deviceDto.getError().getErrorCode()))
                throw new BiometricsServiceException(deviceDto.getError().getErrorCode(),
                        deviceDto.getError().getErrorInfo());

            //TODO check device status
            String deviceStatus = deviceDto.getDeviceStatus();
            callbackId = deviceDto.getCallbackId();
        } catch (BiometricsServiceException e) {
            auditManagerService.audit(AuditEvent.DEVICE_INFO_PARSE_FAILED, Components.REGISTRATION, e.getMessage());
            throw e;
        }  catch (Exception e) {
            auditManagerService.audit(AuditEvent.DISCOVER_SBI_PARSE_FAILED, Components.REGISTRATION, e.getMessage());
            Log.e(TAG, "Failed to parse 095 Device discovery response", e);
            throw new BiometricsServiceException(SBIError.SBI_DISC_INVALID_REPSONSE.getErrorCode(),
                    SBIError.SBI_DISC_INVALID_REPSONSE.getErrorMessage());
        }
        return callbackId;
    }

    public int getModalityThreshold(Modality modality) {
        switch (modality) {
            case FINGERPRINT_SLAB_LEFT:
                return globalParamRepository.getCachedIntegerGlobalParam(RegistrationConstants.LEFT_SLAP_THRESHOLD_KEY);
            case FINGERPRINT_SLAB_RIGHT:
                return globalParamRepository.getCachedIntegerGlobalParam(RegistrationConstants.RIGHT_SLAP_THRESHOLD_KEY);
            case FINGERPRINT_SLAB_THUMBS:
                return globalParamRepository.getCachedIntegerGlobalParam(RegistrationConstants.THUMBS_THRESHOLD_KEY);
            case IRIS_DOUBLE:
                return globalParamRepository.getCachedIntegerGlobalParam(RegistrationConstants.IRIS_THRESHOLD_KEY);
            case FACE:
                return globalParamRepository.getCachedIntegerGlobalParam(RegistrationConstants.FACE_THRESHOLD_KEY);
        }
        return 0;
    }

    public int getAttemptsCount(Modality modality) {
        switch (modality) {
            case FINGERPRINT_SLAB_LEFT:
                return globalParamRepository.getCachedIntegerGlobalParam(RegistrationConstants.LEFT_SLAP_ATTEMPTS_KEY);
            case FINGERPRINT_SLAB_RIGHT:
                return globalParamRepository.getCachedIntegerGlobalParam(RegistrationConstants.RIGHT_SLAP_ATTEMPTS_KEY);
            case FINGERPRINT_SLAB_THUMBS:
                return globalParamRepository.getCachedIntegerGlobalParam(RegistrationConstants.THUMBS_ATTEMPTS_KEY);
            case IRIS_DOUBLE:
                return globalParamRepository.getCachedIntegerGlobalParam(RegistrationConstants.IRIS_ATTEMPTS_KEY);
            case FACE:
                return globalParamRepository.getCachedIntegerGlobalParam(RegistrationConstants.FACE_ATTEMPTS_KEY);
        }
        //Number of attempts for exception photo is not restricted
        return 0;
    }

    /**
     * Validates JWT response from DeviceInfo and Rcapture responses.
     * if the validation fails, throws BiometricsServiceException with below errors
     * SBI_INVALID_SIGNATURE
     * SBI_CERT_PATH_TRUST_FAILED
     * @param signedData
     * @param domain
     */
    public void validateJWTResponse(final String signedData, final String domain)
            throws Exception {
        JWTSignatureVerifyRequestDto jwtSignatureVerifyRequestDto = new JWTSignatureVerifyRequestDto();
        jwtSignatureVerifyRequestDto.setValidateTrust(true);
        jwtSignatureVerifyRequestDto.setDomain(domain);
        jwtSignatureVerifyRequestDto.setJwtSignatureData(signedData);

        JWTSignatureVerifyResponseDto jwtSignatureVerifyResponseDto = clientCryptoManagerService.jwtVerify(jwtSignatureVerifyRequestDto);
        if(!jwtSignatureVerifyResponseDto.isSignatureValid())
            throw new BiometricsServiceException(SBIError.SBI_INVALID_SIGNATURE.getErrorCode(), SBIError.SBI_INVALID_SIGNATURE.getErrorMessage());

        if (jwtSignatureVerifyRequestDto.getValidateTrust() && !jwtSignatureVerifyResponseDto.getTrustValid().equals(KeyManagerConstant.TRUST_VALID)) {
            throw new BiometricsServiceException(SBIError.SBI_CERT_PATH_TRUST_FAILED.getErrorCode(), SBIError.SBI_CERT_PATH_TRUST_FAILED.getErrorMessage());
        }
    }

}
