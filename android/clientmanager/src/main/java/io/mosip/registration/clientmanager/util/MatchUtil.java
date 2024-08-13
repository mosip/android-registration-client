package io.mosip.registration.clientmanager.util;

import android.util.Log;

import org.apache.velocity.util.StringUtils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import io.mosip.kernel.biometrics.constant.BiometricType;
import io.mosip.kernel.biometrics.constant.Match;
import io.mosip.kernel.biometrics.constant.ProcessedLevelType;
import io.mosip.kernel.biometrics.constant.PurposeType;
import io.mosip.kernel.biometrics.constant.QualityType;
import io.mosip.kernel.biometrics.entities.BDBInfo;
import io.mosip.kernel.biometrics.entities.BIR;
import io.mosip.kernel.biometrics.entities.BIRInfo;
import io.mosip.kernel.biometrics.entities.BiometricRecord;
import io.mosip.kernel.biometrics.entities.RegistryIDType;
import io.mosip.kernel.biometrics.entities.SingleAnySubtypeType;
import io.mosip.kernel.biometrics.entities.VersionType;
import io.mosip.kernel.biometrics.model.MatchDecision;
import io.mosip.kernel.biometrics.model.Response;
import io.mosip.kernel.biometrics.spi.IBioApiV2;
import io.mosip.registration.clientmanager.constant.Modality;
import io.mosip.registration.clientmanager.dto.registration.BiometricsDto;
import io.mosip.registration.clientmanager.dto.sbi.CaptureDto;
import io.mosip.registration.clientmanager.entity.UserBiometric;
import io.mosip.registration.clientmanager.repository.UserBiometricRepository;
import io.mosip.registration.keymanager.util.CryptoUtil;
import io.mosip.registration.packetmanager.cbeffutil.jaxbclasses.CbeffConstant;
import io.mosip.registration.packetmanager.util.PacketManagerConstant;


/**
 * Utility for match biometric data
 * @author mdhumair.kankudti
 */
public class MatchUtil {

    /**
     * Responsible for validating the biometric records
     * @param modality {@link Modality}
     * @param captureDto {@link CaptureDto}
     * @param biometricsDtoList {@link List<BiometricsDto>}
     * @param userBiometricRepository {@link UserBiometricRepository}
     * @param iBioApiV2 {@link IBioApiV2}
     * @return boolean value based on the match result
     */
    public static boolean validateBiometricData(Modality modality, CaptureDto captureDto, List<BiometricsDto>
            biometricsDtoList, UserBiometricRepository userBiometricRepository, IBioApiV2 iBioApiV2) {
        BiometricType biometricType = BiometricType.fromValue(modality == Modality.EXCEPTION_PHOTO ?
                modality.getSingleType().value() : captureDto.getBioType());
        String lowerCase = biometricType.toString().toLowerCase();
        String biometricCode = StringUtils.capitalizeFirstLetter(lowerCase);
        List<UserBiometric> userBiometrics = userBiometricRepository.findAllOperatorBiometrics(biometricCode);
        if(userBiometrics.isEmpty()){
            return false;
        }
        return matchBiometrics(biometricType, userBiometrics, biometricsDtoList, iBioApiV2);
    }

    /**
     * Converts the {@link UserBiometric} and {@link BiometricsDto} to {@link BiometricRecord} and
     * {@link BiometricRecord array} respectively and calls the match method of Match-SDK library
     * to get the response of the decisions whether {@link Match MATCHED or NOT_MATCHED}
     * @param biometricType {@link BiometricType}
     * @param userBiometrics {@link UserBiometric}
     * @param biometricsDto {@link BiometricsDto}
     * @param iBioApiV2 {@link IBioApiV2}
     * @return boolean value matched or not based on the match result flag Match.MATCHED or Match.NOT_MATCHED.
     */
    private static boolean matchBiometrics(BiometricType biometricType, List<UserBiometric> userBiometrics, List<BiometricsDto> biometricsDto, IBioApiV2 iBioApiV2) {
        Map<String, List<BIR>> birMap = new HashMap<>();
        for (UserBiometric userBiometric : userBiometrics) {
            String userId = userBiometric.getUsrId();

            birMap.computeIfAbsent(userId, k -> new ArrayList<>())
                    .add(buildBir(userBiometric.getBioAttributeCode(),
                            userBiometric.getQualityScore(), userBiometric.getBioTemplate(), biometricType, ProcessedLevelType.PROCESSED, true));
        }

        List<BIR> birList = new ArrayList<>(biometricsDto.size());

        biometricsDto.forEach(biometricDto -> {
            birList.add(buildBir(biometricDto.getBioSubType(),
                    (long) biometricDto.getQualityScore(),
                    CryptoUtil.base64decoder.decode(biometricDto.getBioValue()), biometricType,
                    ProcessedLevelType.RAW, false));
        });

        BiometricRecord biometricRecord = new BiometricRecord();
        biometricRecord.getSegments().addAll(birList);


        List<BiometricRecord> biometricRecordList = new ArrayList<>();
        for(List<BIR> birListValue: birMap.values()){
            BiometricRecord biometricRecordValue = new BiometricRecord();
            biometricRecordValue.getSegments().addAll(birListValue);
            biometricRecordList.add(biometricRecordValue);
        }
        BiometricRecord [] biometricRecords = biometricRecordList.toArray(new BiometricRecord[0]);

        List<BiometricType> biometricTypes = new ArrayList<>();
        biometricTypes.add(biometricType);

        try {
            Response<MatchDecision[]> response = iBioApiV2.match(biometricRecord, biometricRecords, biometricTypes, new HashMap<>());
            if (response != null && response.getResponse() != null)
            {
                Match decision = Objects.requireNonNull(response.getResponse()[0].getDecisions().get(biometricType)).
                        getMatch();
                if(decision.equals(Match.MATCHED)){
                    return true;
                }
            }
        } catch (Exception e) {
            Log.e("Failed in dedupe check >> ", e.getMessage());
        }
        return false;
    }

    /**
     * Builds the BIR data from the biometric parameters.
     * @param bioAttribute biometric sub type
     * @param qualityScore quality score
     * @param iso byte array comprising of captured data
     * @param biometricType {@link BiometricType}
     * @param processedLevelType {@link ProcessedLevelType}
     * @param isUserBiometric boolean indicating whether the biometric data is from operator
     *                        biometric or current applicant
     * @return {@link BIR} object consisting of decoded data segment.
     */
    public static BIR buildBir(String bioAttribute, long qualityScore, byte[] iso, BiometricType biometricType, ProcessedLevelType processedLevelType, boolean isUserBiometric) {

        // Format
        RegistryIDType birFormat = new RegistryIDType();
        birFormat.setOrganization(PacketManagerConstant.CBEFF_DEFAULT_FORMAT_ORG);
        birFormat.setType(String.valueOf(getFormatType(biometricType)));

        // Algorithm
        RegistryIDType birAlgorithm = new RegistryIDType();
        birAlgorithm.setOrganization(PacketManagerConstant.CBEFF_DEFAULT_ALG_ORG);
        birAlgorithm.setType(PacketManagerConstant.CBEFF_DEFAULT_ALG_TYPE);

        // Quality Type
        QualityType qualityType = new QualityType();
        qualityType.setAlgorithm(birAlgorithm);
        qualityType.setScore(qualityScore);

        List<BiometricType> biometricTypes = new ArrayList<>();
        biometricTypes.add(biometricType);
        return new BIR.BIRBuilder().withBdb(iso)
                .withVersion(new VersionType(1, 1))
                .withCbeffversion(new VersionType(1, 1))
                .withBirInfo(new BIRInfo.BIRInfoBuilder().withIntegrity(false).build())
                .withBdbInfo(new BDBInfo.BDBInfoBuilder().withFormat(birFormat).withQuality(qualityType)
                        .withType(biometricTypes).withSubtype(getSubTypes(biometricType, bioAttribute, isUserBiometric))
                        .withPurpose(PurposeType.IDENTIFY).withLevel(processedLevelType)
                        .withCreationDate(LocalDateTime.now(ZoneId.of("UTC"))).withIndex(UUID.randomUUID().toString())
                        .build())
                .build();
    }

    /**
     * Returns format type required by the {@link BDBInfo} and {@link BIRInfo.BIRInfoBuilder()}
     * @param biometricType {@link BiometricType}
     * @return type of the format
     */
    public static long getFormatType(BiometricType biometricType) {
        long format = 0;
        switch (biometricType) {
            case FINGER:
                format = CbeffConstant.FORMAT_TYPE_FINGER;
                break;

            case EXCEPTION_PHOTO:
            case FACE:
                format = CbeffConstant.FORMAT_TYPE_FACE;
                break;
            case IRIS:
                format = CbeffConstant.FORMAT_TYPE_IRIS;
                break;
        }
        return format;
    }

    /**
     * Returns sub types for the biometric types
     * @param biometricType {@link BiometricType}
     * @param bioAttribute biometric sub type
     * @param isUserBiometric boolean indicating whether the biometric data is from operator
     *                        biometric or current applicant
     * @return list of sub types for the biometric type.
     */
    public static List<String> getSubTypes(BiometricType biometricType, String bioAttribute, boolean isUserBiometric) {
        List<String> subtypes = new LinkedList<>();
        switch (biometricType) {
            case FINGER:
                subtypes.add(bioAttribute.toLowerCase().contains("left") ? SingleAnySubtypeType.LEFT.value()
                        : SingleAnySubtypeType.RIGHT.value());
                if (bioAttribute.toLowerCase().contains("thumb"))
                    subtypes.add(SingleAnySubtypeType.THUMB.value());
                else {
                    String val = bioAttribute.toLowerCase().replace("left", "").replace("right", "");
                    if(isUserBiometric){
                        subtypes.add(SingleAnySubtypeType.fromValue(StringUtils.capitalizeFirstLetter(val).concat("Finger"))
                                .value());
                    } else {
                        subtypes.add(SingleAnySubtypeType.fromValue(val.replace(" ", "")).value());
                    }
                }
                break;
            case IRIS:
                subtypes.add(bioAttribute.toLowerCase().contains("left") ? SingleAnySubtypeType.LEFT.value()
                        : SingleAnySubtypeType.RIGHT.value());
                break;

            case EXCEPTION_PHOTO:
            case FACE:
                break;
        }
        return subtypes;
    }
}
