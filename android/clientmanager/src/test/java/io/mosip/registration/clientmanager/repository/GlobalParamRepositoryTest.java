package io.mosip.registration.clientmanager.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import android.content.Context;
import android.util.Log;

import androidx.room.Room;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.mosip.registration.clientmanager.config.ClientDatabase;
import io.mosip.registration.clientmanager.constant.RegistrationConstants;
import io.mosip.registration.clientmanager.dao.GlobalParamDao;
import io.mosip.registration.clientmanager.dao.LocalConfigDAO;
import io.mosip.registration.clientmanager.entity.GlobalParam;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class GlobalParamRepositoryTest {

    private static final String GLOBAL_PARAM_STRING_ID = "mosip.lang-code";
    private static final String GLOBAL_PARAM_STRING_VALUE = "eng";

    private static final String GLOBAL_PARAM_BOOLEAN_ID = "mosip.isSyncJobActive";
    private static final Boolean GLOBAL_PARAM_BOOLEAN_VALUE = true;

    private static final String GLOBAL_PARAM_INT_ID = "mosip.syncJobId";
    private static final int GLOBAL_PARAM_INT_VALUE = 1;

    private static final String GLOBAL_PARAM_STRING_ID_NOT_CACHED = "mosip.lang-code-not-cached";

    Context appContext;
    ClientDatabase clientDatabase;
    GlobalParamRepository globalParamRepository;
    GlobalParamDao globalParamDao;
    LocalConfigDAO mockLocalConfigDAO;

    @Before
    public void setUp() {
        clearGlobalParamCache();
        appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        clientDatabase = Room.inMemoryDatabaseBuilder(appContext, ClientDatabase.class)
                .allowMainThreadQueries()
                .build();

        globalParamDao = clientDatabase.globalParamDao();
        mockLocalConfigDAO = mock(LocalConfigDAO.class);
        when(mockLocalConfigDAO.getLocalConfigurations()).thenReturn(new HashMap<String, String>());
        globalParamRepository = new GlobalParamRepository(globalParamDao, mockLocalConfigDAO);
        globalParamRepository.refreshConfigurationCache();
    }

    @After
    public void tearDown() {
        clientDatabase.close();
        clearGlobalParamCache();
    }

    private void clearGlobalParamCache() {
        try {
            Field cacheField = GlobalParamRepository.class.getDeclaredField("globalParamMap");
            cacheField.setAccessible(true);
            Map<String, String> cache = (Map<String, String>) cacheField.get(null);
            if (cache != null) {
                cache.clear();
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to clear global param cache", e);
        }
    }

    @Test
    public void saveGlobal() {
        globalParamRepository.saveGlobalParam(GLOBAL_PARAM_STRING_ID, GLOBAL_PARAM_STRING_VALUE);

        String globalParamValue = globalParamRepository.getGlobalParamValue(GLOBAL_PARAM_STRING_ID);
        assertEquals(GLOBAL_PARAM_STRING_VALUE, globalParamValue);

        String globalParamCachedValue = globalParamRepository.getCachedStringGlobalParam(GLOBAL_PARAM_STRING_ID);
        assertEquals(GLOBAL_PARAM_STRING_VALUE, globalParamCachedValue);
    }

    @Test
    public void saveGlobalParamList() {
        List<GlobalParam> globalParamList = new ArrayList<>();
        globalParamList.add(new GlobalParam(GLOBAL_PARAM_BOOLEAN_ID, GLOBAL_PARAM_BOOLEAN_ID, GLOBAL_PARAM_BOOLEAN_VALUE.toString(), true));
        globalParamList.add(new GlobalParam(GLOBAL_PARAM_STRING_ID, GLOBAL_PARAM_STRING_ID, GLOBAL_PARAM_STRING_VALUE, true));
        globalParamList.add(new GlobalParam(GLOBAL_PARAM_INT_ID, GLOBAL_PARAM_INT_ID, String.valueOf(GLOBAL_PARAM_INT_VALUE), true));

        globalParamRepository.saveGlobalParams(globalParamList);
        List<GlobalParam> globalParams = globalParamRepository.getGlobalParams();
        assertEquals(3, globalParams.size());
    }

    @Test
    public void getCachedValues() {
        saveGlobalParamList();

        assertEquals(GLOBAL_PARAM_STRING_VALUE, globalParamRepository.getCachedStringGlobalParam(GLOBAL_PARAM_STRING_ID));
        assertEquals(GLOBAL_PARAM_BOOLEAN_VALUE, globalParamRepository.getCachedBooleanGlobalParam(GLOBAL_PARAM_BOOLEAN_ID));
        assertEquals(GLOBAL_PARAM_INT_VALUE, globalParamRepository.getCachedIntegerGlobalParam(GLOBAL_PARAM_INT_ID));
    }

    @Test
    public void getCachedValuesNotFoundTest() {
        assertNull(globalParamRepository.getCachedStringGlobalParam(GLOBAL_PARAM_STRING_ID_NOT_CACHED));
        assertNull(globalParamRepository.getCachedBooleanGlobalParam(GLOBAL_PARAM_STRING_ID_NOT_CACHED));
        assertEquals(0, globalParamRepository.getCachedIntegerGlobalParam(GLOBAL_PARAM_STRING_ID_NOT_CACHED));
    }

    @Test
    public void getMandatoryLanguageCodesTest() {
        globalParamRepository.saveGlobalParam(RegistrationConstants.MANDATORY_LANGUAGES_KEY, "ENG, eng, hin , , HIN");
        List<String> codes = globalParamRepository.getMandatoryLanguageCodes();
        assertEquals(Arrays.asList("eng", "hin"), codes);
    }

    @Test
    public void getOptionalLanguageCodesTest() {
        globalParamRepository.saveGlobalParam(RegistrationConstants.OPTIONAL_LANGUAGES_KEY, "kan, tam, KAN");
        List<String> codes = globalParamRepository.getOptionalLanguageCodes();
        assertEquals(Arrays.asList("kan", "tam"), codes);
    }

    @Test
    public void getMaxLanguageCountTest() {
        globalParamRepository.saveGlobalParam(RegistrationConstants.MAX_LANGUAGES_COUNT_KEY, "5");
        assertEquals(5, globalParamRepository.getMaxLanguageCount());
    }

    @Test
    public void getMaxLanguageCountDefaultTest() {
        globalParamRepository.saveGlobalParam(RegistrationConstants.MAX_LANGUAGES_COUNT_KEY, "0");
        assertEquals(1, globalParamRepository.getMaxLanguageCount());
    }

    @Test
    public void getMinLanguageCountTest() {
        globalParamRepository.saveGlobalParam(RegistrationConstants.MIN_LANGUAGES_COUNT_KEY, "2");
        assertEquals(2, globalParamRepository.getMinLanguageCount());
    }

    @Test
    public void getMinLanguageCountDefaultTest() {
        globalParamRepository.saveGlobalParam(RegistrationConstants.MIN_LANGUAGES_COUNT_KEY, "-1");
        assertEquals(1, globalParamRepository.getMinLanguageCount());
    }

    @Test
    public void getSelectedHandlesTest() {
        globalParamRepository.saveGlobalParam(RegistrationConstants.SELECTED_HANDLES, "phone, email , phone");
        List<String> handles = globalParamRepository.getSelectedHandles();
        assertEquals(Arrays.asList("phone", "email"), handles);
    }

    @Test
    public void getCachedStringDefaults() {
        assertEquals("applicanttype.mvel", globalParamRepository.getCachedStringMAVELScript());
        assertNull(globalParamRepository.getCachedStringPreRegPacketLocation());
    }

    @Test
    public void refreshConfigurationCacheMergesLocalOverrides() {
        globalParamRepository.saveGlobalParam("param1", "remote");
        Map<String, String> overrides = new HashMap<>();
        overrides.put("param1", "local");
        overrides.put("param2", "localOnly");
        when(mockLocalConfigDAO.getLocalConfigurations()).thenReturn(overrides);

        globalParamRepository.refreshConfigurationCache();

        assertEquals("local", globalParamRepository.getCachedStringGlobalParam("param1"));
        assertEquals("localOnly", globalParamRepository.getCachedStringGlobalParam("param2"));

        when(mockLocalConfigDAO.getLocalConfigurations()).thenReturn(new HashMap<String, String>());
    }

    @Test
    public void refreshConfigurationCacheHandlesException() {
        // Test that refreshConfigurationCache handles exceptions gracefully:
        // 1. Method completes without throwing
        // 2. Exception is logged
        // 3. Cache remains usable after exception
        
        // Save a param before the exception to verify cache state
        globalParamRepository.saveGlobalParam("testParam", "testValue");
        
        try (MockedStatic<Log> logMock = Mockito.mockStatic(Log.class)) {
            logMock.when(() -> Log.e(Mockito.anyString(), Mockito.anyString(), Mockito.any(Throwable.class))).thenReturn(0);
            logMock.when(() -> Log.e(Mockito.anyString(), Mockito.anyString())).thenReturn(0);

            RuntimeException testException = new RuntimeException("boom");
            Mockito.doThrow(testException).when(mockLocalConfigDAO).getLocalConfigurations();
            
            // Assertion 1: Method should complete without throwing (implicit - test would fail if exception propagated)
            globalParamRepository.refreshConfigurationCache();
            
            // Assertion 2: Verify exception was logged (with Throwable parameter)
            logMock.verify(() -> Log.e(
                    Mockito.anyString(),
                    Mockito.eq("Error refreshing configuration cache"),
                    Mockito.any(Throwable.class)));
            
            // Assertion 3: Verify cache is still usable (contains previously saved param)
            assertEquals("testValue", globalParamRepository.getCachedStringGlobalParam("testParam"));
        }
        
        // Restore mock for other tests
        Mockito.doReturn(new HashMap<String, String>()).when(mockLocalConfigDAO).getLocalConfigurations();
    }

    @Test
    public void getGlobalParamsByPatternTrimsValues() {
        GlobalParamDao mockDao = mock(GlobalParamDao.class);
        LocalConfigDAO mockLocal = mock(LocalConfigDAO.class);
        when(mockDao.getGlobalParams()).thenReturn(Collections.emptyList());
        when(mockLocal.getLocalConfigurations()).thenReturn(Collections.emptyMap());

        GlobalParamRepository repositoryWithMocks = new GlobalParamRepository(mockDao, mockLocal);

        GlobalParam param = new GlobalParam("id", "demo", " value ", true);
        when(mockDao.findByNameLikeAndIsActiveTrueAndValIsNotNull("demo")).thenReturn(Collections.singletonList(param));

        Map<String, Object> result = repositoryWithMocks.getGlobalParamsByPattern("demo");
        assertEquals("value", result.get("demo"));
    }

    @Test
    public void getGlobalParamsByPattern_withNullValue_putsNullInMap() {
        GlobalParamDao mockDao = mock(GlobalParamDao.class);
        LocalConfigDAO mockLocal = mock(LocalConfigDAO.class);
        when(mockDao.getGlobalParams()).thenReturn(Collections.emptyList());
        when(mockLocal.getLocalConfigurations()).thenReturn(Collections.emptyMap());
        GlobalParamRepository repo = new GlobalParamRepository(mockDao, mockLocal);

        GlobalParam paramWithNull = new GlobalParam("id", "key1", null, true);
        when(mockDao.findByNameLikeAndIsActiveTrueAndValIsNotNull("p")).thenReturn(Collections.singletonList(paramWithNull));

        Map<String, Object> result = repo.getGlobalParamsByPattern("p");
        assertEquals(1, result.size());
        assertNull(result.get("key1"));
    }

    @Test
    public void getCachedStringAgeGroup_andOtherCachedStringGetters() {
        globalParamRepository.saveGlobalParam(RegistrationConstants.AGE_GROUP_CONFIG, "ADULT");
        globalParamRepository.saveGlobalParam(RegistrationConstants.FORGOT_PASSWORD_URL, "http://forgot");
        globalParamRepository.saveGlobalParam(RegistrationConstants.IDLE_TIME, "300");
        globalParamRepository.saveGlobalParam(RegistrationConstants.REFRESHED_LOGIN_TIME, "600");
        globalParamRepository.saveGlobalParam(RegistrationConstants.GPS_DEVICE_ENABLE_FLAG, "true");
        globalParamRepository.saveGlobalParam(RegistrationConstants.DIST_FRM_MACHINE_TO_CENTER, "100");
        globalParamRepository.saveGlobalParam(RegistrationConstants.OPERATOR_ONBOARDING_BIO_ATTRIBUTES, "FINGER,FACE");
        globalParamRepository.saveGlobalParam(RegistrationConstants.ONBOARD_YOURSELF_URL, "http://onboard");
        globalParamRepository.saveGlobalParam(RegistrationConstants.REGISTERING_INDIVIDUAL_URL, "http://reg");
        globalParamRepository.saveGlobalParam(RegistrationConstants.SYNC_DATA_URL, "http://sync");
        globalParamRepository.saveGlobalParam(RegistrationConstants.MAPPING_DEVICES_URL, "http://mapping");
        globalParamRepository.saveGlobalParam(RegistrationConstants.UPLOADING_DATA_URL, "http://upload");
        globalParamRepository.saveGlobalParam(RegistrationConstants.UPDATING_BIOMETRICS_URL, "http://bio");
        globalParamRepository.saveGlobalParam(RegistrationConstants.PWORD_LENGTH, "8");
        globalParamRepository.saveGlobalParam(RegistrationConstants.DOC_SIZE, "1024");
        globalParamRepository.saveGlobalParam(RegistrationConstants.MAX_AGE, "120");
        globalParamRepository.saveGlobalParam(RegistrationConstants.INVALID_LOGIN_COUNT, "5");
        globalParamRepository.saveGlobalParam(RegistrationConstants.INVALID_LOGIN_TIME, "2");
        globalParamRepository.saveGlobalParam(RegistrationConstants.DOC_TYPE, "PDF");
        globalParamRepository.saveGlobalParam(RegistrationConstants.APP_NAME, "REG");
        globalParamRepository.saveGlobalParam(RegistrationConstants.APP_ID, "REG_ID");
        globalParamRepository.saveGlobalParam(RegistrationConstants.DEFAULT_HOST_IP, "127.0.0.1");
        globalParamRepository.saveGlobalParam(RegistrationConstants.DEFAULT_HOST_NAME, "localhost");
        globalParamRepository.saveGlobalParam(RegistrationConstants.FIELDS_TO_RETAIN_ON_PRID_FETCH, "name");
        globalParamRepository.saveGlobalParam(RegistrationConstants.PACKET_STORE_LOCATION, "/data/packets");
        globalParamRepository.saveGlobalParam(RegistrationConstants.JOBS_OFFLINE, "SyncJob");
        globalParamRepository.saveGlobalParam(RegistrationConstants.JOBS_UNTAGGED, "TagJob");
        globalParamRepository.saveGlobalParam(RegistrationConstants.JOBS_RESTART, "RestartJob");

        assertEquals("ADULT", globalParamRepository.getCachedStringAgeGroup());
        assertEquals("http://forgot", globalParamRepository.getCachedStringForgotPassword());
        assertEquals("300", globalParamRepository.getCachedStringIdleTime());
        assertEquals("600", globalParamRepository.getCachedStringRefreshedLoginTime());
        assertEquals("true", globalParamRepository.getCachedStringGpsDeviceEnableFlag());
        assertEquals("100", globalParamRepository.getCachedStringMachineToCenterDistance());
        assertEquals("FINGER,FACE", globalParamRepository.getCachedStringOperatorOnboardingBioAttributes());
        assertEquals("http://onboard", globalParamRepository.getCachedStringOnboardYourselfUrl());
        assertEquals("http://reg", globalParamRepository.getCachedStringRegisteringIndividualUrl());
        assertEquals("http://sync", globalParamRepository.getCachedStringSyncDataUrl());
        assertEquals("http://mapping", globalParamRepository.getCachedStringMappingDevicesUrl());
        assertEquals("http://upload", globalParamRepository.getCachedStringUploadingDataUrl());
        assertEquals("http://bio", globalParamRepository.getCachedStringUpdatingBiometricsUrl());
        assertEquals("8", globalParamRepository.getCachedStringPasswordLength());
        assertEquals("1024", globalParamRepository.getCachedStringDocumentSize());
        assertEquals("120", globalParamRepository.getCachedStringDOBAgeLimit());
        assertEquals("5", globalParamRepository.getCachedStringInvalidLoginCount());
        assertEquals("2", globalParamRepository.getCachedStringInvalidLoginTime());
        assertEquals("PDF", globalParamRepository.getCachedStringDocType());
        assertEquals("REG", globalParamRepository.getCachedStringAppName());
        assertEquals("REG_ID", globalParamRepository.getCachedStringAppId());
        assertEquals("127.0.0.1", globalParamRepository.getCachedStringDefaultHostIp());
        assertEquals("localhost", globalParamRepository.getCachedStringDefaultHostName());
        assertEquals("name", globalParamRepository.getCachedStringFieldsToRetainOnPridFetch());
        assertEquals("/data/packets", globalParamRepository.getCachedStringPacketStoreLocation());
        assertEquals("SyncJob", globalParamRepository.getCachedStringJobsOffline());
        assertEquals("TagJob", globalParamRepository.getCachedStringJobsUntagged());
        assertEquals("RestartJob", globalParamRepository.getCachedStringJobsRestart());
    }

    @Test
    public void getCachedReadTimeout_andWriteTimeout_validAndInvalid() {
        globalParamRepository.saveGlobalParam(RegistrationConstants.HTTP_API_READ_TIMEOUT, "30000");
        globalParamRepository.saveGlobalParam(RegistrationConstants.HTTP_API_WRITE_TIMEOUT, "60000");
        assertEquals(30000L, globalParamRepository.getCachedReadTimeout());
        assertEquals(60000L, globalParamRepository.getCachedWriteTimeout());

        globalParamRepository.saveGlobalParam(RegistrationConstants.HTTP_API_READ_TIMEOUT, "");
        globalParamRepository.saveGlobalParam(RegistrationConstants.HTTP_API_WRITE_TIMEOUT, "invalid");
        assertEquals(0L, globalParamRepository.getCachedReadTimeout());
        assertEquals(0L, globalParamRepository.getCachedWriteTimeout());
    }

    @Test
    public void getCachedIntCaptureTimeout_validZeroAndOverflow() {
        globalParamRepository.saveGlobalParam(RegistrationConstants.CAPTURE_TIMEOUT, "5000");
        assertEquals(5000, globalParamRepository.getCachedIntCaptureTimeout());

        globalParamRepository.saveGlobalParam(RegistrationConstants.CAPTURE_TIMEOUT, "0");
        assertEquals(Integer.parseInt(RegistrationConstants.DEFAULT_CAPTURE_TIMEOUT), globalParamRepository.getCachedIntCaptureTimeout());

        globalParamRepository.saveGlobalParam(RegistrationConstants.CAPTURE_TIMEOUT, "9999999999999");
        assertEquals(Integer.parseInt(RegistrationConstants.DEFAULT_CAPTURE_TIMEOUT), globalParamRepository.getCachedIntCaptureTimeout());
    }

    @Test
    public void getCachedIntegerDiskSpacePRIDUINVID_andRegMaxCountApproveLimit() {
        globalParamRepository.saveGlobalParam(RegistrationConstants.DISK_SPACE, "500");
        globalParamRepository.saveGlobalParam(RegistrationConstants.PRID_LENGTH, "14");
        globalParamRepository.saveGlobalParam(RegistrationConstants.UIN_LENGTH, "12");
        globalParamRepository.saveGlobalParam(RegistrationConstants.VID_LENGTH, "16");
        globalParamRepository.saveGlobalParam(RegistrationConstants.REG_PAK_MAX_CNT_APPRV_LIMIT, "100");
        assertEquals(500, globalParamRepository.getCachedIntegerDiskSpaceSize());
        assertEquals(14, globalParamRepository.getCachedIntegerPRIDLength());
        assertEquals(12, globalParamRepository.getCachedIntegerUINLength());
        assertEquals(16, globalParamRepository.getCachedIntegerVIDLength());
        assertEquals(100, globalParamRepository.getCachedIntRegMaxCountApproveLimit());
    }

    @Test
    public void getBiometricProviderConfig_resolvesNestedConfig() {
        GlobalParamDao mockDao = mock(GlobalParamDao.class);
        LocalConfigDAO mockLocal = mock(LocalConfigDAO.class);
        when(mockDao.getGlobalParams()).thenReturn(Collections.emptyList());
        when(mockLocal.getLocalConfigurations()).thenReturn(Collections.emptyMap());
        GlobalParamRepository repo = new GlobalParamRepository(mockDao, mockLocal);

        String key1 = GlobalParamRepository.BIOMETRIC_SDK_PROVIDERS_PREFIX + ".face.vendor1.classname";
        String key2 = GlobalParamRepository.BIOMETRIC_SDK_PROVIDERS_PREFIX + ".face.vendor1.version";
        String key3 = GlobalParamRepository.BIOMETRIC_SDK_PROVIDERS_PREFIX + ".finger.vendor2.classname";
        List<GlobalParam> params = Arrays.asList(
                new GlobalParam("id1", key1, " com.face.Sdk ", true),
                new GlobalParam("id2", key2, "1.0", true),
                new GlobalParam("id3", key3, "com.finger.Sdk", true));
        when(mockDao.findByNameLikeAndIsActiveTrueAndValIsNotNull(GlobalParamRepository.BIOMETRIC_SDK_PROVIDERS_PREFIX + ".%"))
                .thenReturn(params);

        Map<String, Map<String, Map<String, String>>> config = repo.getBiometricProviderConfig();
        assertEquals(2, config.size());
        assertEquals("com.face.Sdk", config.get("face").get("vendor1").get("classname"));
        assertEquals("1.0", config.get("face").get("vendor1").get("version"));
        assertEquals("com.finger.Sdk", config.get("finger").get("vendor2").get("classname"));
    }

    @Test
    public void getBiometricProviderConfig_skipsShortOrInvalidKeys() {
        GlobalParamDao mockDao = mock(GlobalParamDao.class);
        LocalConfigDAO mockLocal = mock(LocalConfigDAO.class);
        when(mockDao.getGlobalParams()).thenReturn(Collections.emptyList());
        when(mockLocal.getLocalConfigurations()).thenReturn(Collections.emptyMap());
        GlobalParamRepository repo = new GlobalParamRepository(mockDao, mockLocal);

        String prefix = GlobalParamRepository.BIOMETRIC_SDK_PROVIDERS_PREFIX + ".";
        List<GlobalParam> params = Arrays.asList(
                new GlobalParam("id1", prefix + "a.b.c", "val", true),
                new GlobalParam("id2", prefix + "x.y", "val2", true));
        when(mockDao.findByNameLikeAndIsActiveTrueAndValIsNotNull(GlobalParamRepository.BIOMETRIC_SDK_PROVIDERS_PREFIX + ".%"))
                .thenReturn(params);

        Map<String, Map<String, Map<String, String>>> config = repo.getBiometricProviderConfig();
        assertEquals(1, config.size());
        assertEquals("val", config.get("a").get("b").get("c"));
    }
}