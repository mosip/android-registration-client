package io.mosip.registration.clientmanager.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import android.content.Context;

import androidx.room.Room;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.mosip.registration.clientmanager.config.ClientDatabase;
import io.mosip.registration.clientmanager.constant.RegistrationConstants;
import io.mosip.registration.clientmanager.dao.GlobalParamDao;
import io.mosip.registration.clientmanager.entity.GlobalParam;
import io.mosip.registration.clientmanager.repository.GlobalParamRepository;

@RunWith(RobolectricTestRunner.class)
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

    @Before
    public void setUp() {
        appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        clientDatabase = Room.inMemoryDatabaseBuilder(appContext, ClientDatabase.class)
                .allowMainThreadQueries()
                .build();

        GlobalParamDao globalParamDao = clientDatabase.globalParamDao();
        globalParamRepository = new GlobalParamRepository(globalParamDao);
    }

    @After
    public void tearDown() {
        clientDatabase.close();
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
        globalParamRepository.saveGlobalParam(RegistrationConstants.MANDATORY_LANGUAGES_KEY, "eng, hin, fra");
        List<String> codes = globalParamRepository.getMandatoryLanguageCodes();
        assertEquals(Arrays.asList("eng", "hin", "fra"), codes);
    }

    @Test
    public void getOptionalLanguageCodesTest() {
        globalParamRepository.saveGlobalParam(RegistrationConstants.OPTIONAL_LANGUAGES_KEY, "kan, tam");
        List<String> codes = globalParamRepository.getOptionalLanguageCodes();
        assertEquals(Arrays.asList("kan", "tam"), codes);
    }

    @Test
    public void getMaxLanguageCountTest() {
        globalParamRepository.saveGlobalParam(RegistrationConstants.MAX_LANGUAGES_COUNT_KEY, "5");
        assertEquals(5, globalParamRepository.getMaxLanguageCount());
    }

    @Test
    public void getMinLanguageCountTest() {
        globalParamRepository.saveGlobalParam(RegistrationConstants.MIN_LANGUAGES_COUNT_KEY, "2");
        assertEquals(2, globalParamRepository.getMinLanguageCount());
    }

    @Test
    public void getSelectedHandlesTest() {
        globalParamRepository.saveGlobalParam(RegistrationConstants.SELECTED_HANDLES, "phone, email");
        List<String> handles = globalParamRepository.getSelectedHandles();
        assertEquals(Arrays.asList("phone", "email"), handles);
    }
}
