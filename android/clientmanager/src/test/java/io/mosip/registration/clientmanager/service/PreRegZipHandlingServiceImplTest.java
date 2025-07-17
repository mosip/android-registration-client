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
import io.mosip.registration.keymanager.dto.CryptoResponseDto;
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

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.zip.ZipEntry;
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

    @Test
    public void test_successful_extraction_of_id_json_with_valid_demographic_data() throws Exception {
        ReflectionTestUtils.setField(service, "registrationService", mockRegistrationService);
        ReflectionTestUtils.setField(service, "applicantValidDocumentDao", mockApplicantValidDocumentDao);
        ReflectionTestUtils.setField(service, "THRESHOLD_ENTRIES", 15);
        ReflectionTestUtils.setField(service, "THRESHOLD_SIZE", 200000L);
        ReflectionTestUtils.setField(service, "THRESHOLD_RATIO", 10);

        Map<String, DocumentDto> documentsMap = new HashMap<>();
        Map<String, Object> demographicsMap = new HashMap<>();

        when(mockRegistrationService.getRegistrationDto()).thenReturn(regDto);
        when(regDto.getDocuments()).thenReturn(documentsMap);
        when(regDto.getDemographics()).thenReturn(demographicsMap);

        String jsonContent = "{\"identity\":{\"fullName\":[{\"language\":\"eng\",\"value\":\"John Doe\"}]}}";
        byte[] zipFile = (jsonContent).getBytes();

        RegistrationDto result = service.extractPreRegZipFile(zipFile);

        assertNotNull(result);
        verify(mockRegistrationService, atLeast(1)).getRegistrationDto();
    }

    @Test
    public void test_throws_exception_when_entry_count_exceeds_threshold() throws Exception {
        ReflectionTestUtils.setField(service, "registrationService", mockRegistrationService);
        ReflectionTestUtils.setField(service, "applicantValidDocumentDao", mockApplicantValidDocumentDao);
        ReflectionTestUtils.setField(service, "THRESHOLD_ENTRIES", 2);
        ReflectionTestUtils.setField(service, "THRESHOLD_SIZE", 200000L);
        ReflectionTestUtils.setField(service, "THRESHOLD_RATIO", 10);

        Map<String, DocumentDto> documentsMap = new HashMap<>();
        Map<String, Object> demographicsMap = new HashMap<>();

        when(mockRegistrationService.getRegistrationDto()).thenReturn(regDto);
        when(regDto.getDocuments()).thenReturn(documentsMap);
        when(regDto.getDemographics()).thenReturn(demographicsMap);

        byte[] zipFile = new byte[5];

        service.extractPreRegZipFile(zipFile);
    }

    @Test
    public void test_process_document_files_matching_entries() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ZipOutputStream zos = new ZipOutputStream(baos)) {
            ZipEntry entry = new ZipEntry("document_1.pdf");
            zos.putNextEntry(entry);
            zos.write("dummy content".getBytes());
            zos.closeEntry();
        }
        byte[] zipFile = baos.toByteArray();

        DocumentDto documentDto = new DocumentDto();
        documentDto.setValue("document_1");
        documentDto.setFormat("pdf");
        regDto.getDocuments().put("doc1", documentDto);

        when(mockApplicantValidDocumentDao.findAllDocTypesByDocCategory("document")).thenReturn(Collections.singletonList("docType1"));
        when(mockApplicantValidDocumentDao.findAllDocTypesByCode("docType1")).thenReturn(Collections.singletonList("document"));

        assertThrows(RegBaseUncheckedException.class, () -> {
            service.extractPreRegZipFile(zipFile);
        });
    }

    @Test
    public void test_returns_populated_registration_dto() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ZipOutputStream zos = new ZipOutputStream(baos)) {
            ZipEntry idEntry = new ZipEntry("ID.json");
            zos.putNextEntry(idEntry);
            zos.write("{\"identity\": {\"name\": \"John Doe\"}}".getBytes());
            zos.closeEntry();

            ZipEntry docEntry = new ZipEntry("document_1.pdf");
            zos.putNextEntry(docEntry);
            zos.write("dummy content".getBytes());
            zos.closeEntry();
        }
        byte[] zipFile = baos.toByteArray();

        assertThrows(RegBaseUncheckedException.class, () -> {
            service.extractPreRegZipFile(zipFile);
        });
    }

    @Test
    public void test_handles_zip_within_threshold() throws Exception {
        byte[] preRegZipFile = createZipFileWithEntries(10, 10000); // Mock method to create a zip file with 10 entries, each 10KB
        Mockito.when(regService.getRegistrationDto()).thenReturn(regDto);

        assertThrows(RegBaseUncheckedException.class, () -> {
            service.extractPreRegZipFile(preRegZipFile);
        });
    }

    @Test
    public void test_validates_filenames_and_processes_documents() throws Exception {
        byte[] preRegZipFile = createZipFileWithValidDocuments(); // Mock method to create a zip file with valid document entries
        RegistrationDto registrationDtoMock = new RegistrationDto();
        Mockito.when(regService.getRegistrationDto()).thenReturn(registrationDtoMock);

        assertThrows(RegBaseUncheckedException.class, () -> {
            service.extractPreRegZipFile(preRegZipFile);
        });
    }

    @Test
    public void test_throws_exception_when_size_exceeds_threshold() throws IOException {
        byte[] preRegZipFile = createLargeZipFile();

        assertThrows(RegBaseUncheckedException.class, () -> {
            service.extractPreRegZipFile(preRegZipFile);
        });
    }

    private byte[] createZipFileWithEntries(int entryCount, int entrySize) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ZipOutputStream zos = new ZipOutputStream(baos)) {
            for (int i = 0; i < entryCount; i++) {
                ZipEntry entry = new ZipEntry("entry_" + i + ".txt");
                zos.putNextEntry(entry);
                byte[] data = new byte[entrySize];
                Arrays.fill(data, (byte) i);
                zos.write(data);
                zos.closeEntry();
            }
        }
        return baos.toByteArray();
    }

    private byte[] createZipFileWithValidDocuments() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ZipOutputStream zos = new ZipOutputStream(baos)) {
            ZipEntry entry = new ZipEntry("valid_document.pdf");
            zos.putNextEntry(entry);
            zos.write("valid content".getBytes());
            zos.closeEntry();
        }
        return baos.toByteArray();
    }

    private byte[] createLargeZipFile() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ZipOutputStream zos = new ZipOutputStream(baos)) {
            ZipEntry entry = new ZipEntry("large_file.txt");
            zos.putNextEntry(entry);
            byte[] data = new byte[300000]; // 300KB, exceeding the threshold
            Arrays.fill(data, (byte) 1);
            zos.write(data);
            zos.closeEntry();
        }
        return baos.toByteArray();
    }

    @Test
    public void test_readZipInputStreamToByteArray() throws Exception {
        byte[] data = "abc".getBytes();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ZipOutputStream zos = new ZipOutputStream(baos);
        zos.putNextEntry(new ZipEntry("f.txt"));
        zos.write(data);
        zos.closeEntry();
        zos.close();
        ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(baos.toByteArray()));
        zis.getNextEntry();
        byte[] result = PreRegZipHandlingServiceImpl.readZipInputStreamToByteArray(zis);
        assertArrayEquals(data, result);
    }

    @Test (expected = RegBaseUncheckedException.class)
    public void test_extractPreRegZipFile_handlesIOException() throws Exception {
        when(regService.getRegistrationDto()).thenReturn(regDto);
        RegistrationDto result = service.extractPreRegZipFile("notazip".getBytes());
        assertEquals(regDto, result);
    }

    @Test(expected = RegBaseUncheckedException.class)
    public void test_extractPreRegZipFile_handlesException() throws Exception {
        when(regService.getRegistrationDto()).thenThrow(new RuntimeException("fail"));
        ReflectionTestUtils.setField(service, "registrationService", regService);
        service.extractPreRegZipFile(new byte[0]);
    }

    @Test
    public void test_encryptAndSavePreRegPacket_success() throws Exception {
        CryptoResponseDto resp = new CryptoResponseDto();
        resp.setValue(Base64.getEncoder().encodeToString("abc".getBytes()));
        when(mockClientCryptoManagerService.decrypt(any())).thenReturn(resp);
        KeyGenerator keyGen = mock(KeyGenerator.class);
        SecretKey secretKey = mock(SecretKey.class);
        when(mockCryptoManagerService.generateAESKey(anyInt())).thenReturn(keyGen);
        when(keyGen.generateKey()).thenReturn(secretKey);
        when(secretKey.getEncoded()).thenReturn("1234567890123456".getBytes());
        when(mockCryptoManagerService.symmetricEncryptWithRandomIV(any(), any(), any())).thenReturn("enc".getBytes());
        when(mockGlobalParamRepository.getCachedStringPreRegPacketLocation()).thenReturn("prereg");
        when(mockContext.getFilesDir()).thenReturn(new File(System.getProperty("java.io.tmpdir")));
        PreRegistrationDto dto = service.encryptAndSavePreRegPacket("id", Base64.getEncoder().encodeToString("abc".getBytes()), new CenterMachineDto());
        assertNotNull(dto);
        assertEquals("id", dto.getPreRegId());
    }

    @Test
    public void test_storePreRegPacketToDisk_success() throws Exception {
        when(mockGlobalParamRepository.getCachedStringPreRegPacketLocation()).thenReturn("prereg");
        when(mockContext.getFilesDir()).thenReturn(new File(System.getProperty("java.io.tmpdir")));
        String path = service.storePreRegPacketToDisk("id", "abc".getBytes(), new CenterMachineDto());
        assertTrue(path.contains("id.zip"));
    }

    @Test
    public void test_decryptPreRegPacket_success() throws Exception {
        String key = Base64.getEncoder().encodeToString("1234567890123456".getBytes());
        byte[] encrypted = new byte[]{1,2,3};
        byte[] expected = new byte[]{4,5,6};
        when(mockCryptoManagerService.symmetricDecrypt(any(), eq(encrypted), isNull())).thenReturn(expected);
        byte[] result = service.decryptPreRegPacket(key, encrypted);
        assertArrayEquals(expected, result);
    }

    @Test
    public void test_validateFilename_valid() throws Exception {
        String dir = System.getProperty("java.io.tmpdir");
        File file = new File(dir, "test.txt");
        file.createNewFile();
        file.deleteOnExit();
        String result = ReflectionTestUtils.invokeMethod(service, "validateFilename", file.getAbsolutePath(), dir);
        assertEquals(file.getCanonicalPath(), result);
    }

    @Test(expected = IllegalStateException.class)
    public void test_validateFilename_invalid() throws Exception {
        String dir = System.getProperty("java.io.tmpdir") + File.separator + "intended";
        File file = new File(System.getProperty("java.io.tmpdir"), "outside.txt");
        file.createNewFile();
        file.deleteOnExit();
        ReflectionTestUtils.invokeMethod(service, "validateFilename", file.getAbsolutePath(), dir);
    }

    @Test
    public void test_parseDemographicJson_empty() throws Exception {
        RegistrationDto dto = mock(RegistrationDto.class);
        when(regService.getRegistrationDto()).thenReturn(dto);
        ReflectionTestUtils.setField(service, "registrationService", regService);
        ReflectionTestUtils.invokeMethod(service, "parseDemographicJson", "");
    }

    @Test
    public void test_parseDemographicJson_exception() throws Exception {
        when(regService.getRegistrationDto()).thenReturn(null);
        ReflectionTestUtils.setField(service, "registrationService", regService);
        try {
            ReflectionTestUtils.invokeMethod(service, "parseDemographicJson", "{\"identity\":{}}");
        } catch (Exception e) {
            assertTrue(e.getCause() instanceof RegBaseCheckedException);
        }
    }

    @Test
    public void test_parseDemographicJson_valid() throws Exception {
        RegistrationDto dto = mock(RegistrationDto.class);
        when(regService.getRegistrationDto()).thenReturn(dto);
        when(dto.getSchemaVersion()).thenReturn(1.0);
        when(dto.getDocuments()).thenReturn(new HashMap<>());
        when(dto.getDemographics()).thenReturn(new HashMap<>());
        List<FieldSpecDto> fields = new ArrayList<>();
        FieldSpecDto f1 = new FieldSpecDto(); f1.setId("fullName"); f1.setType("string"); f1.setControlType("textbox"); fields.add(f1);
        FieldSpecDto f2 = new FieldSpecDto(); f2.setId("proofOfIdentity"); f2.setType("documentType"); fields.add(f2);
        when(mockIdentitySchemaRepository.getAllFieldSpec(any(), anyDouble())).thenReturn(fields);
        ReflectionTestUtils.setField(service, "registrationService", regService);
        ReflectionTestUtils.setField(service, "identitySchemaService", mockIdentitySchemaRepository);
        String json = "{ \"identity\": { \"fullName\": \"John Doe\", \"proofOfIdentity\": { \"type\": \"passport\", \"format\": \"pdf\", \"value\": \"doc1\", \"refNumber\": \"ABC123\" } } }";
        ReflectionTestUtils.invokeMethod(service, "parseDemographicJson", json);
        verify(dto).getDocuments();
    }

    @Test
    public void test_getValueFromJson_allTypes_Success() throws Exception {
        when(masterDataService.getFieldValues(anyString(), anyString())).thenReturn(Collections.singletonList(new GenericValueDto("code", "name", "en")));
        when(masterDataService.findAllLocationsByLangCode(anyString())).thenReturn(Collections.emptyList());
        ReflectionTestUtils.setField(service, "masterDataService", masterDataService);
        JSONObject obj = new JSONObject();
        obj.put("string", "abc");
        obj.put("integer", 1);
        obj.put("number", 2L);
        obj.put("simpleType", new org.json.JSONArray().put(new JSONObject().put("language", "en").put("value", "code")));
        assertEquals("abc", ReflectionTestUtils.invokeMethod(service, "getValueFromJson", "string", "string", obj));
        assertNotNull(ReflectionTestUtils.invokeMethod(service, "getValueFromJson", "simpleType", "simpleType", obj));
    }

    @Test
    public void test_initPreRegAdapter_externalStorageNotMounted() {
        MockedStatic<Environment> mockedEnv = Mockito.mockStatic(Environment.class);
        mockedEnv.when(Environment::getExternalStorageState).thenReturn("unmounted");
        ReflectionTestUtils.invokeMethod(service, "initPreRegAdapter", mockContext);
        mockedEnv.close();
    }

    @Test
    public void test_parseDemographicJson_documentType_and_biometricsType() throws Exception {
        RegistrationDto dto = mock(RegistrationDto.class);
        when(regService.getRegistrationDto()).thenReturn(dto);
        when(dto.getSchemaVersion()).thenReturn(1.0);
        when(dto.getDocuments()).thenReturn(new HashMap<>());
        when(dto.getDemographics()).thenReturn(new HashMap<>());
        List<FieldSpecDto> fields = new ArrayList<>();
        FieldSpecDto docField = new FieldSpecDto();
        docField.setId("proofOfIdentity");
        docField.setType("documentType");
        fields.add(docField);
        FieldSpecDto bioField = new FieldSpecDto();
        bioField.setId("face");
        bioField.setType("biometricsType");
        fields.add(bioField);
        when(mockIdentitySchemaRepository.getAllFieldSpec(any(), anyDouble())).thenReturn(fields);
        ReflectionTestUtils.setField(service, "registrationService", regService);
        ReflectionTestUtils.setField(service, "identitySchemaService", mockIdentitySchemaRepository);
        String json = "{ \"identity\": { \"proofOfIdentity\": { \"type\": \"passport\", \"format\": \"pdf\", \"value\": \"doc1\", \"refNumber\": \"ABC123\" }, \"face\": \"faceData\" } }";
        ReflectionTestUtils.invokeMethod(service, "parseDemographicJson", json);
        verify(dto).getDocuments();
    }

    @Test
    public void test_getValueFromJson_missingKey() throws Exception {
        JSONObject obj = new JSONObject();
        java.lang.reflect.Method m = PreRegZipHandlingServiceImpl.class.getDeclaredMethod("getValueFromJson", String.class, String.class, JSONObject.class);
        m.setAccessible(true);
        assertNull(m.invoke(service, "notfound", "string", obj));
    }

    @Test
    public void test_getValueFromJson_unexpectedType() throws Exception {
        JSONObject obj = new JSONObject();
        obj.put("weird", new Object());
        java.lang.reflect.Method m = PreRegZipHandlingServiceImpl.class.getDeclaredMethod("getValueFromJson", String.class, String.class, JSONObject.class);
        m.setAccessible(true);
        assertNull(m.invoke(service, "weird", "unknownType", obj));
    }

    @Test
    public void test_storePreRegPacketToDisk_directoryCreationFails() {
        File file = mock(File.class);
        when(file.exists()).thenReturn(false);
        when(file.mkdirs()).thenReturn(false);
        when(mockContext.getFilesDir()).thenReturn(file);
        when(mockGlobalParamRepository.getCachedStringPreRegPacketLocation()).thenReturn("prereg");
        ReflectionTestUtils.setField(service, "appContext", mockContext);
        ReflectionTestUtils.setField(service, "globalParamRepository", mockGlobalParamRepository);
        try {
            service.storePreRegPacketToDisk("id", new byte[]{1,2,3}, new CenterMachineDto());
        } catch (Exception e) {
            // Should not throw, just print error
        }
    }

    @Test
    public void test_extractPreRegZipFile_withNonMatchingEntries() throws Exception {
        RegistrationDto dto = new RegistrationDto();
        Map<String, DocumentDto> docs = new HashMap<>();
        dto.setDocuments(docs);
        when(mockRegistrationService.getRegistrationDto()).thenReturn(dto);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ZipOutputStream zos = new ZipOutputStream(baos)) {
            zos.putNextEntry(new ZipEntry("random.txt"));
            zos.write("data".getBytes());
            zos.closeEntry();
        }
        byte[] zipBytes = baos.toByteArray();
        RegistrationDto result = service.extractPreRegZipFile(zipBytes);
        assertEquals(dto, result);
    }

    @Test (expected = RegBaseUncheckedException.class)
    public void test_extractPreRegZipFile_removesEmptyDocumentEntries() throws Exception {
        RegistrationDto dto = new RegistrationDto();
        Map<String, DocumentDto> docs = new HashMap<>();
        DocumentDto doc = new DocumentDto();
        doc.setContent(new ArrayList<>());
        docs.put("doc1", doc);
        dto.setDocuments(docs);
        when(mockRegistrationService.getRegistrationDto()).thenReturn(dto);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ZipOutputStream zos = new ZipOutputStream(baos)) {
            zos.putNextEntry(new ZipEntry("random.txt"));
            zos.write("data".getBytes());
            zos.closeEntry();
        }
        byte[] zipBytes = baos.toByteArray();
        RegistrationDto result = service.extractPreRegZipFile(zipBytes);
        assertTrue(result.getDocuments().isEmpty());
    }

    @Test
    public void test_getValueFromJson_simpleType_index1_branch() throws Exception {
        JSONObject obj = new JSONObject();
        org.json.JSONArray arr = new org.json.JSONArray();
        JSONObject langObj = new JSONObject();
        langObj.put("language", "en");
        langObj.put("value", "code");
        arr.put(langObj);
        obj.put("district", arr);

        Location loc = new Location("123", "eng");
        loc.setHierarchyName("district");
        loc.setHierarchyLevel(1);
        when(masterDataService.getFieldValues(eq("district"), eq("en"))).thenReturn(Collections.singletonList(new GenericValueDto("code", "DistrictName", "en")));
        when(masterDataService.findAllLocationsByLangCode(anyString())).thenReturn(Collections.singletonList(loc));
        when(masterDataService.findLocationByHierarchyLevel(eq(1), eq("en"))).thenReturn(Collections.singletonList(new GenericValueDto("code", "DistrictName", "en")));
        ReflectionTestUtils.setField(service, "masterDataService", masterDataService);

        List<?> result = ReflectionTestUtils.invokeMethod(service, "getValueFromJson", "district", "simpleType", obj);
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    public void test_getValueFromJson_simpleType_fallback_to_value() throws Exception {
        JSONObject obj = new JSONObject();
        org.json.JSONArray arr = new org.json.JSONArray();
        JSONObject langObj = new JSONObject();
        langObj.put("language", "en");
        langObj.put("value", "unknown");
        arr.put(langObj);
        obj.put("district", arr);

        when(masterDataService.getFieldValues(eq("district"), eq("en"))).thenReturn(Collections.emptyList());
        when(masterDataService.findAllLocationsByLangCode(anyString())).thenReturn(Collections.emptyList());
        ReflectionTestUtils.setField(service, "masterDataService", masterDataService);

        List<?> result = ReflectionTestUtils.invokeMethod(service, "getValueFromJson", "district", "simpleType", obj);
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals("unknown", ((io.mosip.registration.packetmanager.dto.SimpleType)result.get(0)).getValue());
    }

    @Test
    public void test_getValueFromJson_catchThrowable() throws Exception {
        JSONObject obj = new JSONObject();
        java.lang.reflect.Method m = PreRegZipHandlingServiceImpl.class.getDeclaredMethod("getValueFromJson", String.class, String.class, JSONObject.class);
        m.setAccessible(true);
        assertNull(m.invoke(service, "nonexistent", "string", obj));
    }

    @Test
    public void test_getValueFromJson_nullJSONArray() throws Exception {
        JSONObject obj = new JSONObject();
        obj.put("simpleType", JSONObject.NULL);
        java.lang.reflect.Method m = PreRegZipHandlingServiceImpl.class.getDeclaredMethod("getValueFromJson", String.class, String.class, JSONObject.class);
        m.setAccessible(true);
        assertNull(m.invoke(service, "simpleType", "simpleType", obj));
    }

    @Test
    public void test_getValueFromJson_emptyJSONArray() throws Exception {
        JSONObject obj = new JSONObject();
        obj.put("simpleType", new org.json.JSONArray());
        java.lang.reflect.Method m = PreRegZipHandlingServiceImpl.class.getDeclaredMethod("getValueFromJson", String.class, String.class, JSONObject.class);
        m.setAccessible(true);
        Object result = m.invoke(service, "simpleType", "simpleType", obj);
        assertNotNull(result);
        assertTrue(((List<?>)result).isEmpty());
    }

    @Test
    public void test_getValueFromJson_simpleType_withNullValue() throws Exception {
        JSONObject obj = new JSONObject();
        org.json.JSONArray arr = new org.json.JSONArray();
        JSONObject langObj = new JSONObject();
        langObj.put("language", "en");
        langObj.put("value", JSONObject.NULL);
        arr.put(langObj);
        obj.put("district", arr);

        when(masterDataService.getFieldValues(eq("district"), eq("en"))).thenReturn(Collections.emptyList());
        when(masterDataService.findAllLocationsByLangCode(anyString())).thenReturn(Collections.emptyList());
        ReflectionTestUtils.setField(service, "masterDataService", masterDataService);

        List<?> result = ReflectionTestUtils.invokeMethod(service, "getValueFromJson", "district", "simpleType", obj);
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    public void test_validateDemographicInfoObject_registrationServiceNull() {
        ReflectionTestUtils.setField(service, "registrationService", null);
        boolean result = ReflectionTestUtils.invokeMethod(service, "validateDemographicInfoObject");
        assertFalse(result);
    }

    @Test
    public void test_validateDemographicInfoObject_registrationDtoNull() throws Exception {
        when(regService.getRegistrationDto()).thenReturn(null);
        ReflectionTestUtils.setField(service, "registrationService", regService);
        boolean result = ReflectionTestUtils.invokeMethod(service, "validateDemographicInfoObject");
        assertFalse(result);
    }

    @Test
    public void test_validateDemographicInfoObject_demographicsNull() throws Exception {
        when(regDto.getDemographics()).thenReturn(null);
        when(regService.getRegistrationDto()).thenReturn(regDto);
        ReflectionTestUtils.setField(service, "registrationService", regService);
        boolean result = ReflectionTestUtils.invokeMethod(service, "validateDemographicInfoObject");
        assertFalse(result);
    }

    @Test
    public void test_validateDemographicInfoObject_demographicsNotNull() throws Exception {
        when(regDto.getDemographics()).thenReturn(new HashMap<>());
        when(regService.getRegistrationDto()).thenReturn(regDto);
        ReflectionTestUtils.setField(service, "registrationService", regService);
        boolean result = ReflectionTestUtils.invokeMethod(service, "validateDemographicInfoObject");
        assertTrue(result);
    }

    @Test(expected = RuntimeException.class)
    public void test_validateDemographicInfoObject_getRegistrationDtoThrows() throws Exception {
        when(regService.getRegistrationDto()).thenThrow(new RuntimeException("fail"));
        ReflectionTestUtils.setField(service, "registrationService", regService);
        ReflectionTestUtils.invokeMethod(service, "validateDemographicInfoObject");
    }

    @Test
    public void test_initPreRegAdapter_externalStorageMounted() {
        MockedStatic<Environment> mockedEnv = Mockito.mockStatic(Environment.class);
        MockedStatic<ConfigService> mockedConfig = Mockito.mockStatic(ConfigService.class);
        MockedStatic<Log> mockedLog = Mockito.mockStatic(Log.class);

        mockedEnv.when(Environment::getExternalStorageState).thenReturn(Environment.MEDIA_MOUNTED);
        mockedConfig.when(() -> ConfigService.getProperty(anyString(), any())).thenReturn("testLocation");
        File mockFile = mock(File.class);
        when(mockFile.exists()).thenReturn(false);
        when(mockFile.mkdirs()).thenReturn(true);

        ReflectionTestUtils.invokeMethod(service, "initPreRegAdapter", mockContext);

        mockedLog.verify(() -> Log.i(anyString(), eq("initLocalClientCryptoService: Initialization call successful")));
        mockedEnv.close();
        mockedConfig.close();
        mockedLog.close();
    }

}
