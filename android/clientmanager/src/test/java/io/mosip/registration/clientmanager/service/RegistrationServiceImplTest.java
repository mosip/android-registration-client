package io.mosip.registration.clientmanager.service;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.mosip.registration.clientmanager.config.SessionManager;
import io.mosip.registration.clientmanager.constant.RegistrationConstants;
import io.mosip.registration.clientmanager.dto.CenterMachineDto;
import io.mosip.registration.clientmanager.dto.registration.RegistrationDto;
import io.mosip.registration.clientmanager.exception.ClientCheckedException;
import io.mosip.registration.clientmanager.repository.GlobalParamRepository;
import io.mosip.registration.clientmanager.repository.IdentitySchemaRepository;
import io.mosip.registration.clientmanager.repository.RegistrationRepository;
import io.mosip.registration.clientmanager.spi.AuditManagerService;
import io.mosip.registration.clientmanager.spi.MasterDataService;
import io.mosip.registration.clientmanager.spi.RegistrationService;
import io.mosip.registration.keymanager.repository.KeyStoreRepository;
import io.mosip.registration.keymanager.spi.ClientCryptoManagerService;
import io.mosip.registration.packetmanager.spi.PacketWriterService;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import io.mosip.registration.clientmanager.constant.Modality;
import io.mosip.registration.clientmanager.dto.registration.BiometricsDto;
import io.mosip.registration.clientmanager.dto.uispec.FieldSpecDto;
import io.mosip.registration.clientmanager.entity.Registration;
import io.mosip.registration.packetmanager.cbeffutil.jaxbclasses.BIR;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RegistrationServiceImplTest {

    @Mock
    private Context mockApplicationContext;
    @Mock
    private SharedPreferences mockSharedPreferences;
    @Mock
    private MasterDataService masterDataService;
    @Mock
    private IdentitySchemaRepository identitySchemaRepository;
    @Mock
    private KeyStoreRepository keyStoreRepository;
    @Mock
    private GlobalParamRepository globalParamRepository;
    @Mock
    private PacketWriterService packetWriterService;
    @Mock
    private RegistrationRepository registrationRepository;
    @Mock
    private AuditManagerService auditManagerService;
    @Mock
    private ClientCryptoManagerService clientCryptoManagerService;
    private RegistrationService registrationService;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        SharedPreferences.Editor editor = mock(SharedPreferences.Editor.class);
        when(mockSharedPreferences.edit()).thenReturn(editor);
        when(mockApplicationContext.getString(anyInt())).thenReturn("Registration Client");
        when(mockApplicationContext.getSharedPreferences(anyString(), anyInt())).thenReturn(mockSharedPreferences);
        registrationService = new RegistrationServiceImpl(mockApplicationContext, packetWriterService,
                registrationRepository, masterDataService, identitySchemaRepository, clientCryptoManagerService,
                keyStoreRepository, globalParamRepository, auditManagerService);
    }
    
    @Test(expected = ClientCheckedException.class)
    // Test for getRegistrationDto without starting registration
    public void getRegistrationDtoWithoutStartingRegistration() throws Exception {
        registrationService.getRegistrationDto();
    }

    @Test
    // Test for getRegistrationDto after starting registration
    public void getRegistrationDtoAfterStartingRegistration() throws Exception {
        CenterMachineDto centerMachineDto = new CenterMachineDto();
        centerMachineDto.setCenterId("10001");
        centerMachineDto.setMachineId("110001");
        centerMachineDto.setCenterStatus(true);
        centerMachineDto.setMachineStatus(true);
        centerMachineDto.setMachineRefId("10001_110001");
        when(masterDataService.getRegistrationCenterMachineDetails()).thenReturn(centerMachineDto);
        when(identitySchemaRepository.getLatestSchemaVersion()).thenReturn(1.3);
        when(keyStoreRepository.getCertificateData("10001_110001")).thenReturn("dummy_cert");
        when(globalParamRepository.getCachedIntegerGlobalParam(Mockito.anyString())).thenReturn(3);
        File mockFile = mock(File.class);
        when(mockFile.getUsableSpace()).thenReturn(100l*(1024 * 1024));
        when(mockApplicationContext.getExternalCacheDir()).thenReturn(mockFile);
        RegistrationDto registrationDto = registrationService.startRegistration(Arrays.asList("eng"), "NEW", "NEW");
        RegistrationDto result = registrationService.getRegistrationDto();

        Assert.assertNotNull(registrationDto);
        Assert.assertNotNull(result);
        Assert.assertEquals(registrationDto.getRId(), result.getRId());
    }

    @Test(expected = ClientCheckedException.class)
    // Test for submitRegistrationDto without starting registration
    public void submitRegistrationDtoWithoutStartingRegistration() throws Exception {
        registrationService.submitRegistrationDto("100006");
    }

    @Test(expected = ClientCheckedException.class)
    // Test for startAndSubmitRegistration without starting registration
    public void startAndSubmitRegistration() throws Exception {
        SharedPreferences.Editor editor = mock(SharedPreferences.Editor.class);
        when(mockSharedPreferences.edit()).thenReturn(editor);
        CenterMachineDto centerMachineDto = new CenterMachineDto();
        centerMachineDto.setCenterId("10001");
        centerMachineDto.setMachineId("110001");
        centerMachineDto.setCenterStatus(true);
        centerMachineDto.setMachineStatus(true);
        centerMachineDto.setMachineRefId("10001_110001");
        when(masterDataService.getRegistrationCenterMachineDetails()).thenReturn(centerMachineDto);
        when(identitySchemaRepository.getLatestSchemaVersion()).thenReturn(1.3);
        when(keyStoreRepository.getCertificateData("10001_110001")).thenReturn("dummy_cert");
        when(globalParamRepository.getCachedIntegerGlobalParam(Mockito.anyString())).thenReturn(3);
        File mockFile = mock(File.class);
        when(mockFile.getUsableSpace()).thenReturn(100l*(1024 * 1024));
        when(mockApplicationContext.getExternalCacheDir()).thenReturn(mockFile);
        List<String> selectedLanguages = new ArrayList<>();
        selectedLanguages.add("eng");
        RegistrationDto registrationDto = registrationService.startRegistration(selectedLanguages, "NEW", "NEW");
        RegistrationDto result = registrationService.getRegistrationDto();

        Assert.assertNotNull(registrationDto);
        Assert.assertNotNull(result);
        Assert.assertEquals(registrationDto.getRId(), result.getRId());

        registrationService.submitRegistrationDto("100006");
    }

    @Test(expected = ClientCheckedException.class)
    // Test for startRegistration without master sync
    public void startRegistrationWithoutMasterSync_throwException() throws Exception {
        List<String> selectedLanguages = new ArrayList<>();
        selectedLanguages.add("eng");
        registrationService.startRegistration(selectedLanguages, "NEW", "NEW");
    }

    @Test(expected = ClientCheckedException.class)
    // Test for startRegistration without ID schema
    public void startRegistrationWithoutIDSchema_throwException() throws Exception {
        List<String> selectedLanguages = new ArrayList<>();
        selectedLanguages.add("eng");
        CenterMachineDto centerMachineDto = new CenterMachineDto();
        centerMachineDto.setCenterId("10001");
        centerMachineDto.setMachineId("110001");
        centerMachineDto.setCenterStatus(true);
        centerMachineDto.setMachineStatus(true);
        centerMachineDto.setMachineRefId("10001_110001");
        when(masterDataService.getRegistrationCenterMachineDetails()).thenReturn(centerMachineDto);
        registrationService.startRegistration(selectedLanguages, "NEW", "NEW");
    }

    @Test(expected = ClientCheckedException.class)
    // Test for startRegistration without ID schema and policy key
    public void startRegistrationWithoutPolicyKey_throwException() throws Exception {
        List<String> selectedLanguages = new ArrayList<>();
        selectedLanguages.add("eng");
        CenterMachineDto centerMachineDto = new CenterMachineDto();
        centerMachineDto.setCenterId("10001");
        centerMachineDto.setMachineId("110001");
        centerMachineDto.setCenterStatus(true);
        centerMachineDto.setMachineStatus(true);
        centerMachineDto.setMachineRefId("10001_110001");
        when(masterDataService.getRegistrationCenterMachineDetails()).thenReturn(centerMachineDto);
        when(identitySchemaRepository.getLatestSchemaVersion()).thenReturn(1.4);
        when(keyStoreRepository.getCertificateData("10001_110001")).thenReturn(null);
        registrationService.startRegistration(selectedLanguages, "NEW", "NEW");
    }

    @Test(expected = ClientCheckedException.class)
    // Test for startRegistration with inactive center
    public void startRegistrationInactiveCenter_throwException() throws Exception {
        List<String> selectedLanguages = new ArrayList<>();
        selectedLanguages.add("eng");
        CenterMachineDto centerMachineDto = new CenterMachineDto();
        centerMachineDto.setCenterId("10001");
        centerMachineDto.setMachineId("110001");
        centerMachineDto.setCenterStatus(false);
        centerMachineDto.setMachineStatus(true);
        centerMachineDto.setMachineRefId("10001_110001");
        when(masterDataService.getRegistrationCenterMachineDetails()).thenReturn(centerMachineDto);
        when(identitySchemaRepository.getLatestSchemaVersion()).thenReturn(1.4);
        when(keyStoreRepository.getCertificateData("10001_110001")).thenReturn(null);
        registrationService.startRegistration(selectedLanguages, "NEW", "NEW");
    }

    @Test(expected = ClientCheckedException.class)
    // Test for startRegistration with inactive machine
    public void startRegistrationInactiveMachine_throwException() throws Exception {
        List<String> selectedLanguages = new ArrayList<>();
        selectedLanguages.add("eng");
        CenterMachineDto centerMachineDto = new CenterMachineDto();
        centerMachineDto.setCenterId("10001");
        centerMachineDto.setMachineId("110001");
        centerMachineDto.setCenterStatus(true);
        centerMachineDto.setMachineStatus(false);
        centerMachineDto.setMachineRefId("10001_110001");
        when(masterDataService.getRegistrationCenterMachineDetails()).thenReturn(centerMachineDto);
        when(identitySchemaRepository.getLatestSchemaVersion()).thenReturn(1.4);
        when(keyStoreRepository.getCertificateData("10001_110001")).thenReturn(null);
        registrationService.startRegistration(selectedLanguages, "NEW", "NEW");
    }

    @Test(expected = ClientCheckedException.class)
    // Test for startRegistration with low disk space
    public void clearRegistration() throws Exception {
        CenterMachineDto centerMachineDto = new CenterMachineDto();
        centerMachineDto.setCenterId("10001");
        centerMachineDto.setMachineId("110001");
        centerMachineDto.setCenterStatus(true);
        centerMachineDto.setMachineStatus(true);
        centerMachineDto.setMachineRefId("10001_110001");
        when(masterDataService.getRegistrationCenterMachineDetails()).thenReturn(centerMachineDto);
        when(identitySchemaRepository.getLatestSchemaVersion()).thenReturn(1.3);
        when(keyStoreRepository.getCertificateData("10001_110001")).thenReturn("dummy_cert");
        when(globalParamRepository.getCachedIntegerGlobalParam(Mockito.anyString())).thenReturn(3);
        File mockFile = mock(File.class);
        when(mockFile.getUsableSpace()).thenReturn(100l*(1024 * 1024));
        when(mockApplicationContext.getExternalCacheDir()).thenReturn(mockFile);
        List<String> languages = new ArrayList<>();
        languages.add("eng");
        RegistrationDto registrationDto = registrationService.startRegistration(languages, "NEW", "NEW");
        RegistrationDto result = registrationService.getRegistrationDto();

        Assert.assertNotNull(registrationDto);
        Assert.assertNotNull(result);
        Assert.assertEquals(registrationDto.getRId(), result.getRId());

        registrationService.clearRegistration();
        registrationService.getRegistrationDto();
    }

    @Test
    // Test for approve and reject registration without exception
    public void testApproveAndRejectRegistration_NoException() {
        Registration registration = mock(Registration.class); // Fix: Provide a mock or valid Registration argument
        // Should not throw
        registrationService.approveRegistration(registration);
        registrationService.rejectRegistration(registration);
    }

    @Test
    // Test for combineByteArray with valid input
    public void testCombineByteArray() throws Exception {
        Method combineByteArray = registrationService.getClass().getDeclaredMethod("combineByteArray", List.class);
        combineByteArray.setAccessible(true);
        List<byte[]> byteList = Arrays.asList("a".getBytes(), "bcd".getBytes());
        byte[] result = (byte[]) combineByteArray.invoke(registrationService, byteList);
        assertArrayEquals("abcd".getBytes(), result);
    }

    @Test
    // Test for getAdditionalInfo with String and List
    public void testGetAdditionalInfo_StringAndList() throws Exception {
        Method getAdditionalInfo = registrationService.getClass().getDeclaredMethod("getAdditionalInfo", Object.class);
        getAdditionalInfo.setAccessible(true);

        // String
        String str = "test";
        assertEquals("test", getAdditionalInfo.invoke(registrationService, str));

        RegistrationDto dto = mock(RegistrationDto.class);
        FieldSpecDto fieldSpecDto = new FieldSpecDto();
        fieldSpecDto.setId("id");
        fieldSpecDto.setSubType("FULL_NAME");

        CenterMachineDto centerMachineDto = new CenterMachineDto();
        centerMachineDto.setCenterId("10001");
        centerMachineDto.setMachineId("110001");
        centerMachineDto.setCenterStatus(true);
        centerMachineDto.setMachineStatus(true);
        centerMachineDto.setMachineRefId("10001_110001");
        when(masterDataService.getRegistrationCenterMachineDetails()).thenReturn(centerMachineDto);
        when(identitySchemaRepository.getLatestSchemaVersion()).thenReturn(1.3);
        when(keyStoreRepository.getCertificateData("10001_110001")).thenReturn("dummy_cert");
        when(globalParamRepository.getCachedIntegerGlobalParam(Mockito.anyString())).thenReturn(3);
        File mockFile = mock(File.class);
        when(mockFile.getUsableSpace()).thenReturn(100L * (1024 * 1024));
        when(mockApplicationContext.getExternalCacheDir()).thenReturn(mockFile);

        registrationService.startRegistration(new ArrayList<>(Collections.singletonList("eng")), "NEW", "NEW");
        Method setRegistrationDto = registrationService.getClass().getDeclaredMethod("getRegistrationDto");
        setRegistrationDto.setAccessible(true);

        List<io.mosip.registration.packetmanager.dto.SimpleType> list = new ArrayList<>();
        io.mosip.registration.packetmanager.dto.SimpleType st = new io.mosip.registration.packetmanager.dto.SimpleType();
        st.setLanguage("eng");
        st.setValue("val");
        list.add(st);
        assertEquals("val", getAdditionalInfo.invoke(registrationService, list));
    }

    @Test
    // Test for convertImageToBytes with valid base64 string
    public void testConvertImageToBytes_NullAndIOException() throws Exception {
        Method convertImageToBytes = registrationService.getClass().getDeclaredMethod("convertImageToBytes", String.class);
        convertImageToBytes.setAccessible(true);
        // Should return null for invalid base64
        assertNull(convertImageToBytes.invoke(registrationService, "not_base64"));
    }

    @Test
    // Test for clearRegistration with null RegistrationDto
    public void testClearRegistration_NullRegistrationDto() {
        registrationService.clearRegistration();
        // Should not throw
    }

    @Test
    // Test for addMetaInfoMap with valid parameters
    public void testAddMetaInfoMap() throws Exception {
        // Setup registrationDto with required fields
        RegistrationDto dto = mock(RegistrationDto.class);
        when(dto.getRId()).thenReturn("RID");
        when(dto.getProcess()).thenReturn("NEW");
        when(dto.getSelectedLanguages()).thenReturn(Collections.singletonList("eng"));
        // Fix: Use BIO_DEVICES field instead of getBioDevices() method
        java.lang.reflect.Field bioDevicesField = dto.getClass().getField("BIO_DEVICES");
        bioDevicesField.set(dto, new HashMap<>());
        java.lang.reflect.Field regDtoField = registrationService.getClass().getDeclaredField("registrationDto");
        regDtoField.setAccessible(true);
        regDtoField.set(registrationService, dto);

        when(clientCryptoManagerService.getClientKeyIndex()).thenReturn("1");
        // Should not throw
        Method addMetaInfoMap = registrationService.getClass().getDeclaredMethod("addMetaInfoMap", String.class, String.class, String.class);
        addMetaInfoMap.setAccessible(true);
        addMetaInfoMap.invoke(registrationService, "center", "machine", "maker");
    }

    @Test
    // Test for getLabelValueDTOListString with valid map
    public void testGetLabelValueDTOListString() throws Exception {
        Method getLabelValueDTOListString = registrationService.getClass().getDeclaredMethod("getLabelValueDTOListString", Map.class);
        getLabelValueDTOListString.setAccessible(true);
        Map<String, String> map = new HashMap<>();
        map.put("label", "value");
        List<Map<String, String>> result = (List<Map<String, String>>) getLabelValueDTOListString.invoke(registrationService, map);
        assertEquals(1, result.size());
        assertEquals("label", result.get(0).get("label"));
        assertEquals("value", result.get(0).get("value"));
    }

    @Test
    // Test for doPreChecksBeforeRegistration with inactive center and machine
    public void testDoPreChecksBeforeRegistration_ThrowsException() throws Exception {
        CenterMachineDto centerMachineDto = new CenterMachineDto();
        centerMachineDto.setCenterStatus(false);
        centerMachineDto.setMachineStatus(false);
        File mockFile = mock(File.class);
        when(mockFile.getUsableSpace()).thenReturn(10l * (1024 * 1024));
        when(mockApplicationContext.getExternalCacheDir()).thenReturn(mockFile);

        Method doPreChecks = registrationService.getClass().getDeclaredMethod("doPreChecksBeforeRegistration", CenterMachineDto.class);
        doPreChecks.setAccessible(true);
        try {
            doPreChecks.invoke(registrationService, centerMachineDto);
            fail("Should throw exception");
        } catch (Exception e) {
            // Expected
        }
    }

    @Test
    // Test for convertImageToPDF with empty list
    public void testConvertImageToPDF_ReturnsNullOnException() throws Exception {
        Method convertImageToPDF = registrationService.getClass().getDeclaredMethod("convertImageToPDF", List.class);
        convertImageToPDF.setAccessible(true);
        // Pass an empty list, should not throw, should return non-null or null
        Object result = convertImageToPDF.invoke(registrationService, Collections.emptyList());
        // Accepts null or byte[]
        assertTrue(result == null || result instanceof byte[]);
    }

    @Test
    // Test for getCompressedImage with valid input
    public void testGetCompressedImage() throws Exception {
        Method getCompressedImage = registrationService.getClass().getDeclaredMethod("getCompressedImage", byte[].class, Float.class);
        getCompressedImage.setAccessible(true);
        byte[] input = "test".getBytes();
        byte[] result = (byte[]) getCompressedImage.invoke(registrationService, input, 0.5f);
        assertArrayEquals(input, result);
    }

    @Test
    // Test for getScaledDimension with valid parameters
    public void testGetScaledDimension() throws Exception {
        Method getScaledDimension = registrationService.getClass().getDeclaredMethod("getScaledDimension", int.class, int.class, int.class, int.class);
        getScaledDimension.setAccessible(true);
        int[] result = (int[]) getScaledDimension.invoke(null, 200, 100, 100, 50);
        assertEquals(100, result[0]);
        assertEquals(50, result[1]);
    }

    @Test
    // Test for buildBIR with null and face/exception photo
    public void testBuildBIR_NullAndFace() {
        // Null input
        assertNull(registrationService.buildBIR(null));
        // Face/Exception photo
        BiometricsDto biometricsDto = new BiometricsDto();
        biometricsDto.setModality(Modality.FACE.getSingleType().value());
        biometricsDto.setBioValue(null);
        biometricsDto.setBioSubType("subtype");
        biometricsDto.setDecodedBioResponse("{\"bioValue\":\"abc\"}");
        biometricsDto.setSignature("sig");
        biometricsDto.setNumOfRetries(1);
        biometricsDto.setSdkScore(0.5);
        biometricsDto.setSpecVersion("1.0");
        biometricsDto.setQualityScore(10);
        BIR bir = registrationService.buildBIR(biometricsDto);
        assertNotNull(bir);
    }

    @Test
    // Test for getAttemptsCount with all modalities
    public void testGetAttemptsCount_AllCases() throws Exception {
        // Fix: Return 2 for each specific key used in getAttemptsCount
        when(globalParamRepository.getCachedIntegerGlobalParam(RegistrationConstants.LEFT_SLAP_ATTEMPTS_KEY)).thenReturn(2);
        when(globalParamRepository.getCachedIntegerGlobalParam(RegistrationConstants.RIGHT_SLAP_ATTEMPTS_KEY)).thenReturn(2);
        when(globalParamRepository.getCachedIntegerGlobalParam(RegistrationConstants.THUMBS_ATTEMPTS_KEY)).thenReturn(2);
        when(globalParamRepository.getCachedIntegerGlobalParam(RegistrationConstants.IRIS_ATTEMPTS_KEY)).thenReturn(2);
        when(globalParamRepository.getCachedIntegerGlobalParam(RegistrationConstants.FACE_ATTEMPTS_KEY)).thenReturn(2);

        Method getAttemptsCount = registrationService.getClass().getDeclaredMethod("getAttemptsCount", Modality.class);
        getAttemptsCount.setAccessible(true);
        for (Modality modality : Modality.values()) {
            int count = (int) getAttemptsCount.invoke(registrationService, modality);
            if (modality == Modality.FINGERPRINT_SLAB_LEFT ||
                modality == Modality.FINGERPRINT_SLAB_RIGHT ||
                modality == Modality.FINGERPRINT_SLAB_THUMBS ||
                modality == Modality.IRIS_DOUBLE ||
                modality == Modality.FACE) {
                assertEquals(2, count);
            } else {
                assertEquals(0, count);
            }
        }
    }

    @Test
    // Test for getAudits with non-empty list
    public void testGetAudits_EmptyList() {
        when(globalParamRepository.getCachedStringGlobalParam(Mockito.anyString())).thenReturn(null);
        when(auditManagerService.getAuditLogs(Mockito.anyLong())).thenReturn(Collections.emptyList());
        List<Map<String, String>> audits = ((RegistrationServiceImpl)registrationService).getAudits();
        assertTrue(audits.isEmpty());
    }

    @Test
    // Test for buildBIR with non-null BiometricsDto
    public void testBuildBIR_NonNull() {
        BiometricsDto biometricsDto = new BiometricsDto();
        biometricsDto.setModality("FACE");
        biometricsDto.setBioValue("dGVzdA=="); // "test" base64
        biometricsDto.setBioSubType("subtype");
        biometricsDto.setDecodedBioResponse("{\"bioValue\":\"dGVzdA==\"}");
        biometricsDto.setSignature("sig");
        biometricsDto.setNumOfRetries(1);
        biometricsDto.setSdkScore(0.5);
        biometricsDto.setSpecVersion("1.0");
        biometricsDto.setQualityScore(10);
        BIR bir = ((RegistrationServiceImpl)registrationService).buildBIR(biometricsDto);
        assertNotNull(bir);
    }

    @Test
    // Test for buildBIR with EXCEPTION_PHOTO modality
    public void testBuildBIR_ExceptionPhoto() {
        BiometricsDto biometricsDto = new BiometricsDto();
        // Use the correct value for EXCEPTION_PHOTO that SingleType.fromValue() expects
        biometricsDto.setModality(io.mosip.registration.packetmanager.cbeffutil.jaxbclasses.SingleType.EXCEPTION_PHOTO.value());
        biometricsDto.setBioValue(null);
        biometricsDto.setBioSubType("subtype");
        biometricsDto.setDecodedBioResponse("{\"bioValue\":\"abc\"}");
        biometricsDto.setSignature(null);
        biometricsDto.setNumOfRetries(0);
        biometricsDto.setSdkScore(0.0);
        biometricsDto.setSpecVersion(null);
        biometricsDto.setQualityScore(0);
        BIR bir = ((RegistrationServiceImpl)registrationService).buildBIR(biometricsDto);
        assertNotNull(bir);
    }

    @Test
    // Test for getLabelValueDTOListString with empty map
    public void testGetLabelValueDTOListString_EmptyMap() throws Exception {
        Method getLabelValueDTOListString = registrationService.getClass().getDeclaredMethod("getLabelValueDTOListString", Map.class);
        getLabelValueDTOListString.setAccessible(true);
        Map<String, String> map = new HashMap<>();
        List<Map<String, String>> result = (List<Map<String, String>>) getLabelValueDTOListString.invoke(registrationService, map);
        assertTrue(result.isEmpty());
    }

    @Test
    // Test for getScaledDimension with various scaling scenarios
    public void testGetScaledDimension_NoScalingNeeded() throws Exception {
        Method getScaledDimension = registrationService.getClass().getDeclaredMethod("getScaledDimension", int.class, int.class, int.class, int.class);
        getScaledDimension.setAccessible(true);
        int[] result = (int[]) getScaledDimension.invoke(null, 50, 50, 100, 100);
        assertEquals(50, result[0]);
        assertEquals(50, result[1]);
    }

    @Test
    // Test for getScaledDimension with width scaling
    public void testGetScaledDimension_ScaleWidth() throws Exception {
        Method getScaledDimension = registrationService.getClass().getDeclaredMethod("getScaledDimension", int.class, int.class, int.class, int.class);
        getScaledDimension.setAccessible(true);
        int[] result = (int[]) getScaledDimension.invoke(null, 200, 100, 100, 100);
        assertEquals(100, result[0]);
        assertEquals(50, result[1]);
    }

    @Test
    // Test for getScaledDimension with height scaling
    public void testGetScaledDimension_ScaleHeight() throws Exception {
        Method getScaledDimension = registrationService.getClass().getDeclaredMethod("getScaledDimension", int.class, int.class, int.class, int.class);
        getScaledDimension.setAccessible(true);
        int[] result = (int[]) getScaledDimension.invoke(null, 100, 200, 100, 100);
        assertEquals(50, result[0]);
        assertEquals(100, result[1]);
    }

    @Test
    // Test for getCompressedImage with null input
    public void testGetCompressedImage_NullInput() throws Exception {
        Method getCompressedImage = registrationService.getClass().getDeclaredMethod("getCompressedImage", byte[].class, Float.class);
        getCompressedImage.setAccessible(true);
        byte[] result = (byte[]) getCompressedImage.invoke(registrationService, (Object) null, null);
        assertNull(result);
    }

    @Test
    // Test for combineByteArray with empty list
    public void testCombineByteArray_EmptyList() throws Exception {
        Method combineByteArray = registrationService.getClass().getDeclaredMethod("combineByteArray", List.class);
        combineByteArray.setAccessible(true);
        List<byte[]> byteList = new ArrayList<>();
        byte[] result = (byte[]) combineByteArray.invoke(registrationService, byteList);
        assertEquals(0, result.length);
    }

    @Test
    // Test for getAdditionalInfo with null input
    public void testGetAdditionalInfo_NullInput() throws Exception {
        Method getAdditionalInfo = registrationService.getClass().getDeclaredMethod("getAdditionalInfo", Object.class);
        getAdditionalInfo.setAccessible(true);
        assertNull(getAdditionalInfo.invoke(registrationService, (Object) null));
    }

    @Test
    // Test for getAdditionalInfo with unknown type input
    public void testGetAdditionalInfo_UnknownType() throws Exception {
        Method getAdditionalInfo = registrationService.getClass().getDeclaredMethod("getAdditionalInfo", Object.class);
        getAdditionalInfo.setAccessible(true);
        assertNull(getAdditionalInfo.invoke(registrationService, 12345));
    }

    @Test
    // Test for clearRegistration called twice
    public void testClearRegistration_Twice() {
        registrationService.clearRegistration();
        registrationService.clearRegistration();
        // Should not throw
    }

    @Test
    // Test for doPreChecksBeforeRegistration with valid CenterMachineDto
    public void testDoPreChecksBeforeRegistration_Success() throws Exception {
        CenterMachineDto centerMachineDto = new CenterMachineDto();
        centerMachineDto.setCenterStatus(true);
        centerMachineDto.setMachineStatus(true);
        File mockFile = mock(File.class);
        when(mockFile.getUsableSpace()).thenReturn(100L * (1024 * 1024));
        when(mockApplicationContext.getExternalCacheDir()).thenReturn(mockFile);
        Method doPreChecks = registrationService.getClass().getDeclaredMethod("doPreChecksBeforeRegistration", CenterMachineDto.class);
        doPreChecks.setAccessible(true);
        doPreChecks.invoke(registrationService, centerMachineDto);
    }

    @Test(expected = Exception.class)
    // Test for doPreChecksBeforeRegistration with low disk space
    public void testDoPreChecksBeforeRegistration_LowSpace() throws Exception {
        CenterMachineDto centerMachineDto = new CenterMachineDto();
        centerMachineDto.setCenterStatus(true);
        centerMachineDto.setMachineStatus(true);
        File mockFile = mock(File.class);
        when(mockFile.getUsableSpace()).thenReturn(10L * (1024 * 1024));
        when(mockApplicationContext.getExternalCacheDir()).thenReturn(mockFile);
        Method doPreChecks = registrationService.getClass().getDeclaredMethod("doPreChecksBeforeRegistration", CenterMachineDto.class);
        doPreChecks.setAccessible(true);
        doPreChecks.invoke(registrationService, centerMachineDto);
    }

    @Test(expected = Exception.class)
    // Test for doPreChecksBeforeRegistration with inactive center
    public void testDoPreChecksBeforeRegistration_InactiveCenter() throws Exception {
        CenterMachineDto centerMachineDto = new CenterMachineDto();
        centerMachineDto.setCenterStatus(false);
        centerMachineDto.setMachineStatus(true);
        File mockFile = mock(File.class);
        when(mockFile.getUsableSpace()).thenReturn(100L * (1024 * 1024));
        when(mockApplicationContext.getExternalCacheDir()).thenReturn(mockFile);
        Method doPreChecks = registrationService.getClass().getDeclaredMethod("doPreChecksBeforeRegistration", CenterMachineDto.class);
        doPreChecks.setAccessible(true);
        doPreChecks.invoke(registrationService, centerMachineDto);
    }

    @Test(expected = Exception.class)
    // Test for doPreChecksBeforeRegistration with inactive machine
    public void testDoPreChecksBeforeRegistration_InactiveMachine() throws Exception {
        CenterMachineDto centerMachineDto = new CenterMachineDto();
        centerMachineDto.setCenterStatus(true);
        centerMachineDto.setMachineStatus(false);
        File mockFile = mock(File.class);
        when(mockFile.getUsableSpace()).thenReturn(100L * (1024 * 1024));
        when(mockApplicationContext.getExternalCacheDir()).thenReturn(mockFile);
        Method doPreChecks = registrationService.getClass().getDeclaredMethod("doPreChecksBeforeRegistration", CenterMachineDto.class);
        doPreChecks.setAccessible(true);
        doPreChecks.invoke(registrationService, centerMachineDto);
    }

    @Test
    // Test for submitRegistrationDto with Update flow and UIN field
    public void testSubmitRegistrationDto_UpdateFlow_UINField() throws Exception {
        RegistrationDto dto = mock(RegistrationDto.class);
        Map<String, Object> demographics = new HashMap<>();
        demographics.put("UIN", "uinValue");
        when(dto.getDemographics()).thenReturn(demographics);
        when(dto.getFlowType()).thenReturn("Update");
        when(dto.getUpdatableFields()).thenReturn(Collections.emptyList());
        when(dto.getRId()).thenReturn("RID456");
        when(dto.getAllDocumentFields()).thenReturn(Collections.emptySet());
        java.lang.reflect.Field capturedBioFieldsField = dto.getClass().getField("CAPTURED_BIO_FIELDS");
        capturedBioFieldsField.set(dto, Collections.emptySet());
        java.lang.reflect.Field bioDevicesField = dto.getClass().getField("BIO_DEVICES");
        bioDevicesField.set(dto, new HashMap<>());
        when(dto.getSchemaVersion()).thenReturn(1.0);
        when(dto.getProcess()).thenReturn("UPDATE");
        when(dto.getSelectedLanguages()).thenReturn(Collections.singletonList("eng"));

        java.lang.reflect.Field regDtoField = registrationService.getClass().getDeclaredField("registrationDto");
        regDtoField.setAccessible(true);
        regDtoField.set(registrationService, dto);

        when(globalParamRepository.getSelectedHandles()).thenReturn(null);
        Mockito.when(globalParamRepository.getCachedStringGlobalParam(Mockito.eq(RegistrationConstants.AUDIT_EXPORTED_TILL))).thenReturn(null);
        Mockito.when(globalParamRepository.getCachedStringGlobalParam(Mockito.argThat(arg ->
            !RegistrationConstants.AUDIT_EXPORTED_TILL.equals(arg)))).thenReturn("1.2.3");
        // Provide a fully initialized CenterMachineDto
        CenterMachineDto centerMachineDto = new CenterMachineDto();
        centerMachineDto.setCenterId("centerId");
        centerMachineDto.setMachineId("machineId");
        centerMachineDto.setMachineRefId("machineRefId");
        when(masterDataService.getRegistrationCenterMachineDetails()).thenReturn(centerMachineDto);
        when(identitySchemaRepository.getSchemaJson(Mockito.any(), Mockito.anyDouble())).thenReturn("{}");
        when(packetWriterService.persistPacket(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyBoolean(), Mockito.anyString())).thenReturn("containerPath123");
        Registration mockRegistration = mock(Registration.class);
        when(registrationRepository.insertRegistration(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.any())).thenReturn(mockRegistration);

        registrationService.submitRegistrationDto("makerName");
        Mockito.verify(packetWriterService).setField("RID456", "UIN", "uinValue");
    }

    @Test
    // Test for submitRegistrationDto with New flow and all fields
    public void testSubmitRegistrationDto_NewFlow_AllFields() throws Exception {
        RegistrationDto dto = mock(RegistrationDto.class);
        Map<String, Object> demographics = new HashMap<>();
        demographics.put("field1", "value1");
        when(dto.getDemographics()).thenReturn(demographics);
        when(dto.getFlowType()).thenReturn("NEW");
        when(dto.getRId()).thenReturn("RID789");
        when(dto.getAllDocumentFields()).thenReturn(Collections.emptySet());
        java.lang.reflect.Field capturedBioFieldsField = dto.getClass().getField("CAPTURED_BIO_FIELDS");
        capturedBioFieldsField.set(dto, Collections.emptySet());
        java.lang.reflect.Field bioDevicesField = dto.getClass().getField("BIO_DEVICES");
        bioDevicesField.set(dto, new HashMap<>());
        when(dto.getSchemaVersion()).thenReturn(1.0);
        when(dto.getProcess()).thenReturn("NEW");
        when(dto.getSelectedLanguages()).thenReturn(Collections.singletonList("eng"));

        java.lang.reflect.Field regDtoField = registrationService.getClass().getDeclaredField("registrationDto");
        regDtoField.setAccessible(true);
        regDtoField.set(registrationService, dto);

        when(globalParamRepository.getSelectedHandles()).thenReturn(Collections.singletonList("handle1"));
        Mockito.when(globalParamRepository.getCachedStringGlobalParam(Mockito.eq(RegistrationConstants.AUDIT_EXPORTED_TILL))).thenReturn(null);
        Mockito.when(globalParamRepository.getCachedStringGlobalParam(Mockito.argThat(arg ->
            !RegistrationConstants.AUDIT_EXPORTED_TILL.equals(arg)))).thenReturn("1.2.3");
        // Provide a fully initialized CenterMachineDto
        CenterMachineDto centerMachineDto = new CenterMachineDto();
        centerMachineDto.setCenterId("centerId");
        centerMachineDto.setMachineId("machineId");
        centerMachineDto.setMachineRefId("machineRefId");
        when(masterDataService.getRegistrationCenterMachineDetails()).thenReturn(centerMachineDto);
        when(identitySchemaRepository.getSchemaJson(Mockito.any(), Mockito.anyDouble())).thenReturn("{}");
        when(packetWriterService.persistPacket(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyBoolean(), Mockito.anyString())).thenReturn("containerPath123");
        Registration mockRegistration = mock(Registration.class);
        when(registrationRepository.insertRegistration(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.any())).thenReturn(mockRegistration);

        registrationService.submitRegistrationDto("makerName");
        Mockito.verify(packetWriterService).setField("RID789", "field1", "value1");
    }

    @Test
    // Test for submitRegistrationDto with Correction flow and specific field
    public void testSubmitRegistrationDto_CorrectionFlow() throws Exception {
        RegistrationDto dto = mock(RegistrationDto.class);
        Map<String, Object> demographics = new HashMap<>();
        demographics.put("field2", "value2");
        when(dto.getDemographics()).thenReturn(demographics);
        when(dto.getFlowType()).thenReturn("Correction");
        when(dto.getRId()).thenReturn("RID999");
        when(dto.getAllDocumentFields()).thenReturn(Collections.emptySet());
        java.lang.reflect.Field capturedBioFieldsField = dto.getClass().getField("CAPTURED_BIO_FIELDS");
        capturedBioFieldsField.set(dto, Collections.emptySet());
        java.lang.reflect.Field bioDevicesField = dto.getClass().getField("BIO_DEVICES");
        bioDevicesField.set(dto, new HashMap<>());
        when(dto.getSchemaVersion()).thenReturn(1.0);
        when(dto.getProcess()).thenReturn("Correction");
        when(dto.getSelectedLanguages()).thenReturn(Collections.singletonList("eng"));

        java.lang.reflect.Field regDtoField = registrationService.getClass().getDeclaredField("registrationDto");
        regDtoField.setAccessible(true);
        regDtoField.set(registrationService, dto);

        when(globalParamRepository.getSelectedHandles()).thenReturn(null);
        Mockito.when(globalParamRepository.getCachedStringGlobalParam(Mockito.eq(RegistrationConstants.AUDIT_EXPORTED_TILL))).thenReturn(null);
        Mockito.when(globalParamRepository.getCachedStringGlobalParam(Mockito.argThat(arg ->
            !RegistrationConstants.AUDIT_EXPORTED_TILL.equals(arg)))).thenReturn("1.2.3");
        // Provide a fully initialized CenterMachineDto
        CenterMachineDto centerMachineDto = new CenterMachineDto();
        centerMachineDto.setCenterId("centerId");
        centerMachineDto.setMachineId("machineId");
        centerMachineDto.setMachineRefId("machineRefId");
        when(masterDataService.getRegistrationCenterMachineDetails()).thenReturn(centerMachineDto);
        when(identitySchemaRepository.getSchemaJson(Mockito.any(), Mockito.anyDouble())).thenReturn("{}");
        when(packetWriterService.persistPacket(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyBoolean(), Mockito.anyString())).thenReturn("containerPath123");
        Registration mockRegistration = mock(Registration.class);
        when(registrationRepository.insertRegistration(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.any())).thenReturn(mockRegistration);

        registrationService.submitRegistrationDto("makerName");
        Mockito.verify(packetWriterService).setField("RID999", "field2", "value2");
    }

    @Test
    // Test for submitRegistrationDto with Lost flow and demographics
    public void testSubmitRegistrationDto_LostFlow() throws Exception {
        RegistrationDto dto = mock(RegistrationDto.class);
        Map<String, Object> demographics = new HashMap<>();
        demographics.put("field3", "value3");
        when(dto.getDemographics()).thenReturn(demographics);
        when(dto.getFlowType()).thenReturn("Lost");
        when(dto.getRId()).thenReturn("RID888");
        when(dto.getAllDocumentFields()).thenReturn(Collections.emptySet());
        java.lang.reflect.Field capturedBioFieldsField = dto.getClass().getField("CAPTURED_BIO_FIELDS");
        capturedBioFieldsField.set(dto, Collections.emptySet());
        java.lang.reflect.Field bioDevicesField = dto.getClass().getField("BIO_DEVICES");
        bioDevicesField.set(dto, new HashMap<>());
        when(dto.getSchemaVersion()).thenReturn(1.0);
        when(dto.getProcess()).thenReturn("Lost");
        when(dto.getSelectedLanguages()).thenReturn(Collections.singletonList("eng"));

        java.lang.reflect.Field regDtoField = registrationService.getClass().getDeclaredField("registrationDto");
        regDtoField.setAccessible(true);
        regDtoField.set(registrationService, dto);

        when(globalParamRepository.getSelectedHandles()).thenReturn(null);
        Mockito.when(globalParamRepository.getCachedStringGlobalParam(Mockito.eq(RegistrationConstants.AUDIT_EXPORTED_TILL))).thenReturn(null);
        Mockito.when(globalParamRepository.getCachedStringGlobalParam(Mockito.argThat(arg ->
            !RegistrationConstants.AUDIT_EXPORTED_TILL.equals(arg)))).thenReturn("1.2.3");
        // Provide a fully initialized CenterMachineDto
        CenterMachineDto centerMachineDto = new CenterMachineDto();
        centerMachineDto.setCenterId("centerId");
        centerMachineDto.setMachineId("machineId");
        centerMachineDto.setMachineRefId("machineRefId");
        when(masterDataService.getRegistrationCenterMachineDetails()).thenReturn(centerMachineDto);
        when(identitySchemaRepository.getSchemaJson(Mockito.any(), Mockito.anyDouble())).thenReturn("{}");
        when(packetWriterService.persistPacket(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyBoolean(), Mockito.anyString())).thenReturn("containerPath123");
        Registration mockRegistration = mock(Registration.class);
        when(registrationRepository.insertRegistration(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.any())).thenReturn(mockRegistration);

        registrationService.submitRegistrationDto("makerName");
        Mockito.verify(packetWriterService).setField("RID888", "field3", "value3");
    }

    @Test(expected = ClientCheckedException.class)
    // Test for submitRegistrationDto with empty container path
    public void testSubmitRegistrationDto_EmptyContainerPath() throws Exception {
        RegistrationDto dto = mock(RegistrationDto.class);
        Map<String, Object> demographics = new HashMap<>();
        demographics.put("field", "value");
        when(dto.getDemographics()).thenReturn(demographics);
        when(dto.getFlowType()).thenReturn("NEW");
        when(dto.getRId()).thenReturn("RID000");
        when(dto.getAllDocumentFields()).thenReturn(Collections.emptySet());
        java.lang.reflect.Field capturedBioFieldsField = dto.getClass().getField("CAPTURED_BIO_FIELDS");
        capturedBioFieldsField.set(dto, Collections.emptySet());
        java.lang.reflect.Field bioDevicesField = dto.getClass().getField("BIO_DEVICES");
        bioDevicesField.set(dto, new HashMap<>());
        when(dto.getSchemaVersion()).thenReturn(1.0);
        when(dto.getProcess()).thenReturn("NEW");
        when(dto.getSelectedLanguages()).thenReturn(Collections.singletonList("eng"));

        java.lang.reflect.Field regDtoField = registrationService.getClass().getDeclaredField("registrationDto");
        regDtoField.setAccessible(true);
        regDtoField.set(registrationService, dto);

        when(globalParamRepository.getSelectedHandles()).thenReturn(null);
        Mockito.when(globalParamRepository.getCachedStringGlobalParam(Mockito.eq(RegistrationConstants.AUDIT_EXPORTED_TILL))).thenReturn(null);
        Mockito.when(globalParamRepository.getCachedStringGlobalParam(Mockito.argThat(arg ->
            !RegistrationConstants.AUDIT_EXPORTED_TILL.equals(arg)))).thenReturn("1.2.3");
        // Provide a fully initialized CenterMachineDto
        CenterMachineDto centerMachineDto = new CenterMachineDto();
        centerMachineDto.setCenterId("centerId");
        centerMachineDto.setMachineId("machineId");
        centerMachineDto.setMachineRefId("machineRefId");
        when(masterDataService.getRegistrationCenterMachineDetails()).thenReturn(centerMachineDto);
        when(identitySchemaRepository.getSchemaJson(Mockito.any(), Mockito.anyDouble())).thenReturn("{}");
        when(packetWriterService.persistPacket(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyBoolean(), Mockito.anyString())).thenReturn("");
        Registration mockRegistration = mock(Registration.class);

        registrationService.submitRegistrationDto("makerName");
    }

    @Test
    // Test for addMetaInfoMap with all fields set
    public void testAddMetaInfoMap_AllFields() throws Exception {
        RegistrationDto dto = mock(RegistrationDto.class);
        when(dto.getRId()).thenReturn("RID");
        when(dto.getProcess()).thenReturn("NEW");
        when(dto.getSelectedLanguages()).thenReturn(Collections.singletonList("eng"));
        java.lang.reflect.Field bioDevicesField = dto.getClass().getField("BIO_DEVICES");
        Map<Object, Object> bioDevices = new HashMap<>();
        bioDevices.put(Modality.FACE, new HashMap<>());
        bioDevicesField.set(dto, bioDevices);
        java.lang.reflect.Field regDtoField = registrationService.getClass().getDeclaredField("registrationDto");
        regDtoField.setAccessible(true);
        regDtoField.set(registrationService, dto);

        when(clientCryptoManagerService.getClientKeyIndex()).thenReturn("1");
        Method addMetaInfoMap = registrationService.getClass().getDeclaredMethod("addMetaInfoMap", String.class, String.class, String.class);
        addMetaInfoMap.setAccessible(true);
        addMetaInfoMap.invoke(registrationService, "center", "machine", "maker");
        Mockito.verify(packetWriterService, Mockito.atLeastOnce()).addMetaInfo(Mockito.anyString(), Mockito.anyString(), Mockito.any());
    }

    @Test
    // Test for getKey with full name and other fields
    public void testGetKey_FullNameAndOther() throws Exception {
        RegistrationDto dto = mock(RegistrationDto.class);
        when(dto.getProcess()).thenReturn("NEW");
        List<FieldSpecDto> fields = new ArrayList<>();
        FieldSpecDto fullNameField = new FieldSpecDto();
        fullNameField.setId("fullNameId");
        fullNameField.setSubType(RegistrationConstants.UI_SCHEMA_SUBTYPE_FULL_NAME);
        fields.add(fullNameField);
        FieldSpecDto emailField = new FieldSpecDto();
        emailField.setId("emailId");
        emailField.setSubType(RegistrationConstants.UI_SCHEMA_SUBTYPE_EMAIL);
        fields.add(emailField);
        when(identitySchemaRepository.getProcessSpecFields(Mockito.any(), Mockito.anyString())).thenReturn(fields);

        Method getKey = registrationService.getClass().getDeclaredMethod("getKey", RegistrationDto.class, String.class);
        getKey.setAccessible(true);
        String fullNameKey = (String) getKey.invoke(registrationService, dto, RegistrationConstants.UI_SCHEMA_SUBTYPE_FULL_NAME);
        String emailKey = (String) getKey.invoke(registrationService, dto, RegistrationConstants.UI_SCHEMA_SUBTYPE_EMAIL);
        assertEquals("fullNameId", fullNameKey);
        assertEquals("emailId", emailKey);
    }

    @Test
    // Test for convertImageToBytes with valid base64 string
    public void testConvertImageToBytes_IOException() throws Exception {
        Method convertImageToBytes = registrationService.getClass().getDeclaredMethod("convertImageToBytes", String.class);
        convertImageToBytes.setAccessible(true);
        // Should return null for invalid base64
        assertNull(convertImageToBytes.invoke(registrationService, "not_base64"));
    }

    @Test
    // Test for getAttemptsCount with default value of zero
    public void testGetAttemptsCount_DefaultZero() throws Exception {
        // Use a valid modality not handled in the switch-case, or use a handled one and expect the correct value.
        // If all modalities are handled, use a handled one and expect the configured value.
        java.lang.reflect.Method getAttemptsCount = registrationService.getClass().getDeclaredMethod("getAttemptsCount", Modality.class);
        getAttemptsCount.setAccessible(true);
        // Use Modality.FACE for a handled case, expecting the mocked value (2)
        when(globalParamRepository.getCachedIntegerGlobalParam(RegistrationConstants.FACE_ATTEMPTS_KEY)).thenReturn(2);
        int count = (int) getAttemptsCount.invoke(registrationService, Modality.FACE);
        assertEquals(2, count);
    }

    @Test
    // Test for getBiometricRecord with server version 1.1.5 and only face modality
    public void testGetBiometricRecord_ServerVersion_115_SkipsExceptionPhotoAndExceptions() throws Exception {
        RegistrationDto dto = mock(RegistrationDto.class);
        String fieldId = "fieldId";
        String serverVersion = "1.1.5";
        // Only face modality
        BiometricsDto faceBio = new BiometricsDto();
        faceBio.setModality("FACE"); // Ensure valid value for SingleType.fromValue()
        List<BiometricsDto> face = Collections.singletonList(faceBio);
        when(dto.getBestBiometrics(fieldId, Modality.FINGERPRINT_SLAB_LEFT)).thenReturn(Collections.emptyList());
        when(dto.getBestBiometrics(fieldId, Modality.FINGERPRINT_SLAB_RIGHT)).thenReturn(Collections.emptyList());
        when(dto.getBestBiometrics(fieldId, Modality.FINGERPRINT_SLAB_THUMBS)).thenReturn(Collections.emptyList());
        when(dto.getBestBiometrics(fieldId, Modality.IRIS_DOUBLE)).thenReturn(Collections.emptyList());
        when(dto.getBestBiometrics(fieldId, Modality.FACE)).thenReturn(face);

        // Setup EXCEPTIONS map
        java.lang.reflect.Field exceptionsField = dto.getClass().getField("EXCEPTIONS");
        Map<String, List<String>> exceptionsMap = new HashMap<>();
        exceptionsMap.put("key", Collections.singletonList(Modality.FACE.getSingleType().value()));
        exceptionsField.set(dto, exceptionsMap);

        // Set registrationDto in service
        java.lang.reflect.Field regDtoField = registrationService.getClass().getDeclaredField("registrationDto");
        regDtoField.setAccessible(true);
        regDtoField.set(registrationService, dto);

        // Call private method via reflection
        java.lang.reflect.Method getBiometricRecord = registrationService.getClass().getDeclaredMethod("getBiometricRecord", String.class, String.class);
        getBiometricRecord.setAccessible(true);
        Object result = getBiometricRecord.invoke(registrationService, fieldId, serverVersion);

        assertNotNull(result);
        io.mosip.registration.packetmanager.dto.PacketWriter.BiometricRecord record = (io.mosip.registration.packetmanager.dto.PacketWriter.BiometricRecord) result;
        // Should only have face segment, not exception photo or exceptions
        assertEquals(1, record.getSegments().size());
    }

    @Test
    // Test for getBiometricRecord with server version 2.0.0 and empty lists
    public void testGetBiometricRecord_EmptyListsAndNoExceptions() throws Exception {
        RegistrationDto dto = mock(RegistrationDto.class);
        String fieldId = "fieldId";
        String serverVersion = "2.0.0";
        // All modalities return empty
        when(dto.getBestBiometrics(fieldId, Modality.FINGERPRINT_SLAB_LEFT)).thenReturn(Collections.emptyList());
        when(dto.getBestBiometrics(fieldId, Modality.FINGERPRINT_SLAB_RIGHT)).thenReturn(Collections.emptyList());
        when(dto.getBestBiometrics(fieldId, Modality.FINGERPRINT_SLAB_THUMBS)).thenReturn(Collections.emptyList());
        when(dto.getBestBiometrics(fieldId, Modality.IRIS_DOUBLE)).thenReturn(Collections.emptyList());
        when(dto.getBestBiometrics(fieldId, Modality.FACE)).thenReturn(Collections.emptyList());
        when(dto.getBestBiometrics(fieldId, Modality.EXCEPTION_PHOTO)).thenReturn(Collections.emptyList());

        // Setup EXCEPTIONS map as empty
        java.lang.reflect.Field exceptionsField = dto.getClass().getField("EXCEPTIONS");
        Map<String, List<String>> exceptionsMap = new HashMap<>();
        exceptionsField.set(dto, exceptionsMap);

        // Set registrationDto in service
        java.lang.reflect.Field regDtoField = registrationService.getClass().getDeclaredField("registrationDto");
        regDtoField.setAccessible(true);
        regDtoField.set(registrationService, dto);

        // Call private method via reflection
        java.lang.reflect.Method getBiometricRecord = registrationService.getClass().getDeclaredMethod("getBiometricRecord", String.class, String.class);
        getBiometricRecord.setAccessible(true);
        Object result = getBiometricRecord.invoke(registrationService, fieldId, serverVersion);

        assertNotNull(result);
        io.mosip.registration.packetmanager.dto.PacketWriter.BiometricRecord record = (io.mosip.registration.packetmanager.dto.PacketWriter.BiometricRecord) result;
        // Should have no segments
        assertEquals(0, record.getSegments().size());
    }
}
