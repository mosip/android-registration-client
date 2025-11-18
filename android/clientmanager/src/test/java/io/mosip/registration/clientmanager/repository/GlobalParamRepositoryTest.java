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
        // Save a param before the exception to verify cache state
        globalParamRepository.saveGlobalParam("testParam", "testValue");
        
        try (MockedStatic<Log> logMock = Mockito.mockStatic(Log.class)) {
            logMock.when(() -> Log.e(Mockito.anyString(), Mockito.anyString(), Mockito.any(Throwable.class))).thenReturn(0);
            logMock.when(() -> Log.e(Mockito.anyString(), Mockito.anyString())).thenReturn(0);

            RuntimeException testException = new RuntimeException("boom");
            Mockito.doThrow(testException).when(mockLocalConfigDAO).getLocalConfigurations();
            
            // Method should complete without throwing
            globalParamRepository.refreshConfigurationCache();
            
            // Verify exception was logged (with Throwable parameter)
            logMock.verify(() -> Log.e(
                    Mockito.anyString(),
                    Mockito.eq("Error refreshing configuration cache"),
                    Mockito.any(Throwable.class)));
            
            // Verify cache is still usable (contains previously saved param)
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
}