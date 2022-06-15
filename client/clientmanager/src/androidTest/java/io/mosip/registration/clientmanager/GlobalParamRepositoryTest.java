package io.mosip.registration.clientmanager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import io.mosip.registration.clientmanager.config.ClientDatabase;
import io.mosip.registration.clientmanager.dao.GlobalParamDao;
import io.mosip.registration.clientmanager.entity.GlobalParam;
import io.mosip.registration.clientmanager.repository.GlobalParamRepository;

/**
 * @author Anshul vanawat
 * @since 02/06/2022.
 */

@RunWith(AndroidJUnit4.class)
public class GlobalParamRepositoryTest {

    private static final String GLOBAL_PARAM_STRING_ID = "mosip.lang-code";
    private static final String GLOBAL_PARAM_STRING_VALUE = "eng";

    private static final String GLOBAL_PARAM_BOOLEAN_ID = "mosip.isSyncJobActive";
    private static final Boolean GLOBAL_PARAM_BOOLEAN_VALUE = true;

    private static final String GLOBAL_PARAM_INT_ID = "mosip.syncJobId";
    private static final int GLOBAL_PARAM_INT_VALUE = 1;

    Context appContext;
    ClientDatabase clientDatabase;

    GlobalParamRepository globalParamRepository;

    @Before
    public void setUp() {
        appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        clientDatabase = ClientDatabase.getDatabase(appContext);
        clientDatabase.clearAllTables();

        GlobalParamDao globalParamDao = clientDatabase.globalParamDao();
        globalParamRepository = new GlobalParamRepository(globalParamDao);
    }

    @After
    public void tearDown() {
        clientDatabase.clearAllTables();
    }

    @Test
    public void saveGlobalParamTest() {
        globalParamRepository.saveGlobalParam(GLOBAL_PARAM_STRING_ID, GLOBAL_PARAM_STRING_VALUE);

        String globalParamValue = globalParamRepository.getGlobalParamValue(GLOBAL_PARAM_STRING_ID);
        assertEquals(GLOBAL_PARAM_STRING_VALUE, globalParamValue);

        String globalParamCachedValue = GlobalParamRepository.getCachedStringGlobalParam(GLOBAL_PARAM_STRING_ID);
        assertEquals(GLOBAL_PARAM_STRING_VALUE, globalParamCachedValue);
    }

    @Test
    public void saveGlobalParamListTest() {

        List<GlobalParam> globalParamList = new ArrayList<>();
        globalParamList.add(new GlobalParam(GLOBAL_PARAM_BOOLEAN_ID
                , GLOBAL_PARAM_BOOLEAN_ID
                , GLOBAL_PARAM_BOOLEAN_VALUE.toString()
                , true));
        globalParamList.add(new GlobalParam(GLOBAL_PARAM_STRING_ID
                , GLOBAL_PARAM_STRING_ID
                , GLOBAL_PARAM_STRING_VALUE
                , true));
        globalParamList.add(new GlobalParam(GLOBAL_PARAM_INT_ID
                , GLOBAL_PARAM_INT_ID
                , String.valueOf(GLOBAL_PARAM_INT_VALUE)
                , true));

        globalParamRepository.saveGlobalParams(globalParamList);

        List<GlobalParam> globalParams = globalParamRepository.getGlobalParams();
        assertEquals(3, globalParams.size());
    }

    @Test
    public void getCachedValuesTest() {

        List<GlobalParam> globalParamList = new ArrayList<>();
        globalParamList.add(new GlobalParam(GLOBAL_PARAM_BOOLEAN_ID
                , GLOBAL_PARAM_BOOLEAN_ID
                , GLOBAL_PARAM_BOOLEAN_VALUE.toString()
                , true));
        globalParamList.add(new GlobalParam(GLOBAL_PARAM_STRING_ID
                , GLOBAL_PARAM_STRING_ID
                , GLOBAL_PARAM_STRING_VALUE
                , true));
        globalParamList.add(new GlobalParam(GLOBAL_PARAM_INT_ID
                , GLOBAL_PARAM_INT_ID
                , String.valueOf(GLOBAL_PARAM_INT_VALUE)
                , true));

        globalParamRepository.saveGlobalParams(globalParamList);

        String globalParamCachedStringValue = GlobalParamRepository.getCachedStringGlobalParam(GLOBAL_PARAM_STRING_ID);
        assertEquals(GLOBAL_PARAM_STRING_VALUE, globalParamCachedStringValue);

        Boolean globalParamCachedBoolValue = GlobalParamRepository.getCachedBooleanGlobalParam(GLOBAL_PARAM_BOOLEAN_ID);
        assertEquals(GLOBAL_PARAM_BOOLEAN_VALUE, globalParamCachedBoolValue);

        int globalParamCachedIntValue = GlobalParamRepository.getCachedIntegerGlobalParam(GLOBAL_PARAM_INT_ID);
        assertEquals(GLOBAL_PARAM_INT_VALUE, globalParamCachedIntValue);
    }

    @Test
    public void getCachedValuesNotFound() {
        String globalParamCachedStringValue = GlobalParamRepository.getCachedStringGlobalParam(GLOBAL_PARAM_STRING_ID);
        assertNull(globalParamCachedStringValue);

        Boolean globalParamCachedBoolValue = GlobalParamRepository.getCachedBooleanGlobalParam(GLOBAL_PARAM_BOOLEAN_ID);
        assertNull(globalParamCachedBoolValue);

        int globalParamCachedIntValue = GlobalParamRepository.getCachedIntegerGlobalParam(GLOBAL_PARAM_INT_ID);
        assertEquals(0, globalParamCachedIntValue);
    }
}