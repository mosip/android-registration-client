package io.mosip.registration.clientmanager.repository;

import android.content.Context;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.mosip.registration.clientmanager.constant.RegistrationConstants;
import io.mosip.registration.clientmanager.dao.IdentitySchemaDao;
import io.mosip.registration.clientmanager.dao.ProcessSpecDao;
import io.mosip.registration.clientmanager.dto.http.IdSchemaResponse;
import io.mosip.registration.clientmanager.dto.uispec.FieldSpecDto;
import io.mosip.registration.clientmanager.dto.uispec.ProcessSpecDto;
import io.mosip.registration.clientmanager.dto.uispec.RequiredDto;
import io.mosip.registration.clientmanager.dto.uispec.ScreenSpecDto;
import io.mosip.registration.clientmanager.dto.uispec.SettingsSpecDto;
import io.mosip.registration.clientmanager.entity.IdentitySchema;
import io.mosip.registration.clientmanager.entity.ProcessSpec;
import io.mosip.registration.packetmanager.util.HMACUtils2;
import io.mosip.registration.packetmanager.util.JsonUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class IdentitySchemaRepositoryTest {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @InjectMocks
    private IdentitySchemaRepository identitySchemaRepository;

    @Mock
    private IdentitySchemaDao identitySchemaDao;

    @Mock
    private GlobalParamRepository globalParamRepository;

    @Mock
    private TemplateRepository templateRepository;

    @Mock
    private ProcessSpecDao processSpecDao;

    @Mock
    private Context context;

    private ObjectMapper objectMapper = new ObjectMapper();
    private File testDirectory;

    @Before
    public void setUp() throws Exception {
        testDirectory = temporaryFolder.newFolder("test-files");
        when(context.getFilesDir()).thenReturn(testDirectory);
    }

    /**
     * Test saveIdentitySchema() should successfully save schema and create file.
     */
    @Test
    public void testSaveIdentitySchema_Success() throws Exception {
        IdSchemaResponse idSchemaResponse = new IdSchemaResponse();
        idSchemaResponse.setId("schema-id");
        idSchemaResponse.setIdVersion(1.0);
        idSchemaResponse.setSchema(new ArrayList<>());
        String schemaJson = JsonUtils.javaObjectToJsonString(idSchemaResponse);

        identitySchemaRepository.saveIdentitySchema(context, idSchemaResponse);

        File expectedFile = new File(testDirectory, "schema_1.0");
        assertTrue("Schema file should be created", expectedFile.exists());

        verify(identitySchemaDao).insertIdentitySchema(any(IdentitySchema.class));
    }

    /**
     * Test saveIdentitySchema() should migrate schema and create process spec when fields are present.
     */
    @Test
    public void testSaveIdentitySchema_WithMigration() throws Exception {
        IdSchemaResponse idSchemaResponse = new IdSchemaResponse();
        idSchemaResponse.setId("schema-id");
        idSchemaResponse.setIdVersion(1.0);

        FieldSpecDto demographicField = new FieldSpecDto();
        demographicField.setId("fullName");
        demographicField.setType("simpleType");
        demographicField.setFieldType("simpleType");
        demographicField.setInputRequired(true);
        HashMap<String, String> demoLabels = new HashMap<>();
        demoLabels.put("primary", "Full Name");
        demoLabels.put("secondary", "Nom complet");
        demographicField.setLabel(demoLabels);
        demographicField.setVisible(new RequiredDto("MVEL", "identity.?isNew"));

        FieldSpecDto documentField = new FieldSpecDto();
        documentField.setId("proofOfIdentity");
        documentField.setType("documentType");
        documentField.setFieldType("documentType");
        documentField.setInputRequired(true);
        HashMap<String, String> docLabels = new HashMap<>();
        docLabels.put("primary", "Proof of Identity");
        docLabels.put("secondary", "Preuve d'identité");
        documentField.setLabel(docLabels);

        FieldSpecDto individualBiometricField = new FieldSpecDto();
        individualBiometricField.setId("individualBiometrics");
        individualBiometricField.setType("biometricsType");
        individualBiometricField.setFieldType("biometricsType");
        individualBiometricField.setInputRequired(true);
        HashMap<String, String> bioLabels = new HashMap<>();
        bioLabels.put("primary", "Biometrics");
        bioLabels.put("secondary", "Biométriques");
        individualBiometricField.setLabel(bioLabels);
        individualBiometricField.setRequiredOn(Arrays.asList(new RequiredDto("MVEL", "identity.?isChild || identity.isChild")));

        FieldSpecDto introducerBiometricField = new FieldSpecDto();
        introducerBiometricField.setId("introducerBiometrics");
        introducerBiometricField.setType("biometricsType");
        introducerBiometricField.setFieldType("biometricsType");
        introducerBiometricField.setInputRequired(true);
        HashMap<String, String> introLabels = new HashMap<>();
        introLabels.put("primary", "Introducer Biometrics");
        introLabels.put("secondary", "Biométrie introduite");
        introducerBiometricField.setLabel(introLabels);
        introducerBiometricField.setRequiredOn(Arrays.asList(new RequiredDto("MVEL", "identity.?isChild")));

        FieldSpecDto dynamicField = new FieldSpecDto();
        dynamicField.setId("dynamicField");
        dynamicField.setType("simpleType");
        dynamicField.setFieldType("dynamic");
        dynamicField.setInputRequired(true);
        HashMap<String, String> dynLabels = new HashMap<>();
        dynLabels.put("primary", "Dynamic Field");
        dynLabels.put("secondary", "Champ dynamique");
        dynamicField.setLabel(dynLabels);

        idSchemaResponse.setSchema(Arrays.asList(demographicField, documentField, individualBiometricField, introducerBiometricField, dynamicField));

        when(globalParamRepository.getCachedStringGlobalParam(eq(RegistrationConstants.PRIMARY_LANGUAGE))).thenReturn("en");
        when(globalParamRepository.getCachedStringGlobalParam(eq(RegistrationConstants.SECONDARY_LANGUAGE))).thenReturn("fr");
        when(globalParamRepository.getCachedStringGlobalParam(eq("newRegistrationProcess_en"))).thenReturn("New Registration");
        when(globalParamRepository.getCachedStringGlobalParam(eq("newRegistrationProcess_fr"))).thenReturn("Nouvelle Inscription");
        when(globalParamRepository.getCachedStringGlobalParam(eq(RegistrationConstants.ALLOWED_BIO_ATTRIBUTES))).thenReturn("finger, iris");
        when(globalParamRepository.getCachedStringGlobalParam(eq(RegistrationConstants.AGEGROUP_CONFIG))).thenReturn(
                "{\"infant\": {\"bioAttributes\": [\"finger\"], \"isGuardianAuthRequired\": true}}"
        );
        when(globalParamRepository.getCachedStringGlobalParam(eq(RegistrationConstants.CONSENT_SCREEN_TEMPLATE_NAME))).thenReturn("consent-template");
        when(globalParamRepository.getCachedStringGlobalParam(eq("consentScreenName_en"))).thenReturn("Consent");
        when(globalParamRepository.getCachedStringGlobalParam(eq("consentScreenName_fr"))).thenReturn("Consentement");
        when(globalParamRepository.getCachedStringGlobalParam(eq("demographicsScreenName_en"))).thenReturn("Demographics");
        when(globalParamRepository.getCachedStringGlobalParam(eq("demographicsScreenName_fr"))).thenReturn("Démographiques");
        when(globalParamRepository.getCachedStringGlobalParam(eq("documentsScreenName_en"))).thenReturn("Documents");
        when(globalParamRepository.getCachedStringGlobalParam(eq("documentsScreenName_fr"))).thenReturn("Documents");
        when(globalParamRepository.getCachedStringGlobalParam(eq("biometricsScreenName_en"))).thenReturn("Biometrics");
        when(globalParamRepository.getCachedStringGlobalParam(eq("biometricsScreenName_fr"))).thenReturn("Biométriques");
        when(globalParamRepository.getCachedStringGlobalParam(eq(RegistrationConstants.INDIVIDUAL_BIOMETRICS_ID))).thenReturn("individualBiometrics");
        when(globalParamRepository.getCachedStringGlobalParam(eq(RegistrationConstants.INTRODUCER_BIOMETRICS_ID))).thenReturn("introducerBiometrics");
        when(globalParamRepository.getCachedStringGlobalParam(eq(RegistrationConstants.INFANT_AGEGROUP_NAME))).thenReturn("infant");
        when(templateRepository.getTemplate(eq("consent-template"), eq("en"))).thenReturn("[{\"id\":\"consentField\",\"inputRequired\":true,\"label\":{\"primary\":\"Consent\"}}]");

        identitySchemaRepository.saveIdentitySchema(context, idSchemaResponse);

        File expectedFile = new File(testDirectory, "schema_1.0");
        assertTrue("Schema file should be created", expectedFile.exists());

        verify(identitySchemaDao).insertIdentitySchema(any(IdentitySchema.class));

        String fileContent = IOUtils.toString(new FileInputStream(expectedFile), StandardCharsets.UTF_8);
        IdSchemaResponse savedResponse = JsonUtils.jsonStringToJavaObject(fileContent, IdSchemaResponse.class);
        ProcessSpecDto newProcess = savedResponse.getNewProcess();
        assertNotNull("New process should be created", newProcess);
        assertEquals(4, newProcess.getScreens().size());
    }

    /**
     * Test saveIdentitySchema() should handle migration failure gracefully and still save schema.
     */
    @Test
    public void testSaveIdentitySchema_MigrationFailure() throws Exception {
        IdSchemaResponse idSchemaResponse = new IdSchemaResponse();
        idSchemaResponse.setId("schema-id");
        idSchemaResponse.setIdVersion(1.0);

        FieldSpecDto field = new FieldSpecDto();
        field.setId("testField");
        field.setType("simpleType");
        idSchemaResponse.setSchema(Arrays.asList(field));

        when(globalParamRepository.getCachedStringGlobalParam(anyString()))
                .thenThrow(new RuntimeException("Migration error"));

        identitySchemaRepository.saveIdentitySchema(context, idSchemaResponse);

        File expectedFile = new File(testDirectory, "schema_1.0");
        assertTrue("Schema file should be created even if migration fails", expectedFile.exists());
        verify(identitySchemaDao).insertIdentitySchema(any(IdentitySchema.class));
    }

    /**
     * Test createProcessSpec() should save process spec and create corresponding file.
     */
    @Test
    public void testCreateProcessSpec_Success() throws Exception {
        ProcessSpecDto processSpecDto = new ProcessSpecDto("NEW", 1, "NEW", true, new HashMap<>(), new HashMap<>(), "icon.png", null, null);

        identitySchemaRepository.createProcessSpec(context, "NEW", 1.0, processSpecDto);

        File expectedFile = new File(testDirectory, "NEW");
        assertTrue("Process spec file should be created", expectedFile.exists());

        verify(identitySchemaDao).insertIdentitySchema(any(IdentitySchema.class));
        verify(processSpecDao).insertProcessSpec(any(ProcessSpec.class));
    }

    /**
     * Test getLatestSchemaVersion() should return the latest schema version if present.
     */
    @Test
    public void testGetLatestSchemaVersion_Success() {
        IdentitySchema identitySchema = new IdentitySchema("schema-id");
        identitySchema.setSchemaVersion(1.0);
        when(identitySchemaDao.findLatestSchema()).thenReturn(identitySchema);

        Double version = identitySchemaRepository.getLatestSchemaVersion();

        assertEquals(1.0, version, 0.0);
    }

    /**
     * Test getLatestSchemaVersion() should return null if no schema is found.
     */
    @Test
    public void testGetLatestSchemaVersion_Null() {
        when(identitySchemaDao.findLatestSchema()).thenReturn(null);

        Double version = identitySchemaRepository.getLatestSchemaVersion();

        assertNull(version);
    }

    /**
     * Test getSchemaJson() should return schema JSON if file and hash are valid.
     */
    @Test
    public void testGetSchemaJson_Success() throws Exception {
        File schemaFile = new File(testDirectory, "schema_1.0");
        IdSchemaResponse idSchemaResponse = new IdSchemaResponse();
        idSchemaResponse.setSchemaJson("schema-json");
        String schemaJson = JsonUtils.javaObjectToJsonString(idSchemaResponse);

        try (FileWriter writer = new FileWriter(schemaFile)) {
            writer.write(schemaJson);
        }

        IdentitySchema identitySchema = new IdentitySchema("schema-id");
        identitySchema.setSchemaVersion(1.0);
        identitySchema.setFileName("schema_1.0");
        identitySchema.setFileLength(schemaFile.length());
        identitySchema.setFileHash(HMACUtils2.digestAsPlainText(schemaJson.getBytes(StandardCharsets.UTF_8)));

        when(identitySchemaDao.findIdentitySchema(1.0, "schema_1.0")).thenReturn(identitySchema);

        String result = identitySchemaRepository.getSchemaJson(context, 1.0);

        assertEquals("schema-json", result);
    }

    /**
     * Test getSchemaJson() should throw exception if schema is not found.
     */
    @Test(expected = Exception.class)
    public void testGetSchemaJson_SchemaNotFound() throws Exception {
        when(identitySchemaDao.findIdentitySchema(1.0, "schema_1.0")).thenReturn(null);

        identitySchemaRepository.getSchemaJson(context, 1.0);
    }

    /**
     * Test getSchemaJson() should throw exception if file length matches but hash mismatches.
     */
    @Test(expected = Exception.class)
    public void testGetSchemaJson_HashMismatch() throws Exception {
        File schemaFile = new File(testDirectory, "schema_1.0");
        try (FileWriter writer = new FileWriter(schemaFile)) {
            writer.write("original content");
        }

        IdentitySchema identitySchema = new IdentitySchema("schema-id");
        identitySchema.setSchemaVersion(1.0);
        identitySchema.setFileName("schema_1.0");
        identitySchema.setFileLength(schemaFile.length());
        identitySchema.setFileHash("different-hash");

        when(identitySchemaDao.findIdentitySchema(1.0, "schema_1.0")).thenReturn(identitySchema);

        identitySchemaRepository.getSchemaJson(context, 1.0);
    }

    /**
     * Test getSchemaJson() should throw exception when IO failure occurs while reading file.
     */
    @Test(expected = Exception.class)
    public void testGetSchemaJson_IOFailure() throws Exception {
        File schemaDir = new File(testDirectory, "schema_1.0");
        assertTrue(schemaDir.mkdir());

        IdentitySchema identitySchema = new IdentitySchema("schema-id");
        identitySchema.setSchemaVersion(1.0);
        identitySchema.setFileName("schema_1.0");
        identitySchema.setFileLength(0L);
        identitySchema.setFileHash("hash");

        when(identitySchemaDao.findIdentitySchema(1.0, "schema_1.0")).thenReturn(identitySchema);

        identitySchemaRepository.getSchemaJson(context, 1.0);
    }

    /**
     * Test getSchemaJson() should throw exception if file is tampered (hash or length mismatch).
     */
    @Test(expected = Exception.class)
    public void testGetSchemaJson_FileTampered() throws Exception {
        File schemaFile = new File(testDirectory, "schema_1.0");
        try (FileWriter writer = new FileWriter(schemaFile)) {
            writer.write("original content");
        }

        IdentitySchema identitySchema = new IdentitySchema("schema-id");
        identitySchema.setSchemaVersion(1.0);
        identitySchema.setFileName("schema_1.0");
        identitySchema.setFileLength(100L);
        identitySchema.setFileHash("hash");

        when(identitySchemaDao.findIdentitySchema(1.0, "schema_1.0")).thenReturn(identitySchema);

        identitySchemaRepository.getSchemaJson(context, 1.0);
    }

    @Test
    public void testGetNewProcessSpec_Success() throws Exception {
        File schemaFile = new File(testDirectory, "schema_1.0");
        ProcessSpecDto processSpecDto = new ProcessSpecDto("NEW", 1, "NEW", true, new HashMap<>(), new HashMap<>(), "icon.png", null, null);
        IdSchemaResponse idSchemaResponse = new IdSchemaResponse();
        idSchemaResponse.setNewProcess(processSpecDto);
        String schemaJson = JsonUtils.javaObjectToJsonString(idSchemaResponse);

        try (FileWriter writer = new FileWriter(schemaFile)) {
            writer.write(schemaJson);
        }

        IdentitySchema identitySchema = new IdentitySchema("schema-id");
        identitySchema.setSchemaVersion(1.0);
        identitySchema.setFileName("schema_1.0");
        identitySchema.setFileLength(schemaFile.length());
        identitySchema.setFileHash(HMACUtils2.digestAsPlainText(schemaJson.getBytes(StandardCharsets.UTF_8)));

        when(identitySchemaDao.findIdentitySchema(1.0, "schema_1.0")).thenReturn(identitySchema);

        ProcessSpecDto result = identitySchemaRepository.getNewProcessSpec(context, 1.0);

        assertEquals(processSpecDto.getId(), result.getId());
    }

    @Test(expected = Exception.class)
    public void testGetNewProcessSpec_SchemaMissing() throws Exception {
        when(identitySchemaDao.findIdentitySchema(1.0, "schema_1.0")).thenReturn(null);
        identitySchemaRepository.getNewProcessSpec(context, 1.0);
    }

    @Test
    public void testGetSettingsSchema_Success() throws Exception {
        File schemaFile = new File(testDirectory, "schema_1.0");
        List<SettingsSpecDto> settings = Arrays.asList(new SettingsSpecDto());
        IdSchemaResponse idSchemaResponse = new IdSchemaResponse();
        idSchemaResponse.setSettings(settings);
        String schemaJson = JsonUtils.javaObjectToJsonString(idSchemaResponse);

        try (FileWriter writer = new FileWriter(schemaFile)) {
            writer.write(schemaJson);
        }

        IdentitySchema identitySchema = new IdentitySchema("schema-id");
        identitySchema.setSchemaVersion(1.0);
        identitySchema.setFileName("schema_1.0");
        identitySchema.setFileLength(schemaFile.length());
        identitySchema.setFileHash(HMACUtils2.digestAsPlainText(schemaJson.getBytes(StandardCharsets.UTF_8)));

        when(identitySchemaDao.findIdentitySchema(1.0, "schema_1.0")).thenReturn(identitySchema);

        List<SettingsSpecDto> result = identitySchemaRepository.getSettingsSchema(context, 1.0);
        assertEquals(1, result.size());
    }

    /**
     * Test getAllFieldSpec() should return all field specs from the process spec.
     */
    @Test
    public void testGetAllFieldSpec_Success() throws Exception {
        File schemaFile = new File(testDirectory, "schema_1.0");

        List<FieldSpecDto> fields = Arrays.asList(new FieldSpecDto());
        ScreenSpecDto screen = new ScreenSpecDto("Screen1", new HashMap<>(), fields, 1, true,false);
        ProcessSpecDto processSpecDto = new ProcessSpecDto("NEW", 1, "NEW", true, new HashMap<>(), new HashMap<>(), "icon.png", null, null);
        processSpecDto.setScreens(Arrays.asList(screen));

        IdSchemaResponse idSchemaResponse = new IdSchemaResponse();
        idSchemaResponse.setNewProcess(processSpecDto);
        String schemaJson = JsonUtils.javaObjectToJsonString(idSchemaResponse);

        try (FileWriter writer = new FileWriter(schemaFile)) {
            writer.write(schemaJson);
        }

        IdentitySchema identitySchema = new IdentitySchema("schema-id");
        identitySchema.setSchemaVersion(1.0);
        identitySchema.setFileName("schema_1.0");
        identitySchema.setFileLength(schemaFile.length());
        identitySchema.setFileHash(HMACUtils2.digestAsPlainText(schemaJson.getBytes(StandardCharsets.UTF_8)));

        when(identitySchemaDao.findIdentitySchema(1.0, "schema_1.0")).thenReturn(identitySchema);

        List<FieldSpecDto> result = identitySchemaRepository.getAllFieldSpec(context, 1.0);

        assertEquals(fields.size(), result.size());
    }

    /**
     * Test getAllFieldSpec() should throw exception if process spec is not found in schema.
     */
    @Test(expected = Exception.class)
    public void testGetAllFieldSpec_ProcessSpecNotFound() throws Exception {
        File schemaFile = new File(testDirectory, "schema_1.0");
        IdSchemaResponse idSchemaResponse = new IdSchemaResponse();
        idSchemaResponse.setNewProcess(null);
        String schemaJson = JsonUtils.javaObjectToJsonString(idSchemaResponse);

        try (FileWriter writer = new FileWriter(schemaFile)) {
            writer.write(schemaJson);
        }

        IdentitySchema identitySchema = new IdentitySchema("schema-id");
        identitySchema.setSchemaVersion(1.0);
        identitySchema.setFileName("schema_1.0");
        identitySchema.setFileLength(schemaFile.length());
        identitySchema.setFileHash(HMACUtils2.digestAsPlainText(schemaJson.getBytes(StandardCharsets.UTF_8)));

        when(identitySchemaDao.findIdentitySchema(1.0, "schema_1.0")).thenReturn(identitySchema);

        identitySchemaRepository.getAllFieldSpec(context, 1.0);
    }

    /**
     * Test getAllProcessSpecDTO() should return all process spec DTOs for a schema version.
     */
    @Test
    public void testGetAllProcessSpecDTO_Success() throws Exception {
        File processSpecFile = new File(testDirectory, "NEW");
        ProcessSpecDto processSpecDto = new ProcessSpecDto("NEW", 1, "NEW", true, new HashMap<>(), new HashMap<>(), "icon.png", null, null);
        String schemaJson = JsonUtils.javaObjectToJsonString(processSpecDto);

        try (FileWriter writer = new FileWriter(processSpecFile)) {
            writer.write(schemaJson);
        }

        IdentitySchema identitySchema = new IdentitySchema("schema-id");
        identitySchema.setSchemaVersion(1.0);
        identitySchema.setFileName("NEW");
        identitySchema.setFileLength(processSpecFile.length());
        identitySchema.setFileHash(HMACUtils2.digestAsPlainText(schemaJson.getBytes(StandardCharsets.UTF_8)));

        ProcessSpec processSpec = new ProcessSpec("NEW");
        processSpec.setType("NEW");
        processSpec.setIdVersion(1.0);

        when(processSpecDao.getAllProcessSpec(1.0)).thenReturn(Arrays.asList(processSpec));
        when(identitySchemaDao.findIdentitySchema(1.0, "NEW")).thenReturn(identitySchema);

        List<ProcessSpecDto> result = identitySchemaRepository.getAllProcessSpecDTO(context, 1.0);

        assertEquals(1, result.size());
        assertEquals(processSpecDto.getId(), result.get(0).getId());
    }

    @Test(expected = Exception.class)
    public void testGetAllProcessSpecDTO_NoProcessSpec() throws Exception {
        when(processSpecDao.getAllProcessSpec(1.0)).thenReturn(null);

        identitySchemaRepository.getAllProcessSpecDTO(context, 1.0);
    }

    @Test(expected = Exception.class)
    public void testGetAllProcessSpecDTO_IdentitySchemaMissing() throws Exception {
        ProcessSpec processSpec = new ProcessSpec("NEW");
        processSpec.setType("NEW");
        processSpec.setIdVersion(1.0);
        when(processSpecDao.getAllProcessSpec(1.0)).thenReturn(Arrays.asList(processSpec));
        when(identitySchemaDao.findIdentitySchema(1.0, "NEW")).thenReturn(null);

        identitySchemaRepository.getAllProcessSpecDTO(context, 1.0);
    }

    /**
     * Test getProcessSpecFields() should return all field specs for a given process spec.
     */
    @Test
    public void testGetProcessSpecFields_Success() throws Exception {
        File processSpecFile = new File(testDirectory, "NEW");

        List<FieldSpecDto> fields = Arrays.asList(new FieldSpecDto());
        ScreenSpecDto screen = new ScreenSpecDto("Screen1", new HashMap<>(), fields, 1, true, false);
        ProcessSpecDto processSpecDto = new ProcessSpecDto("NEW", 1, "NEW", true, new HashMap<>(), new HashMap<>(), "icon.png", null, null);
        processSpecDto.setScreens(Arrays.asList(screen));
        String schemaJson = JsonUtils.javaObjectToJsonString(processSpecDto);

        try (FileWriter writer = new FileWriter(processSpecFile)) {
            writer.write(schemaJson);
        }

        IdentitySchema identitySchema = new IdentitySchema("schema-id");
        identitySchema.setSchemaVersion(1.0);
        identitySchema.setFileName("NEW");
        identitySchema.setFileLength(processSpecFile.length());
        identitySchema.setFileHash(HMACUtils2.digestAsPlainText(schemaJson.getBytes(StandardCharsets.UTF_8)));

        ProcessSpec processSpec = new ProcessSpec("NEW");
        processSpec.setType("NEW");
        processSpec.setIdVersion(1.0);

        when(identitySchemaDao.findLatestSchema()).thenReturn(identitySchema);
        when(processSpecDao.getProcessSpecFromProcessId("NEW", 1.0)).thenReturn(processSpec);
        when(identitySchemaDao.findIdentitySchema(1.0, "NEW")).thenReturn(identitySchema);

        List<FieldSpecDto> result = identitySchemaRepository.getProcessSpecFields(context, "NEW");

        assertEquals(fields.size(), result.size());
    }

    @Test(expected = Exception.class)
    public void testGetProcessSpecFields_NoProcessSpec() throws Exception {
        IdentitySchema identitySchema = new IdentitySchema("schema-id");
        identitySchema.setSchemaVersion(1.0);
        when(identitySchemaDao.findLatestSchema()).thenReturn(identitySchema);
        when(processSpecDao.getProcessSpecFromProcessId("NEW", 1.0)).thenReturn(null);

        identitySchemaRepository.getProcessSpecFields(context, "NEW");
    }

    @Test(expected = Exception.class)
    public void testGetProcessSpecFields_IdentitySchemaMissing() throws Exception {
        IdentitySchema identitySchema = new IdentitySchema("schema-id");
        identitySchema.setSchemaVersion(1.0);
        ProcessSpec processSpec = new ProcessSpec("NEW");
        processSpec.setType("NEW");
        processSpec.setIdVersion(1.0);

        when(identitySchemaDao.findLatestSchema()).thenReturn(identitySchema);
        when(processSpecDao.getProcessSpecFromProcessId("NEW", 1.0)).thenReturn(processSpec);
        when(identitySchemaDao.findIdentitySchema(1.0, "NEW")).thenReturn(null);

        identitySchemaRepository.getProcessSpecFields(context, "NEW");
    }

    @Test(expected = Exception.class)
    public void testGetProcessSpecFields_FileTampered() throws Exception {
        File processSpecFile = new File(testDirectory, "NEW");
        try (FileWriter writer = new FileWriter(processSpecFile)) {
            writer.write("content");
        }

        IdentitySchema identitySchema = new IdentitySchema("schema-id");
        identitySchema.setSchemaVersion(1.0);
        identitySchema.setFileName("NEW");
        identitySchema.setFileLength(999L);
        identitySchema.setFileHash("hash");

        ProcessSpec processSpec = new ProcessSpec("NEW");
        processSpec.setType("NEW");
        processSpec.setIdVersion(1.0);

        when(identitySchemaDao.findLatestSchema()).thenReturn(identitySchema);
        when(processSpecDao.getProcessSpecFromProcessId("NEW", 1.0)).thenReturn(processSpec);
        when(identitySchemaDao.findIdentitySchema(1.0, "NEW")).thenReturn(identitySchema);

        identitySchemaRepository.getProcessSpecFields(context, "NEW");
    }
}