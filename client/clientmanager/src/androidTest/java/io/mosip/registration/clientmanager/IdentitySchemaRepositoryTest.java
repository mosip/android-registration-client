package io.mosip.registration.clientmanager;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.mosip.registration.clientmanager.config.ClientDatabase;
import io.mosip.registration.clientmanager.dao.IdentitySchemaDao;
import io.mosip.registration.clientmanager.dto.http.IdSchemaResponse;
import io.mosip.registration.clientmanager.dto.uispec.FieldSpecDto;
import io.mosip.registration.clientmanager.dto.uispec.ProcessSpecDto;
import io.mosip.registration.clientmanager.dto.uispec.ScreenSpecDto;
import io.mosip.registration.clientmanager.repository.IdentitySchemaRepository;

@RunWith(AndroidJUnit4.class)
public class IdentitySchemaRepositoryTest {

    Context appContext;
    ClientDatabase clientDatabase;

    IdentitySchemaRepository identitySchemaRepository;

    IdSchemaResponse idSchemaResponse;

    @Before
    public void setUp() {
        appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        clientDatabase = ClientDatabase.getDatabase(appContext);
        clientDatabase.clearAllTables();

        IdentitySchemaDao identitySchemaDao = clientDatabase.identitySchemaDao();
        identitySchemaRepository = new IdentitySchemaRepository(identitySchemaDao);

        ProcessSpecDto processSpecDto = new ProcessSpecDto();
        ScreenSpecDto screen = new ScreenSpecDto();
        FieldSpecDto field = new FieldSpecDto();
        screen.setFields(Arrays.asList(field));
        processSpecDto.setScreens(Arrays.asList(screen));

        idSchemaResponse = new IdSchemaResponse();
        idSchemaResponse.setId("Test101");
        idSchemaResponse.setIdVersion(1.5);
        idSchemaResponse.setNewProcess(processSpecDto);
    }

    @After
    public void tearDown() {
        clientDatabase.clearAllTables();
    }

    @Test
    public void getAllFieldSpecTest() throws Exception {
        identitySchemaRepository.saveIdentitySchema(appContext, idSchemaResponse);
        List<FieldSpecDto> fields = identitySchemaRepository.getAllFieldSpec(appContext, 1.5);
        Assert.assertNotNull(fields);
    }

    @Test
    public void getLatestSchemaVersionTest() throws Exception {
        identitySchemaRepository.saveIdentitySchema(appContext, idSchemaResponse);
        Assert.assertEquals(identitySchemaRepository.getLatestSchemaVersion(), 1.5, 1.5);
    }

}
