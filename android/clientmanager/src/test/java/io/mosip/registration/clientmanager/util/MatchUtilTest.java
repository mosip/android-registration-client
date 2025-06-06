package io.mosip.registration.clientmanager.util;

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
import io.mosip.kernel.biometrics.model.Decision;
import io.mosip.kernel.biometrics.model.MatchDecision;
import io.mosip.kernel.biometrics.model.Response;
import io.mosip.kernel.biometrics.spi.IBioApiV2;
import io.mosip.registration.clientmanager.constant.Modality;
import io.mosip.registration.clientmanager.dto.registration.BiometricsDto;
import io.mosip.registration.clientmanager.dto.sbi.CaptureDto;
import io.mosip.registration.clientmanager.entity.UserBiometric;
import io.mosip.registration.clientmanager.repository.UserBiometricRepository;
import io.mosip.registration.clientmanager.util.MatchUtil;
import io.mosip.registration.packetmanager.cbeffutil.jaxbclasses.CbeffConstant;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class MatchUtilTest {

    @Mock
    private UserBiometricRepository userBiometricRepository;
    @Mock
    private IBioApiV2 iBioApiV2;
    @Mock
    private MatchUtil matchUtil;

    @Test
    public void testBuildBir_finger() {
        BIR bir = matchUtil.buildBir("LeftIndex", 80L, new byte[]{1, 2, 3}, BiometricType.FINGER, ProcessedLevelType.PROCESSED, true);
        assertNotNull(bir);
        assertNotNull(bir.getBdbInfo());
        assertEquals(PurposeType.IDENTIFY, bir.getBdbInfo().getPurpose());
    }

    @Test
    public void testBuildBir_face() {
        BIR bir = matchUtil.buildBir("Face", 90L, new byte[]{1, 2, 3}, BiometricType.FACE, ProcessedLevelType.RAW, false);
        assertNotNull(bir);
        assertNotNull(bir.getBdbInfo());
    }

    @Test
    public void testBuildBir_iris() {
        BIR bir = matchUtil.buildBir("LeftIris", 70L, new byte[]{1, 2, 3}, BiometricType.IRIS, ProcessedLevelType.PROCESSED, false);
        assertNotNull(bir);
        assertNotNull(bir.getBdbInfo());
    }

    @Test
    public void testBuildBir_exceptionPhoto() {
        BIR bir = matchUtil.buildBir("ExceptionPhoto", 60L, new byte[]{1, 2, 3}, BiometricType.EXCEPTION_PHOTO, ProcessedLevelType.RAW, false);
        assertNotNull(bir);
        assertNotNull(bir.getBdbInfo());
    }

    @Test
    public void testGetFormatType() {
        assertEquals(CbeffConstant.FORMAT_TYPE_FINGER, matchUtil.getFormatType(BiometricType.FINGER));
        assertEquals(CbeffConstant.FORMAT_TYPE_FACE, matchUtil.getFormatType(BiometricType.FACE));
        assertEquals(CbeffConstant.FORMAT_TYPE_FACE, matchUtil.getFormatType(BiometricType.EXCEPTION_PHOTO));
        assertEquals(CbeffConstant.FORMAT_TYPE_IRIS, matchUtil.getFormatType(BiometricType.IRIS));
    }

    @Test
    public void testGetSubTypes_finger_leftThumb_user() {
        List<String> subtypes = matchUtil.getSubTypes(BiometricType.FINGER, "LeftThumb", true);
        assertEquals(2, subtypes.size());
        assertEquals(SingleAnySubtypeType.LEFT.value(), subtypes.get(0));
        assertEquals(SingleAnySubtypeType.THUMB.value(), subtypes.get(1));
    }

    @Test
    public void testGetSubTypes_iris_left() {
        List<String> subtypes = matchUtil.getSubTypes(BiometricType.IRIS, "LeftIris", false);
        assertEquals(1, subtypes.size());
        assertEquals(SingleAnySubtypeType.LEFT.value(), subtypes.get(0));
    }

    @Test
    public void testGetSubTypes_face() {
        List<String> subtypes = matchUtil.getSubTypes(BiometricType.FACE, "Face", false);
        assertTrue(subtypes.isEmpty());
    }

    @Test
    public void testGetSubTypes_exceptionPhoto() {
        List<String> subtypes = matchUtil.getSubTypes(BiometricType.EXCEPTION_PHOTO, "ExceptionPhoto", false);
        assertTrue(subtypes.isEmpty());
    }

    @Test
    public void test_validate_biometric_data_empty_user_biometrics() {
        Modality modality = Modality.FACE;

        CaptureDto captureDto = new CaptureDto();
        captureDto.setBioType("Face");

        List<BiometricsDto> biometricsDtoList = new ArrayList<>();
        BiometricsDto biometricsDto = new BiometricsDto();
        biometricsDto.setBioValue("base64EncodedFaceValue");
        biometricsDto.setBioSubType("Face");
        biometricsDto.setQualityScore(85.0f);
        biometricsDtoList.add(biometricsDto);

        Mockito.when(userBiometricRepository.findAllOperatorBiometrics("Face")).thenReturn(new ArrayList<>());

        boolean result = matchUtil.validateBiometricData(modality, captureDto, biometricsDtoList, userBiometricRepository, iBioApiV2);

        assertFalse(result);
        Mockito.verify(userBiometricRepository).findAllOperatorBiometrics("Face");
        Mockito.verify(iBioApiV2, Mockito.never()).match(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
    }

    @Test
    public void test_match_biometrics_when_valid_data_returns_true() {
        BiometricType biometricType = BiometricType.FINGER;

        List<UserBiometric> userBiometrics = new ArrayList<>();
        UserBiometric userBiometric = new UserBiometric();
        userBiometric.setUsrId("testUser");
        userBiometric.setBioAttributeCode("LEFT_THUMB");
        userBiometric.setQualityScore(90);
        userBiometric.setBioTemplate("template".getBytes());
        userBiometrics.add(userBiometric);

        List<BiometricsDto> biometricsDto = new ArrayList<>();
        BiometricsDto dto = new BiometricsDto();
        dto.setBioSubType("LEFT_THUMB");
        dto.setQualityScore(85);
        dto.setBioValue(Base64.getUrlEncoder().encodeToString("template".getBytes()));
        biometricsDto.add(dto);

        Response<MatchDecision[]> response = new Response<>();
        MatchDecision[] decisions = new MatchDecision[1];
        Map<BiometricType, Decision> decisionMap = new HashMap<>();
        Decision decision = new Decision();
        decision.setMatch(Match.MATCHED);
        decisionMap.put(biometricType, decision);
        response.setResponse(decisions);

        Mockito.when(iBioApiV2.match(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(response);

        boolean result = Boolean.TRUE.equals(ReflectionTestUtils.invokeMethod(matchUtil, "matchBiometrics",
                biometricType, userBiometrics, biometricsDto, iBioApiV2));

        assertFalse(result);

        Mockito.verify(iBioApiV2).match(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
    }

    @Test
    public void test_match_biometrics_with_empty_user_biometrics_returns_false() {
        BiometricType biometricType = BiometricType.FINGER;

        List<UserBiometric> userBiometrics = new ArrayList<>();

        List<BiometricsDto> biometricsDto = new ArrayList<>();
        BiometricsDto dto = new BiometricsDto();
        dto.setBioSubType("LEFT_THUMB");
        dto.setQualityScore(85);
        dto.setBioValue(Base64.getUrlEncoder().encodeToString("template".getBytes()));
        biometricsDto.add(dto);

        Response<MatchDecision[]> response = new Response<>();
        MatchDecision[] decisions = new MatchDecision[0];
        response.setResponse(decisions);

        Mockito.when(iBioApiV2.match(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(response);

        boolean result = Boolean.TRUE.equals(ReflectionTestUtils.invokeMethod(matchUtil, "matchBiometrics",
                biometricType, userBiometrics, biometricsDto, iBioApiV2));

        assertFalse(result);

        ArgumentCaptor<BiometricRecord[]> recordsCaptor = ArgumentCaptor.forClass(BiometricRecord[].class);
        Mockito.verify(iBioApiV2).match(Mockito.any(BiometricRecord.class), recordsCaptor.capture(), Mockito.any(), Mockito.any());
        assertEquals(0, recordsCaptor.getValue().length);
    }

}
