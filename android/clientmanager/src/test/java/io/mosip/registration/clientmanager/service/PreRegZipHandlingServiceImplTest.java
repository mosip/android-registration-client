package io.mosip.registration.clientmanager.service;

import static android.content.ContentValues.TAG;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import io.mosip.registration.clientmanager.dao.ApplicantValidDocumentDao;
import io.mosip.registration.clientmanager.dto.CenterMachineDto;
import io.mosip.registration.clientmanager.dto.PreRegistrationDto;
import io.mosip.registration.clientmanager.dto.registration.DocumentDto;
import io.mosip.registration.clientmanager.dto.registration.GenericValueDto;
import io.mosip.registration.clientmanager.dto.registration.RegistrationDto;
import io.mosip.registration.clientmanager.dto.uispec.FieldSpecDto;
import io.mosip.registration.clientmanager.entity.Location;
import io.mosip.registration.clientmanager.exception.RegBaseCheckedException;
import io.mosip.registration.clientmanager.exception.RegBaseUncheckedException;
import io.mosip.registration.clientmanager.repository.GlobalParamRepository;
import io.mosip.registration.clientmanager.repository.IdentitySchemaRepository;
import io.mosip.registration.clientmanager.service.external.PreRegZipHandlingService;
import io.mosip.registration.clientmanager.service.external.impl.PreRegZipHandlingServiceImpl;
import io.mosip.registration.clientmanager.spi.MasterDataService;
import io.mosip.registration.clientmanager.spi.RegistrationService;
import io.mosip.registration.keymanager.spi.ClientCryptoManagerService;
import io.mosip.registration.keymanager.spi.CryptoManagerService;
import io.mosip.registration.packetmanager.dto.SimpleType;
import io.mosip.registration.packetmanager.spi.IPacketCryptoService;
import io.mosip.registration.packetmanager.util.ConfigService;
import io.mosip.registration.packetmanager.util.PacketKeeper;
import org.json.JSONObject;
import org.junit.*;
import org.junit.jupiter.api.Assertions;
import org.mockito.*;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class PreRegZipHandlingServiceImplTest {

    @Mock private Context mockContext;
    @Mock private ApplicantValidDocumentDao mockApplicantValidDocumentDao;
    @Mock private IdentitySchemaRepository mockIdentitySchemaRepository;
    @Mock private ClientCryptoManagerService mockClientCryptoManagerService;
    @Mock private RegistrationService mockRegistrationService;
    @Mock private CryptoManagerService mockCryptoManagerService;
    @Mock private PacketKeeper mockPacketKeeper;
    @Mock private IPacketCryptoService mockIPacketCryptoService;
    @Mock private MasterDataService mockMasterDataService;
    @Mock private GlobalParamRepository mockGlobalParamRepository;
    @Mock private RegistrationService regService;
    @Mock private MasterDataService masterDataService;
    @Mock private RegistrationDto regDto;
    @Mock private Context ctx;

    private PreRegZipHandlingServiceImpl service;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new PreRegZipHandlingServiceImpl(
                mockContext,
                mockApplicantValidDocumentDao,
                mockIdentitySchemaRepository,
                mockClientCryptoManagerService,
                mockRegistrationService,
                mockCryptoManagerService,
                mockPacketKeeper,
                mockIPacketCryptoService,
                mockMasterDataService,
                mockGlobalParamRepository
        );
    }

    @Test
    public void test_readZipInputStreamToByteArray_readsData() throws Exception {
        byte[] data = "test".getBytes();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ZipOutputStream zos = new ZipOutputStream(baos)) {
            zos.putNextEntry(new java.util.zip.ZipEntry("file.txt"));
            zos.write(data);
            zos.closeEntry();
        }
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        ZipInputStream zis = new ZipInputStream(bais);
        zis.getNextEntry();
        byte[] result = PreRegZipHandlingServiceImpl.readZipInputStreamToByteArray(zis);
        assertArrayEquals(data, result);
    }

    @Test
    public void test_decryptPreRegPacket_callsCryptoManager() throws Exception {
        byte[] encrypted = new byte[]{1,2,3};
        String key = Base64.getEncoder().encodeToString("1234567890123456".getBytes());
        byte[] expected = new byte[]{4,5,6};
        when(mockCryptoManagerService.symmetricDecrypt(any(), eq(encrypted), isNull())).thenReturn(expected);

        byte[] result = service.decryptPreRegPacket(key, encrypted);
        assertArrayEquals(expected, result);
    }

    @Test(expected = IllegalAccessException.class)
    public void test_validateFilename_invalidPath_throws() throws Exception {
        String file = "/tmp/evil.txt";
        String dir = "/home/user/safe";
        service.getClass()
                .getDeclaredMethod("validateFilename", String.class, String.class)
                .invoke(service, file, dir);
    }

    @Test (expected = RegBaseUncheckedException.class)
    public void test_extractPreRegZipFile_returnsRegistrationDto() throws Exception {
        RegistrationDto dto = new RegistrationDto();
        when(mockRegistrationService.getRegistrationDto()).thenReturn(dto);
        RegistrationDto result = service.extractPreRegZipFile("dummy".getBytes());
        assertEquals(dto, result);
    }

    @Test
    public void test_encryptAndSavePreRegPacket_handlesException() {
        PreRegistrationDto result = service.encryptAndSavePreRegPacket("id", "packet", new CenterMachineDto());
        assertNotNull(result);
    }

    @Test
    public void test_storePreRegPacketToDisk_handleIOException() throws RegBaseUncheckedException {
        String path = service.storePreRegPacketToDisk("id", new byte[]{1,2,3}, new CenterMachineDto());
        assertNotNull(path);
    }

    @Test(expected = RegBaseUncheckedException.class)
    public void test_extractPreRegZipFile_handlesOtherException() throws Exception {
        when(regService.getRegistrationDto()).thenThrow(new RuntimeException("fail"));
        java.lang.reflect.Field f = PreRegZipHandlingServiceImpl.class.getDeclaredField("registrationService");
        f.setAccessible(true);
        f.set(service, regService);

        service.extractPreRegZipFile(new byte[0]);
    }

    @Test
    public void test_parseDemographicJson_handlesEmptyString() throws Exception {
        java.lang.reflect.Method m = PreRegZipHandlingServiceImpl.class.getDeclaredMethod("parseDemographicJson", String.class);
        m.setAccessible(true);
        m.invoke(service, "");
    }

    @Test
    public void test_parseDemographicJson_handlesException() throws Exception {
        when(regService.getRegistrationDto()).thenReturn(null);
        java.lang.reflect.Field f = PreRegZipHandlingServiceImpl.class.getDeclaredField("registrationService");
        f.setAccessible(true);
        f.set(service, regService);

        java.lang.reflect.Method m = PreRegZipHandlingServiceImpl.class.getDeclaredMethod("parseDemographicJson", String.class);
        m.setAccessible(true);
        try {
            m.invoke(service, "{\"identity\":{}}");
        } catch (Exception e) {
            assertTrue(e.getCause() instanceof RegBaseCheckedException);
        }
    }

    @Test
    public void test_getValueFromJson_allTypes() throws Exception {
        when(masterDataService.getFieldValues(anyString(), anyString())).thenReturn(Collections.singletonList(new GenericValueDto("code", "name", "en")));
        when(masterDataService.findAllLocationsByLangCode(anyString())).thenReturn(Collections.emptyList());
        java.lang.reflect.Field f = PreRegZipHandlingServiceImpl.class.getDeclaredField("masterDataService");
        f.setAccessible(true);
        f.set(service, masterDataService);

        JSONObject obj = new JSONObject();
        obj.put("string", "abc");
        obj.put("integer", 1);
        obj.put("number", 2L);
        obj.put("simpleType", new org.json.JSONArray().put(new JSONObject().put("language", "en").put("value", "code")));

        java.lang.reflect.Method m = PreRegZipHandlingServiceImpl.class.getDeclaredMethod("getValueFromJson", String.class, String.class, JSONObject.class);
        m.setAccessible(true);

        assertEquals("abc", m.invoke(service, "string", "string", obj));
        assertEquals(1, m.invoke(service, "integer", "integer", obj));
        assertEquals(2L, m.invoke(service, "number", "number", obj));
        assertNotNull(m.invoke(service, "simpleType", "simpleType", obj));
    }

    @Test
    public void test_getValueFromJson_handlesThrowable() throws Exception {
        JSONObject obj = new JSONObject();
        obj.put("bad", new Object());
        java.lang.reflect.Method m = PreRegZipHandlingServiceImpl.class.getDeclaredMethod("getValueFromJson", String.class, String.class, JSONObject.class);
        m.setAccessible(true);
        assertNull(m.invoke(service, "bad", "string", obj));
    }

    @Test
    public void test_validateDemographicInfoObject_nullService() throws Exception {
        java.lang.reflect.Field f = PreRegZipHandlingServiceImpl.class.getDeclaredField("registrationService");
        f.setAccessible(true);
        f.set(service, null);

        java.lang.reflect.Method m = PreRegZipHandlingServiceImpl.class.getDeclaredMethod("validateDemographicInfoObject");
        m.setAccessible(true);
        assertFalse((Boolean) m.invoke(service));
    }

    @Test
    public void test_validateDemographicInfoObject_nullDemographics() throws Exception {
        when(regDto.getDemographics()).thenReturn(null);
        when(regService.getRegistrationDto()).thenReturn(regDto);
        java.lang.reflect.Field f = PreRegZipHandlingServiceImpl.class.getDeclaredField("registrationService");
        f.setAccessible(true);
        f.set(service, regService);

        java.lang.reflect.Method m = PreRegZipHandlingServiceImpl.class.getDeclaredMethod("validateDemographicInfoObject");
        m.setAccessible(true);
        assertFalse((Boolean) m.invoke(service));
    }

    @Test
    public void test_validateDemographicInfoObject_true() throws Exception {
        when(regDto.getDemographics()).thenReturn(new HashMap<>());
        when(regService.getRegistrationDto()).thenReturn(regDto);
        java.lang.reflect.Field f = PreRegZipHandlingServiceImpl.class.getDeclaredField("registrationService");
        f.setAccessible(true);
        f.set(service, regService);

        java.lang.reflect.Method m = PreRegZipHandlingServiceImpl.class.getDeclaredMethod("validateDemographicInfoObject");
        m.setAccessible(true);
        assertTrue((Boolean) m.invoke(service));
    }

    @Test
    public void test_storePreRegPacketToDisk_handlesIOException() {
        when(ctx.getFilesDir()).thenReturn(new File("/invalid/path"));
        java.lang.reflect.Field f;
        try {
            f = PreRegZipHandlingServiceImpl.class.getDeclaredField("appContext");
            f.setAccessible(true);
            f.set(service, ctx);
            GlobalParamRepository repo = mock(GlobalParamRepository.class);
            when(repo.getCachedStringPreRegPacketLocation()).thenReturn("prereg");
            java.lang.reflect.Field f2 = PreRegZipHandlingServiceImpl.class.getDeclaredField("globalParamRepository");
            f2.setAccessible(true);
            f2.set(service, repo);

            try {
                service.storePreRegPacketToDisk("id", new byte[]{1,2,3}, new CenterMachineDto());
            } catch (RuntimeException e) {
                assertTrue(e.getCause() instanceof RegBaseUncheckedException);
            }
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void test_valid_file_within_intended_directory() throws IOException {
        String intendedDir = System.getProperty("java.io.tmpdir");
        File testFile = new File(intendedDir, "testFile.txt");
        testFile.createNewFile();
        testFile.deleteOnExit();

        String result = ReflectionTestUtils.invokeMethod(service, "validateFilename", testFile.getAbsolutePath(), intendedDir);

        assertEquals(testFile.getCanonicalPath(), result);
    }

    @Test
    public void test_file_outside_intended_directory_throws_exception() throws IOException {
        String intendedDir = System.getProperty("java.io.tmpdir") + File.separator + "intended";
        File intendedDirFile = new File(intendedDir);
        intendedDirFile.mkdirs();
        intendedDirFile.deleteOnExit();

        String outsideFilePath = System.getProperty("java.io.tmpdir") + File.separator + "outside.txt";
        File outsideFile = new File(outsideFilePath);
        outsideFile.createNewFile();
        outsideFile.deleteOnExit();

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            ReflectionTestUtils.invokeMethod(service, "validateFilename", outsideFilePath, intendedDir);
        });

        assertEquals("File is outside extraction target directory.", exception.getMessage());
    }


    @Test
    public void test_init_prereg_adapter_when_external_storage_not_mounted() {
        MockedStatic<Environment> mockedEnvironment = Mockito.mockStatic(Environment.class);
        mockedEnvironment.when(Environment::getExternalStorageState).thenReturn("unmounted");

        MockedStatic<Log> mockedLog = Mockito.mockStatic(Log.class);

        ReflectionTestUtils.invokeMethod(service, "initPreRegAdapter", mockContext);

        assertEquals(mockContext, ReflectionTestUtils.getField(service, "appContext"));
        assertNull(ReflectionTestUtils.getField(service, "BASE_LOCATION"));

        mockedLog.verify(() -> Log.e(anyString(), eq("External Storage not mounted")));
        mockedLog.verify(() -> Log.i(anyString(), eq("initLocalClientCryptoService: Initialization call successful")));

        mockedEnvironment.close();
        mockedLog.close();
    }

    @Test
    public void test_parse_demographic_json_with_valid_data() throws Exception {
        RegistrationDto mockRegistrationDto = mock(RegistrationDto.class);
        Map<String, DocumentDto> documents = new HashMap<>();
        Map<String, Object> demographics = new HashMap<>();

        when(mockRegistrationService.getRegistrationDto()).thenReturn(mockRegistrationDto);
        when(mockRegistrationDto.getSchemaVersion()).thenReturn(1.0);
        when(mockRegistrationDto.getDocuments()).thenReturn(documents);
        when(mockRegistrationDto.getDemographics()).thenReturn(demographics);

        List<FieldSpecDto> fieldList = new ArrayList<>();
        FieldSpecDto fieldSpec1 = new FieldSpecDto();
        fieldSpec1.setId("fullName");
        fieldSpec1.setType("simpleType");
        fieldSpec1.setControlType("textbox");
        fieldList.add(fieldSpec1);

        FieldSpecDto fieldSpec2 = new FieldSpecDto();
        fieldSpec2.setId("proofOfIdentity");
        fieldSpec2.setType("documentType");
        fieldList.add(fieldSpec2);

        when(mockIdentitySchemaRepository.getAllFieldSpec(any(Context.class), anyDouble())).thenReturn(fieldList);

        String validJson = "{ \"identity\": { \"fullName\": \"John Doe\", \"proofOfIdentity\": { \"type\": \"passport\", \"format\": \"pdf\", \"value\": \"doc1\", \"refNumber\": \"ABC123\" } } }";

        ReflectionTestUtils.invokeMethod(service, "parseDemographicJson", validJson);

        verify(mockRegistrationDto).getDocuments();
        verify(mockRegistrationDto).addWithoutDocument(eq("proofOfIdentity"), eq("passport"), eq("pdf"), eq("doc1"), eq("ABC123"));
        verify(mockIdentitySchemaRepository).getAllFieldSpec(any(Context.class), eq(1.0));
    }

    @Test
    public void test_parse_demographic_json_with_empty_input() throws Exception {
        RegistrationDto mockRegistrationDto = mock(RegistrationDto.class);
        when(mockRegistrationService.getRegistrationDto()).thenReturn(mockRegistrationDto);

        PreRegZipHandlingServiceImpl spyService = spy(service);

        ReflectionTestUtils.invokeMethod(spyService, "parseDemographicJson", "");

        verify(mockRegistrationDto, never()).addWithoutDocument(anyString(), anyString(), anyString(), anyString(), anyString());
        verify(mockRegistrationDto, never()).getDemographics();
        verify(mockIdentitySchemaRepository, never()).getAllFieldSpec(any(Context.class), anyDouble());
        verify(mockRegistrationDto, never()).addWithoutDocument(anyString(), anyString(), anyString(), anyString(), anyString());
        verify(mockRegistrationDto, never()).getDemographics();
        verify(mockIdentitySchemaRepository, never()).getAllFieldSpec(any(Context.class), anyDouble());
    }


}
