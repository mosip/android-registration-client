package io.mosip.registration.clientmanager.service;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import android.content.Context;

import android.content.SharedPreferences;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.registration.clientmanager.constant.RegistrationConstants;
import io.mosip.registration.clientmanager.dao.*;
import io.mosip.registration.clientmanager.dto.CenterMachineDto;
import io.mosip.registration.clientmanager.dto.ReasonListDto;
import io.mosip.registration.clientmanager.dto.http.*;
import io.mosip.registration.clientmanager.dto.registration.GenericDto;
import io.mosip.registration.clientmanager.dto.registration.GenericValueDto;
import io.mosip.registration.clientmanager.dto.uispec.ProcessSpecDto;
import io.mosip.registration.clientmanager.entity.*;
import io.mosip.registration.clientmanager.repository.*;
import io.mosip.registration.clientmanager.spi.JobManagerService;
import io.mosip.registration.keymanager.dto.*;
import io.mosip.registration.keymanager.dto.CACertificateResponseDto;
import io.mosip.registration.keymanager.exception.KeymanagerServiceException;
import io.mosip.registration.keymanager.spi.CertificateManagerService;
import io.mosip.registration.keymanager.util.CryptoUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;

import io.mosip.registration.clientmanager.spi.SyncRestService;
import io.mosip.registration.keymanager.spi.ClientCryptoManagerService;
import org.springframework.test.util.ReflectionTestUtils;
import retrofit2.Call;

/**
 * @author Anshul vanawat
 * @since 01/06/2022.
 */

@RunWith(MockitoJUnitRunner.class)
public class MasterDataServiceImplTest {

    @Mock
    private Context mockContext;
    @Mock
    private ObjectMapper mockObjectMapper;
    @Mock
    private SyncRestService mockSyncRestService;
    @Mock
    private ClientCryptoManagerService mockClientCryptoManagerService;
    @Mock
    private MachineRepository mockMachineRepository;
    @Mock
    private ReasonListRepository mockReasonListRepository;
    @Mock
    private RegistrationCenterRepository mockRegistrationCenterRepository;
    @Mock
    private DocumentTypeRepository mockDocumentTypeRepository;
    @Mock
    private ApplicantValidDocRepository mockApplicantValidDocRepository;
    @Mock
    private TemplateRepository mockTemplateRepository;
    @Mock
    private DynamicFieldRepository mockDynamicFieldRepository;
    @Mock
    private LocationRepository mockLocationRepository;
    @Mock
    private GlobalParamRepository mockGlobalParamRepository;
    @Mock
    private IdentitySchemaRepository mockIdentitySchemaRepository;
    @Mock
    private BlocklistedWordRepository mockBlocklistedWordRepository;
    @Mock
    private SyncJobDefRepository mockSyncJobDefRepository;
    @Mock
    private UserDetailRepository mockUserDetailRepository;
    @Mock
    private CertificateManagerService mockCertificateManagerService;
    @Mock
    private LanguageRepository mockLanguageRepository;
    @Mock
    private JobManagerService mockJobManagerService;
    @Mock
    private FileSignatureDao mockFileSignatureDao;

    @InjectMocks
    private MasterDataServiceImpl masterDataService;

    @Mock
    private SharedPreferences mockSharedPreferences;

    @Mock
    private Call<ResponseWrapper<CertificateResponse>> mockCall;

    private final String TEST_APP_NAME = "MockAppName";

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        when(mockContext.getString(anyInt())).thenReturn(TEST_APP_NAME);

        when(mockContext.getSharedPreferences(eq(TEST_APP_NAME), eq(Context.MODE_PRIVATE)))
                .thenReturn(mockSharedPreferences);
    }

    @Test
    public void test_constructor_initializes_sharedPreferences_correctly() {
        assertNotNull(String.valueOf(masterDataService), "MasterDataServiceImpl should be instantiated by @InjectMocks.");

        verify(mockContext).getString(anyInt());
    }

    @Test
    public void test_returns_center_machine_dto_when_data_available() {
        String machineName = "testMachine";
        String machineId = "M001";
        String centerId = "C001";

        MachineMaster machineMaster = new MachineMaster(machineId);
        machineMaster.setName(machineName);
        machineMaster.setIsActive(true);
        machineMaster.setRegCenterId(centerId);

        List<RegistrationCenter> centers = new ArrayList<>();
        RegistrationCenter center1 = new RegistrationCenter(centerId, "eng");
        center1.setName("Test Center English");
        center1.setIsActive(true);

        RegistrationCenter center2 = new RegistrationCenter(centerId, "fra");
        center2.setName("Test Center French");
        center2.setIsActive(true);

        centers.add(center1);
        centers.add(center2);

        Mockito.when(mockClientCryptoManagerService.getMachineName()).thenReturn(machineName);
        Mockito.when(mockMachineRepository.getMachine(machineName)).thenReturn(machineMaster);
        Mockito.when(mockRegistrationCenterRepository.getRegistrationCenter(centerId)).thenReturn(centers);

        CenterMachineDto result = masterDataService.getRegistrationCenterMachineDetails();

        assertNotNull(result);
        assertEquals(machineId, result.getMachineId());
        assertEquals(machineName, result.getMachineName());
        assertEquals(true, result.getMachineStatus());
        assertEquals(centerId, result.getCenterId());
        assertEquals(true, result.getCenterStatus());
        assertEquals(centerId + "_" + machineId, result.getMachineRefId());
        assertEquals(2, result.getCenterNames().size());
        assertEquals("Test Center English", result.getCenterNames().get("eng"));
        assertEquals("Test Center French", result.getCenterNames().get("fra"));
    }

    @Test
    public void test_returns_null_when_machine_not_found() {
        String machineName = "nonExistentMachine";

        Mockito.when(mockClientCryptoManagerService.getMachineName()).thenReturn(machineName);
        Mockito.when(mockMachineRepository.getMachine(machineName)).thenReturn(null);

        CenterMachineDto result = masterDataService.getRegistrationCenterMachineDetails();

        assertNull(result);

        verify(mockRegistrationCenterRepository, Mockito.never()).getRegistrationCenter(Mockito.anyString());
    }

    @Test
    public void test_get_all_reasons_list_for_given_language_code() {
        String langCode = "eng";

        List<ReasonList> reasonLists = new ArrayList<>();
        ReasonList reason1 = new ReasonList();
        reason1.setCode("REA001");
        reason1.setName("Invalid Address");
        reason1.setDescription("Address is invalid");
        reason1.setLangCode("eng");

        ReasonList reason2 = new ReasonList();
        reason2.setCode("REA002");
        reason2.setName("Gender-Photo Mismatch");
        reason2.setDescription("Gender does not match with photo");
        reason2.setLangCode("eng");

        reasonLists.add(reason1);
        reasonLists.add(reason2);

        when(mockReasonListRepository.getAllReasonList(langCode)).thenReturn(reasonLists);

        List<ReasonListDto> result = masterDataService.getAllReasonsList(langCode);

        assertEquals(2, result.size());
        assertEquals("REA001", result.get(0).getCode());
        assertEquals("Invalid Address", result.get(0).getName());
        assertEquals("Address is invalid", result.get(0).getDescription());
        assertEquals("eng", result.get(0).getLangCode());
        assertEquals("REA002", result.get(1).getCode());
        assertEquals("Gender-Photo Mismatch", result.get(1).getName());
    }

    @Test
    public void test_get_all_reasons_list_with_empty_repository_result() {
        String langCode = "fra";
        List<ReasonList> emptyReasonList = new ArrayList<>();
        when(mockReasonListRepository.getAllReasonList(langCode)).thenReturn(emptyReasonList);

        List<ReasonListDto> result = masterDataService.getAllReasonsList(langCode);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        assertEquals(0, result.size());

        verify(mockReasonListRepository).getAllReasonList(langCode);
    }

    @Test
    public void test_returns_predefined_location_hierarchy_levels() {
        String langCode = "eng";

        List<GenericDto> result = masterDataService.getAllLocationHierarchyLevels(langCode);

        assertEquals(5, result.size());
        assertEquals("Country", result.get(0).getName());
        assertEquals("Region", result.get(1).getName());
        assertEquals("Province", result.get(2).getName());
        assertEquals("City", result.get(3).getName());
        assertEquals("Postal Code", result.get(4).getName());

        for (GenericDto dto : result) {
            assertEquals(langCode, dto.getLangCode());
        }
    }

    @Test
    public void test_works_with_any_language_code_including_null() {
        List<GenericDto> nullLangResult = masterDataService.getAllLocationHierarchyLevels(null);

        String arbitraryLangCode = "xyz";
        List<GenericDto> arbitraryLangResult = masterDataService.getAllLocationHierarchyLevels(arbitraryLangCode);

        assertEquals(5, nullLangResult.size());
        for (GenericDto dto : nullLangResult) {
            assertNull(dto.getLangCode());
        }

        assertEquals(5, arbitraryLangResult.size());
        for (GenericDto dto : arbitraryLangResult) {
            assertEquals(arbitraryLangCode, dto.getLangCode());
        }
    }

    @Test
    public void test_returns_dynamic_field_values_for_valid_field_name_and_language() {
        String fieldName = "gender";
        String langCode = "eng";
        List<GenericValueDto> expectedValues = Arrays.asList(
                new GenericValueDto("Male", "M", "eng"),
                new GenericValueDto("Female", "F", "eng")
        );

        when(mockDynamicFieldRepository.getDynamicValues(fieldName, langCode))
                .thenReturn(expectedValues);

        List<GenericValueDto> actualValues = masterDataService.getFieldValues(fieldName, langCode);

        assertEquals(expectedValues.size(), actualValues.size());
        assertEquals(expectedValues, actualValues);
        verify(mockDynamicFieldRepository).getDynamicValues(fieldName, langCode);
    }

    @Test
    public void test_returns_empty_list_when_field_name_does_not_exist() {
        String nonExistentFieldName = "nonExistentField";
        String langCode = "eng";
        List<GenericValueDto> emptyList = new ArrayList<>();

        when(mockDynamicFieldRepository.getDynamicValues(nonExistentFieldName, langCode))
                .thenReturn(emptyList);

        List<GenericValueDto> result = masterDataService.getFieldValues(nonExistentFieldName, langCode);

        assertTrue(result.isEmpty());
        assertEquals(0, result.size());
        verify(mockDynamicFieldRepository).getDynamicValues(nonExistentFieldName, langCode);
    }

    @Test
    public void test_returns_field_values_when_parameters_valid() {
        String fieldName = "gender";
        String code = "MLE";
        List<GenericValueDto> expectedValues = Arrays.asList(
                new GenericValueDto("Male", "MLE", "eng"),
                new GenericValueDto("Homme", "MLE", "fra")
        );

        when(mockDynamicFieldRepository.getDynamicValuesByCode(fieldName, code))
                .thenReturn(expectedValues);

        List<GenericValueDto> result = masterDataService.getFieldValuesByCode(fieldName, code);

        assertEquals(expectedValues.size(), result.size());
        assertEquals(expectedValues, result);
        verify(mockDynamicFieldRepository).getDynamicValuesByCode(fieldName, code);
    }

    @Test
    public void test_throws_exception_when_fieldname_null() {
        String fieldName = null;
        String code = "MLE";

        when(mockDynamicFieldRepository.getDynamicValuesByCode(fieldName, code))
                .thenThrow(new NullPointerException("fieldName is marked non-null but is null"));

        assertThrows(NullPointerException.class, () -> {
            masterDataService.getFieldValuesByCode(fieldName, code);
        });

        verify(mockDynamicFieldRepository).getDynamicValuesByCode(fieldName, code);
    }

    @Test
    public void test_returns_location_data_when_parent_code_provided() {
        String parentCode = "REG001";
        String langCode = "eng";
        List<GenericValueDto> expectedLocations = Arrays.asList(
                new GenericValueDto("Location1", "LOC1", "eng"),
                new GenericValueDto("Location2", "LOC2", "eng")
        );
        Mockito.when(mockLocationRepository.getLocations(parentCode, langCode)).thenReturn(expectedLocations);

        List<GenericValueDto> result = masterDataService.findLocationByParentHierarchyCode(parentCode, langCode);

        assertEquals(expectedLocations, result);
        Mockito.verify(mockLocationRepository).getLocations(parentCode, langCode);
    }

    @Test
    public void test_handles_null_parent_code() {
        String langCode = "eng";
        List<GenericValueDto> expectedParentLocations = Arrays.asList(
                new GenericValueDto("ParentLocation1", "PLC1", "eng"),
                new GenericValueDto("ParentLocation2", "PLC2", "eng")
        );
        Mockito.when(mockLocationRepository.getLocations(null, langCode)).thenReturn(expectedParentLocations);

        List<GenericValueDto> result = masterDataService.findLocationByParentHierarchyCode(null, langCode);

        assertEquals(expectedParentLocations, result);

        Mockito.verify(mockLocationRepository).getLocations(null, langCode);
    }

    @Test
    public void test_find_location_by_valid_code() {
        String locationCode = "LOC001";
        List<GenericValueDto> expectedLocations = Arrays.asList(
                new GenericValueDto("Location 1", locationCode, "eng")
        );

        Mockito.when(mockLocationRepository.getLocationsByCode(locationCode)).thenReturn(expectedLocations);

        List<GenericValueDto> result = masterDataService.findLocationByCode(locationCode);

        Assertions.assertEquals(expectedLocations, result);
        Mockito.verify(mockLocationRepository).getLocationsByCode(locationCode);
    }

    @Test
    public void test_find_location_by_null_code() {
        String locationCode = null;
        List<GenericValueDto> expectedEmptyList = Collections.emptyList();

        Mockito.when(mockLocationRepository.getLocationsByCode(locationCode)).thenReturn(expectedEmptyList);

        List<GenericValueDto> result = masterDataService.findLocationByCode(locationCode);

        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.isEmpty());
        Mockito.verify(mockLocationRepository).getLocationsByCode(locationCode);
    }

    @Test
    public void test_find_location_by_hierarchy_level_returns_locations() {
        int hierarchyLevel = 2;
        String langCode = "eng";

        List<GenericValueDto> expectedLocations = Arrays.asList(
                new GenericValueDto("New York", "NY", "eng"),
                new GenericValueDto("California", "CA", "eng")
        );

        Mockito.when(mockLocationRepository.getLocationsBasedOnHierarchyLevel(hierarchyLevel, langCode))
                .thenReturn(expectedLocations);

        List<GenericValueDto> actualLocations = masterDataService.findLocationByHierarchyLevel(hierarchyLevel, langCode);

        assertEquals(expectedLocations, actualLocations);
        Mockito.verify(mockLocationRepository).getLocationsBasedOnHierarchyLevel(hierarchyLevel, langCode);
    }

    @Test
    public void test_find_location_by_hierarchy_level_returns_empty_list() {
        int hierarchyLevel = 5;
        String langCode = "eng";

        List<GenericValueDto> emptyList = Collections.emptyList();
        Mockito.when(mockLocationRepository.getLocationsBasedOnHierarchyLevel(hierarchyLevel, langCode))
                .thenReturn(emptyList);

        List<GenericValueDto> result = masterDataService.findLocationByHierarchyLevel(hierarchyLevel, langCode);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        Mockito.verify(mockLocationRepository).getLocationsBasedOnHierarchyLevel(hierarchyLevel, langCode);
    }

    @Test
    public void test_get_document_types_with_valid_parameters() {
        String categoryCode = "POI";
        String applicantType = "ADULT";
        String langCode = "eng";

        List<String> expectedDocTypes = Arrays.asList("Passport", "Driving License");
        when(mockApplicantValidDocRepository.getDocumentTypes(applicantType, categoryCode, langCode))
                .thenReturn(expectedDocTypes);

        List<String> actualDocTypes = masterDataService.getDocumentTypes(categoryCode, applicantType, langCode);

        assertEquals(expectedDocTypes, actualDocTypes);
        verify(mockApplicantValidDocRepository).getDocumentTypes(applicantType, categoryCode, langCode);
    }

    @Test
    public void test_get_document_types_with_null_applicant_type() {
        String categoryCode = "POI";
        String applicantType = null;
        String langCode = "eng";

        List<String> expectedDocTypes = Arrays.asList("Passport", "Driving License", "National ID");
        when(mockApplicantValidDocRepository.getDocumentTypes(null, categoryCode, langCode))
                .thenReturn(expectedDocTypes);

        List<String> actualDocTypes = masterDataService.getDocumentTypes(categoryCode, applicantType, langCode);

        assertEquals(expectedDocTypes, actualDocTypes);
        verify(mockApplicantValidDocRepository).getDocumentTypes(null, categoryCode, langCode);
    }

    @Test
    public void test_get_template_content_returns_template_from_repository() {
        String templateName = "registration-receipt";
        String langCode = "eng";
        String expectedTemplate = "<html><body>Template Content</body></html>";

        when(mockTemplateRepository.getTemplate(templateName, langCode))
                .thenReturn(expectedTemplate);

        String actualTemplate = masterDataService.getTemplateContent(templateName, langCode);

        assertEquals(expectedTemplate, actualTemplate);
        verify(mockTemplateRepository).getTemplate(templateName, langCode);
    }

    @Test
    public void test_get_template_content_returns_empty_when_no_template_found() {
        String templateName = "non-existent-template";
        String langCode = "eng";
        String emptyTemplate = "";

        when(mockTemplateRepository.getTemplate(templateName, langCode))
                .thenReturn(emptyTemplate);

        String actualTemplate = masterDataService.getTemplateContent(templateName, langCode);

        assertEquals(emptyTemplate, actualTemplate);
        assertTrue(actualTemplate.isEmpty());
        verify(mockTemplateRepository).getTemplate(templateName, langCode);
    }

    @Test
    public void test_retrieves_preview_template_content() {
        String templateTypeCode = "IDC";
        String langCode = "eng";
        String expectedContent = "<html><body>Preview Template</body></html>";

        when(mockTemplateRepository.getPreviewTemplate(templateTypeCode, langCode))
                .thenReturn(expectedContent);

        String actualContent = masterDataService.getPreviewTemplateContent(templateTypeCode, langCode);

        assertEquals(expectedContent, actualContent);
        verify(mockTemplateRepository).getPreviewTemplate(templateTypeCode, langCode);
    }

    @Test
    public void test_handles_null_template_type_code() {
        String templateTypeCode = null;
        String langCode = "eng";
        String expectedContent = "";

        when(mockTemplateRepository.getPreviewTemplate(templateTypeCode, langCode))
                .thenReturn(expectedContent);

        String actualContent = masterDataService.getPreviewTemplateContent(templateTypeCode, langCode);

        assertEquals(expectedContent, actualContent);
        assertTrue(actualContent.isEmpty());
        verify(mockTemplateRepository).getPreviewTemplate(templateTypeCode, langCode);
    }

    @Test
    public void test_get_all_languages_returns_languages_from_repository() {
        List<Language> expectedLanguages = new ArrayList<>();
        Language english = new Language("eng");
        english.setName("English");
        english.setNativeName("English");
        english.setIsActive(true);
        english.setIsDeleted(false);

        Language french = new Language("fra");
        french.setName("French");
        french.setNativeName("Français");
        french.setIsActive(true);
        french.setIsDeleted(false);

        expectedLanguages.add(english);
        expectedLanguages.add(french);

        Mockito.when(mockLanguageRepository.getAllLanguages()).thenReturn(expectedLanguages);

        List<Language> actualLanguages = masterDataService.getAllLanguages();

        assertEquals(expectedLanguages, actualLanguages);
        Mockito.verify(mockLanguageRepository).getAllLanguages();
    }

    @Test
    public void test_get_all_languages_returns_empty_list_when_no_languages_available() {
        List<Language> emptyLanguageList = new ArrayList<>();
        Mockito.when(mockLanguageRepository.getAllLanguages()).thenReturn(emptyLanguageList);

        List<Language> actualLanguages = masterDataService.getAllLanguages();

        assertTrue(actualLanguages.isEmpty());
        assertEquals(0, actualLanguages.size());
        Mockito.verify(mockLanguageRepository).getAllLanguages();
    }

    @Test
    public void test_find_locations_by_lang_code_returns_filtered_list() {
        String langCode = "eng";
        List<Location> expectedLocations = new ArrayList<>();
        Location location1 = new Location("LOC1", langCode);
        location1.setName("Location 1");
        Location location2 = new Location("LOC2", langCode);
        location2.setName("Location 2");
        expectedLocations.add(location1);
        expectedLocations.add(location2);

        Mockito.when(mockLocationRepository.findAllLocationsByLangCode(langCode)).thenReturn(expectedLocations);

        List<Location> result = masterDataService.findAllLocationsByLangCode(langCode);

        Assertions.assertEquals(expectedLocations.size(), result.size());
        Assertions.assertEquals(expectedLocations, result);
        Mockito.verify(mockLocationRepository).findAllLocationsByLangCode(langCode);
    }

    @Test
    public void test_find_locations_by_lang_code_returns_empty_list_when_none_exist() {
        String langCode = "fra";
        List<Location> emptyList = new ArrayList<>();

        Mockito.when(mockLocationRepository.findAllLocationsByLangCode(langCode)).thenReturn(emptyList);

        List<Location> result = masterDataService.findAllLocationsByLangCode(langCode);

        Assertions.assertTrue(result.isEmpty());
        Mockito.verify(mockLocationRepository).findAllLocationsByLangCode(langCode);
    }

    @Test
    public void test_save_global_param_with_valid_parameters() {
        String id = "testId";
        String value = "testValue";

        masterDataService.saveGlobalParam(id, value);

        Mockito.verify(mockGlobalParamRepository, Mockito.times(1)).saveGlobalParam(id, value);
    }

    @Test
    public void test_returns_empty_string_when_global_param_value_is_null() {
        Mockito.when(mockGlobalParamRepository.getGlobalParamValue("testId")).thenReturn(null);

        String result = masterDataService.getGlobalParamValue("testId");

        assertEquals("", result);
        Mockito.verify(mockGlobalParamRepository).getGlobalParamValue("testId");
    }

    @Test
    public void test_returns_value_from_repository_when_not_null() {
        Mockito.when(mockGlobalParamRepository.getGlobalParamValue("testId")).thenReturn("testValue");

        String result = masterDataService.getGlobalParamValue("testId");

        assertEquals("testValue", result);
        Mockito.verify(mockGlobalParamRepository).getGlobalParamValue("testId");
    }

    @Test
    public void test_successfully_decrypts_data_using_client_crypto_manager() throws Exception {
        ReflectionTestUtils.setField(masterDataService, "clientCryptoManagerService", mockClientCryptoManagerService);
        ReflectionTestUtils.setField(masterDataService, "dynamicFieldRepository", mockDynamicFieldRepository);

        String encryptedData = "encryptedData";
        String decryptedData = "WyJ7XCJpZFwiOlwiZmllbGQxXCIsXCJkYXRhVHlwZVwiOlwidGV4dFwiLFwibmFtZVwiOlwiRmllbGQgMVwiLFwibGFuZ0NvZGVcIjpcImVuZ1wiLFwiaXNBY3RpdmVcIjp0cnVlLFwiZmllbGRWYWxcIjpbe1wiY29kZVwiOlwiRlJcIixcInZhbHVlXCI6XCJGb3JlaWduXCIsXCJhY3RpdmVcIjpmYWxzZX1dfSJd";
        CryptoResponseDto cryptoResponseDto = new CryptoResponseDto();
        cryptoResponseDto.setValue(decryptedData);

        Mockito.when(mockClientCryptoManagerService.decrypt(Mockito.any(CryptoRequestDto.class))).thenReturn(cryptoResponseDto);

        ReflectionTestUtils.invokeMethod(masterDataService, "saveDynamicData", encryptedData);

        Mockito.verify(mockClientCryptoManagerService).decrypt(Mockito.any(CryptoRequestDto.class));
        Mockito.verify(mockDynamicFieldRepository).saveDynamicField(Mockito.any(JSONObject.class));
    }


    @Test
    public void test_empty_json_array_after_decryption() throws Exception {
        ReflectionTestUtils.setField(masterDataService, "clientCryptoManagerService", mockClientCryptoManagerService);
        ReflectionTestUtils.setField(masterDataService, "dynamicFieldRepository", mockDynamicFieldRepository);

        String encryptedData = "encryptedData";
        String decryptedData = "W10=";

        CryptoResponseDto cryptoResponseDto = new CryptoResponseDto();
        cryptoResponseDto.setValue(decryptedData);

        Mockito.when(mockClientCryptoManagerService.decrypt(Mockito.any(CryptoRequestDto.class))).thenReturn(cryptoResponseDto);

        ReflectionTestUtils.invokeMethod(masterDataService, "saveDynamicData", encryptedData);

        Mockito.verify(mockClientCryptoManagerService).decrypt(Mockito.any(CryptoRequestDto.class));
        Mockito.verify(mockDynamicFieldRepository, Mockito.never()).saveDynamicField(Mockito.any(JSONObject.class));
    }


    @Test
    public void test_returns_server_version_from_global_parameters() {
       ReflectionTestUtils.setField(masterDataService, "globalParamRepository", mockGlobalParamRepository);

        String expectedVersion = "1.2.0";
        Mockito.when(mockGlobalParamRepository.getCachedStringGlobalParam(RegistrationConstants.SERVER_VERSION))
                .thenReturn(expectedVersion);

        String actualVersion = ReflectionTestUtils.invokeMethod(masterDataService, "getServerVersionFromConfigs");

        assertEquals(expectedVersion, actualVersion);
    }

    @Test
    public void test_returns_null_when_server_version_not_found() {
        ReflectionTestUtils.setField(masterDataService, "globalParamRepository", mockGlobalParamRepository);

        Mockito.when(mockGlobalParamRepository.getCachedStringGlobalParam(RegistrationConstants.SERVER_VERSION))
                .thenReturn(null);

        String actualVersion = ReflectionTestUtils.invokeMethod(masterDataService, "getServerVersionFromConfigs");

        assertNull(actualVersion);
    }

    @Test
    public void test_returns_file_range_when_signature_exists_and_file_length_less_than_content_length() {
        ReflectionTestUtils.setField(masterDataService, "fileSignatureDao", mockFileSignatureDao);

        Path mockPath = Mockito.mock(Path.class);
        File mockFile = Mockito.mock(File.class);
        FileSignature mockSignature = new FileSignature();
        mockSignature.setFileName("test.txt");
        mockSignature.setContentLength(1000);

        Mockito.when(mockPath.toFile()).thenReturn(mockFile);
        Mockito.when(mockFile.getName()).thenReturn("test.txt");
        Mockito.when(mockFile.length()).thenReturn(500L);
        Mockito.when(mockFileSignatureDao.findByFileName("test.txt")).thenReturn(Optional.of(mockSignature));

        long[] result = ReflectionTestUtils.invokeMethod(masterDataService, "getFileRange", mockPath);

        assertNotNull(result);
        assertEquals(500L, result[0]);
        assertEquals(1000, result[1]);
    }

    @Test
    public void test_throws_null_pointer_exception_when_path_is_null() {
        ReflectionTestUtils.setField(masterDataService, "fileSignatureDao", mockFileSignatureDao);

        assertThrows(NullPointerException.class, () -> {
            ReflectionTestUtils.invokeMethod(masterDataService, "getFileRange", (Path) null);
        });

        Mockito.verify(mockFileSignatureDao, Mockito.never()).findByFileName(Mockito.anyString());
    }

    @Test
    public void test_save_file_signature_successfully() {
        Path path = Paths.get("test.txt");
        boolean isFileEncrypted = true;
        String signature = "test-signature";
        Integer contentLength = 100;

        ReflectionTestUtils.setField(masterDataService, "fileSignatureDao", mockFileSignatureDao);

        ReflectionTestUtils.invokeMethod(masterDataService, "saveFileSignature", path, isFileEncrypted, signature, contentLength);

        ArgumentCaptor<FileSignature> fileSignatureCaptor = ArgumentCaptor.forClass(FileSignature.class);
        Mockito.verify(mockFileSignatureDao).insert(fileSignatureCaptor.capture());

        FileSignature capturedFileSignature = fileSignatureCaptor.getValue();
        assertEquals("test.txt", capturedFileSignature.getFileName());
        assertEquals(signature, capturedFileSignature.getSignature());
        assertEquals(isFileEncrypted, capturedFileSignature.getEncrypted());
        assertEquals(contentLength, capturedFileSignature.getContentLength());
    }

    @Test
    public void test_save_file_signature_returns_early_when_signature_null() {
        Path path = Paths.get("test.txt");
        boolean isFileEncrypted = true;
        String signature = null;
        Integer contentLength = 100;

        ReflectionTestUtils.invokeMethod(masterDataService, "saveFileSignature", path, isFileEncrypted, signature, contentLength);

        Mockito.verify(mockFileSignatureDao, Mockito.never()).insert(Mockito.any(FileSignature.class));
    }

    @Test
    public void test_returns_current_time_in_iso_format() {
        String currentTime = ReflectionTestUtils.invokeMethod(masterDataService, "getCurrentTime");

        assertNotNull(currentTime);

        assertTrue(currentTime.matches("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\.\\d+Z"));

        Instant parsedTime = Instant.parse(currentTime);
        Instant now = Instant.now();

        long diffInMillis = Math.abs(now.toEpochMilli() - parsedTime.toEpochMilli());
        assertTrue("Time difference should be less than 5 seconds", diffInMillis < 5000);
    }

    @Test
    public void test_returns_system_time_even_if_manipulated() {
        Instant beforeTest = Instant.now();

        String currentTimeStr = ReflectionTestUtils.invokeMethod(masterDataService, "getCurrentTime");
        Instant currentTime = Instant.parse(currentTimeStr);

        Instant afterTest = Instant.now();

        boolean isTimeInRange = (currentTime.isAfter(beforeTest.minusMillis(1000)) &&
                currentTime.isBefore(afterTest.plusMillis(1000)));

        assertTrue("The returned time should reflect the actual system time", isTimeInRange);

        long diffFromNow = Math.abs(Instant.now().toEpochMilli() - currentTime.toEpochMilli());
        assertTrue("Time should be close to actual system time", diffFromNow < 5000);
    }

    @Test
    public void test_get_hierarchy_level_returns_level_for_valid_name() {
        ReflectionTestUtils.setField(masterDataService, "locationRepository", mockLocationRepository);

        String hierarchyLevelName = "PROVINCE";
        Integer expectedLevel = 2;
        Mockito.when(mockLocationRepository.getHierarchyLevel(hierarchyLevelName)).thenReturn(expectedLevel);

        Integer actualLevel = masterDataService.getHierarchyLevel(hierarchyLevelName);

        assertEquals(expectedLevel, actualLevel);
        Mockito.verify(mockLocationRepository).getHierarchyLevel(hierarchyLevelName);
    }

    @Test
    public void test_get_hierarchy_level_handles_null_parameter() {
        ReflectionTestUtils.setField(masterDataService, "locationRepository", mockLocationRepository);

        String hierarchyLevelName = null;
        Integer expectedLevel = null;
        Mockito.when(mockLocationRepository.getHierarchyLevel(hierarchyLevelName)).thenReturn(expectedLevel);

        Integer actualLevel = masterDataService.getHierarchyLevel(hierarchyLevelName);

        assertNull(actualLevel);
        Mockito.verify(mockLocationRepository).getHierarchyLevel(hierarchyLevelName);
    }

    @Test
    public void test_returns_result_value() {
        try {
            java.lang.reflect.Field resultField = MasterDataServiceImpl.class.getDeclaredField("result");
            resultField.setAccessible(true);
            resultField.set(masterDataService, "test_result");
        } catch (Exception e) {
            fail("Failed to set result field: " + e.getMessage());
        }

        String actualResult = masterDataService.onResponseComplete();

        assertEquals("test_result", actualResult);
    }

    @Test
    public void test_returns_null_when_result_is_null() {
        try {
            java.lang.reflect.Field resultField = MasterDataServiceImpl.class.getDeclaredField("result");
            resultField.setAccessible(true);
            resultField.set(masterDataService, null);
        } catch (Exception e) {
            fail("Failed to set result field: " + e.getMessage());
        }

        String actualResult = masterDataService.onResponseComplete();

        assertNull(actualResult);
    }

    @Test
    public void test_save_user_details_successfully() throws JSONException {
        String encryptedData = "encrypted_data";
        JSONArray decryptedData = new JSONArray();
        decryptedData.put(new JSONObject("{\"userId\":\"user1\",\"isActive\":true,\"regCenterId\":\"REG001\"}"));

        ReflectionTestUtils.setField(masterDataService, "userDetailRepository", mockUserDetailRepository);
        ReflectionTestUtils.setField(masterDataService, "clientCryptoManagerService", mockClientCryptoManagerService);

        CryptoResponseDto cryptoResponseDto = new CryptoResponseDto();
        cryptoResponseDto.setValue(CryptoUtil.base64encoder.encodeToString(decryptedData.toString().getBytes()));
        when(mockClientCryptoManagerService.decrypt(any(CryptoRequestDto.class))).thenReturn(cryptoResponseDto);

        ReflectionTestUtils.invokeMethod(masterDataService, "saveUserDetails", encryptedData);

        verify(mockUserDetailRepository, times(1)).saveUserDetail(any(JSONArray.class));
    }

    @Test
    public void test_save_user_details_handles_exception() throws JSONException {
        String encryptedData = "encrypted_data";

        ReflectionTestUtils.setField(masterDataService, "userDetailRepository", mockUserDetailRepository);
        ReflectionTestUtils.setField(masterDataService, "clientCryptoManagerService", mockClientCryptoManagerService);

        when(mockClientCryptoManagerService.decrypt(any(CryptoRequestDto.class))).thenThrow(new KeymanagerServiceException("KER-CRY-001", "Decryption failed"));

        ReflectionTestUtils.invokeMethod(masterDataService, "saveUserDetails", encryptedData);

        verify(mockUserDetailRepository, never()).saveUserDetail(any(JSONArray.class));
    }

    @Test
    public void test_successfully_parses_json_and_extracts_process_specs() throws Exception {
        Field contextField = MasterDataServiceImpl.class.getDeclaredField("context");
        contextField.setAccessible(true);
        contextField.set(masterDataService, mockContext);

        Field identitySchemaRepositoryField = MasterDataServiceImpl.class.getDeclaredField("identitySchemaRepository");
        identitySchemaRepositoryField.setAccessible(true);
        identitySchemaRepositoryField.set(masterDataService, mockIdentitySchemaRepository);

        IdSchemaResponse idSchemaResponse = new IdSchemaResponse();
        idSchemaResponse.setIdVersion(1.0);

        String jsonString = "{\"registrationProcess\":{\"id\":\"reg-process\",\"order\":1,\"flow\":\"registration\",\"isActive\":true}}";

        ProcessSpecDto expectedProcessSpecDto = new ProcessSpecDto();
        expectedProcessSpecDto.setId("reg-process");
        expectedProcessSpecDto.setOrder(1);
        expectedProcessSpecDto.setFlow("registration");
        expectedProcessSpecDto.setActive(true);

        ReflectionTestUtils.invokeMethod(masterDataService, "saveProcessSpec", idSchemaResponse, jsonString);

        Mockito.verify(mockIdentitySchemaRepository).createProcessSpec(
                Mockito.eq(mockContext),
                Mockito.eq("registrationProcess"),
                Mockito.eq(1.0),
                Mockito.argThat(processSpecDto ->
                        processSpecDto.getId().equals("reg-process") &&
                                processSpecDto.getOrder() == 1 &&
                                processSpecDto.getFlow().equals("registration") &&
                                processSpecDto.isActive() == true
                )
        );
    }


    @Test
    public void test_handles_empty_json_string() throws Exception {
        Field contextField = MasterDataServiceImpl.class.getDeclaredField("context");
        contextField.setAccessible(true);
        contextField.set(masterDataService, mockContext);

        Field identitySchemaRepositoryField = MasterDataServiceImpl.class.getDeclaredField("identitySchemaRepository");
        identitySchemaRepositoryField.setAccessible(true);
        identitySchemaRepositoryField.set(masterDataService, mockIdentitySchemaRepository);

        IdSchemaResponse idSchemaResponse = new IdSchemaResponse();
        idSchemaResponse.setIdVersion(1.0);

        String emptyJsonString = "{}";

        ReflectionTestUtils.invokeMethod(masterDataService, "saveProcessSpec", idSchemaResponse, emptyJsonString);

        Mockito.verify(mockIdentitySchemaRepository, Mockito.never()).createProcessSpec(
                Mockito.any(Context.class),
                Mockito.anyString(),
                Mockito.anyDouble(),
                Mockito.any(ProcessSpecDto.class)
        );
    }

    @Test
    public void test_iterates_and_identifies_process_keys() throws Exception {
        String jsonString = "{\"key1\":\"value1\", \"testProcess\":{\"id\":\"123\", \"order\":1, \"flow\":\"flow1\", \"isActive\":true}}";
        IdSchemaResponse idSchemaResponse = new IdSchemaResponse();
        idSchemaResponse.setIdVersion(1.0);

        ReflectionTestUtils.invokeMethod(masterDataService, "saveProcessSpec", idSchemaResponse, jsonString);

        verify(mockIdentitySchemaRepository, times(1)).createProcessSpec(any(Context.class), eq("testProcess"), eq(1.0), any(ProcessSpecDto.class));
    }

    @Test
    public void test_converts_json_to_processspecdto() throws Exception {
        String jsonString = "{\"testProcess\":{\"id\":\"123\", \"order\":1, \"flow\":\"flow1\", \"isActive\":true}}";
        IdSchemaResponse idSchemaResponse = new IdSchemaResponse();
        idSchemaResponse.setIdVersion(1.0);

        ReflectionTestUtils.invokeMethod(masterDataService, "saveProcessSpec", idSchemaResponse, jsonString);

        ArgumentCaptor<ProcessSpecDto> captor = ArgumentCaptor.forClass(ProcessSpecDto.class);
        verify(mockIdentitySchemaRepository).createProcessSpec(any(Context.class), eq("testProcess"), eq(1.0), captor.capture());

        ProcessSpecDto capturedDto = captor.getValue();
        assertEquals("123", capturedDto.getId());
        assertEquals(1, capturedDto.getOrder());
        assertEquals("flow1", capturedDto.getFlow());
        assertTrue(capturedDto.isActive());
    }

    @Test
    public void test_handles_malformed_json_data() {
        IdSchemaResponse idSchemaResponse = new IdSchemaResponse();
        idSchemaResponse.setIdVersion(1.0);
        String malformedJsonString = "{ \"firstProcess\": { \"id\": \"proc1\", \"order\": 1, \"flow\": \"flow1\", \"isActive\": true, ";

        assertThrows(JSONException.class, () -> {
            ReflectionTestUtils.invokeMethod(masterDataService, "saveProcessSpec", idSchemaResponse, malformedJsonString);
        });
    }

    @Test
    public void test_successful_decrypt_and_parse() throws Exception {
        Field cryptoField = MasterDataServiceImpl.class.getDeclaredField("clientCryptoManagerService");
        cryptoField.setAccessible(true);
        cryptoField.set(masterDataService, mockClientCryptoManagerService);

        Field mapperField = MasterDataServiceImpl.class.getDeclaredField("objectMapper");
        mapperField.setAccessible(true);
        mapperField.set(masterDataService, mockObjectMapper);

        String encodedCipher = "test-encoded-cipher";
        String decodedValue = "eyJrZXkxIjoidmFsdWUxIiwia2V5MiI6InZhbHVlMiJ9";
        Map<String, Object> expectedMap = new HashMap<>();
        expectedMap.put("key1", "value1");
        expectedMap.put("key2", "value2");

        CryptoResponseDto responseDto = new CryptoResponseDto();
        responseDto.setValue(decodedValue);

        Mockito.when(mockClientCryptoManagerService.decrypt(Mockito.any(CryptoRequestDto.class))).thenReturn(responseDto);
        Mockito.when(mockObjectMapper.readValue(Mockito.any(byte[].class), Mockito.eq(HashMap.class))).thenReturn((HashMap) expectedMap);

        Map<String, Object> result = ReflectionTestUtils.invokeMethod(masterDataService, "getParams", encodedCipher);

        assertEquals(expectedMap, result);
        Mockito.verify(mockClientCryptoManagerService).decrypt(Mockito.any(CryptoRequestDto.class));
        Mockito.verify(mockObjectMapper).readValue(Mockito.any(byte[].class), Mockito.eq(HashMap.class));
    }

    @Test
    public void test_returns_empty_map_on_io_exception() throws Exception {
        Field cryptoField = MasterDataServiceImpl.class.getDeclaredField("clientCryptoManagerService");
        cryptoField.setAccessible(true);
        cryptoField.set(masterDataService, mockClientCryptoManagerService);

        Field mapperField = MasterDataServiceImpl.class.getDeclaredField("objectMapper");
        mapperField.setAccessible(true);
        mapperField.set(masterDataService, mockObjectMapper);

        String encodedCipher = "test-encoded-cipher";
        String decodedValue = "eyJrZXkxIjoidmFsdWUxIiwia2V5MiI6InZhbHVlMiJ9";

        CryptoResponseDto responseDto = new CryptoResponseDto();
        responseDto.setValue(decodedValue);

        Mockito.when(mockClientCryptoManagerService.decrypt(Mockito.any(CryptoRequestDto.class))).thenReturn(responseDto);
        Mockito.when(mockObjectMapper.readValue(Mockito.any(byte[].class), Mockito.eq(HashMap.class)))
                .thenThrow(new IOException("Test exception"));

        Map<String, Object> result = ReflectionTestUtils.invokeMethod(masterDataService, "getParams", encodedCipher);

        assertEquals(Collections.EMPTY_MAP, result);
        Mockito.verify(mockClientCryptoManagerService).decrypt(Mockito.any(CryptoRequestDto.class));
        Mockito.verify(mockObjectMapper).readValue(Mockito.any(byte[].class), Mockito.eq(HashMap.class));
    }

    @Test
    public void test_nested_map_flattening() {
        Map<String, Object> nestedMap = new HashMap<>();
        nestedMap.put("key1", "value1");

        Map<String, Object> innerMap = new HashMap<>();
        innerMap.put("innerKey1", "innerValue1");
        innerMap.put("innerKey2", 123);

        Map<String, Object> deeperMap = new HashMap<>();
        deeperMap.put("deepKey", true);

        innerMap.put("deeperMap", deeperMap);
        nestedMap.put("innerMap", innerMap);

        Map<String, String> resultMap = new HashMap<>();

        ReflectionTestUtils.invokeMethod(masterDataService, "parseToMap", nestedMap, resultMap);

        assertEquals(4, resultMap.size());
        assertEquals("value1", resultMap.get("key1"));
        assertEquals("innerValue1", resultMap.get("innerKey1"));
        assertEquals("123", resultMap.get("innerKey2"));
        assertEquals("true", resultMap.get("deepKey"));
    }

    @Test
    public void test_empty_input_map() {
        Map<String, Object> emptyMap = new HashMap<>();

        Map<String, String> resultMap = new HashMap<>();

        ReflectionTestUtils.invokeMethod(masterDataService, "parseToMap", emptyMap, resultMap);

        assertTrue(resultMap.isEmpty());
    }

    @Test
    public void test_parse_headers_from_json_object() throws Exception {
        Path path = mock(Path.class);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("headers", "Content-Type:application/json,Authorization:Bearer token");
        jsonObject.put("encrypted", false);
        jsonObject.put("url", "https://example.com/script");
        boolean isManualSync = false;

        Map<String, String> expectedMap = new HashMap<>();
        expectedMap.put("Content-Type", "application/json");
        expectedMap.put("Authorization", "Bearer token");

        ReflectionTestUtils.invokeMethod(masterDataService, "downloadUrlData", path, jsonObject, isManualSync);

        Map<String, String> capturedMap = getHeadersFromService(jsonObject);

        assertTrue(capturedMap.containsKey("Content-Type"));
        assertEquals("application/json", capturedMap.get("Content-Type"));
        assertTrue(capturedMap.containsKey("Authorization"));
        assertEquals("Bearer token", capturedMap.get("Authorization"));
        assertTrue(capturedMap.containsKey("Range"));
        assertEquals("bytes=0-", capturedMap.get("Range"));
    }

    @Test
    public void test_handle_null_headers() throws Exception {
        Path path = mock(Path.class);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("headers", "");
        jsonObject.put("encrypted", false);
        jsonObject.put("url", "https://example.com/script");
        boolean isManualSync = false;

        ReflectionTestUtils.invokeMethod(masterDataService, "downloadUrlData", path, jsonObject, isManualSync);

        Map<String, String> capturedMap = getHeadersFromService(jsonObject);

        assertEquals(1, capturedMap.size());
        assertTrue(capturedMap.containsKey("Range"));
        assertEquals("bytes=0-", capturedMap.get("Range"));

        jsonObject.put("headers", JSONObject.NULL);
        ReflectionTestUtils.invokeMethod(masterDataService, "downloadUrlData", path, jsonObject, isManualSync);

        capturedMap = getHeadersFromService(jsonObject);
        assertEquals(1, capturedMap.size());
        assertTrue(capturedMap.containsKey("Range"));
    }

    @Test
    public void test_parse_headers_from_json_object_alternative() throws Exception {
        Path path = mock(Path.class);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("headers", "Content-Type:application/json,Authorization:Bearer token");
        jsonObject.put("encrypted", false);
        jsonObject.put("url", "https://example.com/script");
        boolean isManualSync = false;

        ReflectionTestUtils.invokeMethod(masterDataService, "downloadUrlData", path, jsonObject, isManualSync);
    }

    private Map<String, String> getHeadersFromService(JSONObject jsonObject) {
        Map<String, String> headers = new HashMap<>();

        String headersString = jsonObject.optString("headers", "");
        if (!headersString.isEmpty() && !JSONObject.NULL.equals(jsonObject.opt("headers"))) {
            String[] headerPairs = headersString.split(",");
            for (String pair : headerPairs) {
                String[] keyValue = pair.split(":", 2);
                if (keyValue.length == 2) {
                    headers.put(keyValue[0].trim(), keyValue[1].trim());
                }
            }
        }

        headers.put("Range", "bytes=0-");

        return headers;
    }

    @Test
    public void test_save_ca_certificates_with_non_empty_list() {
        ReflectionTestUtils.setField(masterDataService, "certificateManagerService", mockCertificateManagerService);

        List<CACertificateDto> caCertificateDtos = new ArrayList<>();
        CACertificateDto certificateDto = new CACertificateDto();
        certificateDto.setCertId("cert-123");
        certificateDto.setCertData("certificate-data");
        certificateDto.setPartnerDomain("DEVICE");
        certificateDto.setCreatedtimes(LocalDateTime.now());
        caCertificateDtos.add(certificateDto);

        CACertificateResponseDto responseDto = new CACertificateResponseDto();
        responseDto.setStatus("success");
        responseDto.setTimestamp(LocalDateTime.now());

        when(mockCertificateManagerService.uploadCACertificate(any(CACertificateRequestDto.class))).thenReturn(responseDto);

        ReflectionTestUtils.invokeMethod(masterDataService, "saveCACertificate", caCertificateDtos);

        verify(mockCertificateManagerService, times(1)).uploadCACertificate(any(CACertificateRequestDto.class));
    }

    @Test
    public void test_save_ca_certificates_with_null_input() {
        ReflectionTestUtils.setField(masterDataService, "certificateManagerService", mockCertificateManagerService);

        List<CACertificateDto> caCertificateDtos = null;

        ReflectionTestUtils.invokeMethod(masterDataService, "saveCACertificate", caCertificateDtos);

        verify(mockCertificateManagerService, never()).uploadCACertificate(any(CACertificateRequestDto.class));
    }

    @Test
    public void test_sorts_certificates_by_creation_time() {
        List<CACertificateDto> certificates = new ArrayList<>();
        CACertificateDto cert1 = new CACertificateDto();
        cert1.setCreatedtimes(LocalDateTime.of(2023, 10, 1, 0, 0));
        CACertificateDto cert2 = new CACertificateDto();
        cert2.setCreatedtimes(LocalDateTime.of(2023, 9, 1, 0, 0));
        certificates.add(cert1);
        certificates.add(cert2);

        ReflectionTestUtils.invokeMethod(masterDataService, "saveCACertificate", certificates);

        assertEquals(LocalDateTime.of(2023, 9, 1, 0, 0), certificates.get(0).getCreatedtimes());
        assertEquals(LocalDateTime.of(2023, 10, 1, 0, 0), certificates.get(1).getCreatedtimes());
    }


    @Test
    public void test_empty_input_list_handling() {
        List<CACertificateDto> caCertificateDtos = new ArrayList<>();

        ReflectionTestUtils.invokeMethod(masterDataService, "saveCACertificate", caCertificateDtos);
    }

    @Test
    public void test_save_global_params_with_valid_config() {
        ReflectionTestUtils.setField(masterDataService, "context", mockContext);
        ReflectionTestUtils.setField(masterDataService, "globalParamRepository", mockGlobalParamRepository);
        ReflectionTestUtils.setField(masterDataService, "clientCryptoManagerService", mockClientCryptoManagerService);
        ReflectionTestUtils.setField(masterDataService, "objectMapper", mockObjectMapper);

        Map<String, Object> responseMap = new HashMap<>();
        Map<String, Object> configDetailMap = new HashMap<>();
        configDetailMap.put("registrationConfiguration", "encryptedData");
        responseMap.put("configDetail", configDetailMap);

        CryptoResponseDto cryptoResponseDto = new CryptoResponseDto();
        cryptoResponseDto.setValue("base64EncodedValue");
        when(mockClientCryptoManagerService.decrypt(any(CryptoRequestDto.class))).thenReturn(cryptoResponseDto);

        Map<String, Object> decryptedParams = new HashMap<>();
        decryptedParams.put("param1", "value1");
        decryptedParams.put("param2", "value2");
        decryptedParams.put(RegistrationConstants.DEDUPLICATION_ENABLE_FLAG, "true");

        try {
            when(mockObjectMapper.readValue(any(byte[].class), eq(HashMap.class))).thenReturn((HashMap) decryptedParams);
        } catch (IOException e) {
            fail("Mock setup failed");
        }

        SharedPreferences.Editor mockEditor = mock(SharedPreferences.Editor.class);
        when(mockContext.getString(anyInt())).thenReturn("app_name");
        when(mockContext.getSharedPreferences(anyString(), anyInt())).thenReturn(mockSharedPreferences);
        when(mockSharedPreferences.edit()).thenReturn(mockEditor);
        when(mockEditor.putString(anyString(), anyString())).thenReturn(mockEditor);

        when(mockGlobalParamRepository.getCachedStringGlobalParam(RegistrationConstants.DEDUPLICATION_ENABLE_FLAG)).thenReturn("true");

        ReflectionTestUtils.invokeMethod(masterDataService, "saveGlobalParams", responseMap);

        ArgumentCaptor<List<GlobalParam>> globalParamCaptor = ArgumentCaptor.forClass(List.class);
        verify(mockGlobalParamRepository).saveGlobalParams(globalParamCaptor.capture());

        List<GlobalParam> capturedParams = globalParamCaptor.getValue();
        assertNotNull(capturedParams);
        assertFalse(capturedParams.isEmpty());
        assertTrue(capturedParams.stream().anyMatch(param -> param.getId().equals("param1")));
        assertTrue(capturedParams.stream().anyMatch(param -> param.getId().equals("param2")));

        verify(mockEditor).putString(eq(RegistrationConstants.DEDUPLICATION_ENABLE_FLAG), eq("true"));
        verify(mockEditor).apply();
    }

    @Test
    public void test_save_global_params_with_null_config_detail() {
        ReflectionTestUtils.setField(masterDataService, "context", mockContext);
        ReflectionTestUtils.setField(masterDataService, "globalParamRepository", mockGlobalParamRepository);
        ReflectionTestUtils.setField(masterDataService, "clientCryptoManagerService", mockClientCryptoManagerService);

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("configDetail", null);

        SharedPreferences.Editor mockEditor = mock(SharedPreferences.Editor.class);
        when(mockContext.getString(anyInt())).thenReturn("app_name");
        when(mockContext.getSharedPreferences(anyString(), anyInt())).thenReturn(mockSharedPreferences);
        when(mockSharedPreferences.edit()).thenReturn(mockEditor);
        lenient().when(mockEditor.putString(anyString(), anyString())).thenReturn(mockEditor);

        when(mockGlobalParamRepository.getCachedStringGlobalParam(RegistrationConstants.DEDUPLICATION_ENABLE_FLAG)).thenReturn(null);

        ReflectionTestUtils.invokeMethod(masterDataService, "saveGlobalParams", responseMap);

        ArgumentCaptor<List<GlobalParam>> globalParamCaptor = ArgumentCaptor.forClass(List.class);
        verify(mockGlobalParamRepository).saveGlobalParams(globalParamCaptor.capture());

        List<GlobalParam> capturedParams = globalParamCaptor.getValue();
        assertNotNull(capturedParams);
        assertTrue(capturedParams.isEmpty());

        verify(mockEditor).putString(eq(RegistrationConstants.DEDUPLICATION_ENABLE_FLAG), isNull());
        verify(mockEditor).apply();
    }

}
