package io.mosip.registration.clientmanager.repository;

import android.content.Context;

import androidx.room.Room;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import io.mosip.registration.clientmanager.config.ClientDatabase;
import io.mosip.registration.clientmanager.dao.GlobalParamDao;
import io.mosip.registration.clientmanager.dao.IdentitySchemaDao;
import io.mosip.registration.clientmanager.dao.ProcessSpecDao;
import io.mosip.registration.clientmanager.dao.TemplateDao;
import io.mosip.registration.clientmanager.dto.http.IdSchemaResponse;
import io.mosip.registration.clientmanager.dto.uispec.FieldSpecDto;
import io.mosip.registration.clientmanager.dto.uispec.ProcessSpecDto;
import io.mosip.registration.clientmanager.dto.uispec.ScreenSpecDto;

@RunWith(RobolectricTestRunner.class)
public class IdentitySchemaRepositoryTest {

    private Context appContext;
    private ClientDatabase clientDatabase;
    private IdentitySchemaRepository identitySchemaRepository;
    private IdSchemaResponse idSchemaResponse;

    @Before
    public void setUp() {
        appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        clientDatabase = Room.inMemoryDatabaseBuilder(appContext, ClientDatabase.class)
                .allowMainThreadQueries()
                .build();

        IdentitySchemaDao identitySchemaDao = clientDatabase.identitySchemaDao();
        TemplateDao templateDao = clientDatabase.templateDao();
        TemplateRepository templateRepository = new TemplateRepository(templateDao);
        GlobalParamDao globalParamDao = clientDatabase.globalParamDao();
        GlobalParamRepository globalParamRepository = new GlobalParamRepository(globalParamDao);
        ProcessSpecDao processSpecDao = clientDatabase.processSpecDao();
        identitySchemaRepository = new IdentitySchemaRepository(templateRepository, globalParamRepository, identitySchemaDao, processSpecDao);

        ProcessSpecDto processSpecDto = new ProcessSpecDto();
        ScreenSpecDto screen = new ScreenSpecDto();
        FieldSpecDto field = new FieldSpecDto();
        field.setId("residenceStatus");
        screen.setFields(Arrays.asList(field));
        processSpecDto.setScreens(Arrays.asList(screen));

        idSchemaResponse = new IdSchemaResponse();
        idSchemaResponse.setId("1");
        idSchemaResponse.setIdVersion(1.5);
        idSchemaResponse.setNewProcess(processSpecDto);
    }

    @After
    public void tearDown() {
        clientDatabase.close();
    }

    @Test
    public void testSaveAndRetrieveFieldSpec() throws Exception {
        identitySchemaRepository.saveIdentitySchema(appContext, idSchemaResponse);
        List<FieldSpecDto> fields = identitySchemaRepository.getAllFieldSpec(appContext, 1.5);
        Assert.assertNotNull(fields);
        Assert.assertFalse(fields.isEmpty());
        Assert.assertEquals("residenceStatus", fields.get(0).getId());
    }

    @Test
    public void testGetLatestSchemaVersion() throws Exception {
        identitySchemaRepository.saveIdentitySchema(appContext, idSchemaResponse);
        Double version = identitySchemaRepository.getLatestSchemaVersion();
        Assert.assertNotNull(version);
        Assert.assertEquals(1.5, version, 0.01);
    }

    @Test
    public void testSaveMultipleSchemasAndRetrieveLatestVersion() throws Exception {
        IdSchemaResponse anotherSchema = new IdSchemaResponse();
        anotherSchema.setId("1");
        anotherSchema.setIdVersion(2.0);
        anotherSchema.setNewProcess(new ProcessSpecDto());

        identitySchemaRepository.saveIdentitySchema(appContext, idSchemaResponse);
        identitySchemaRepository.saveIdentitySchema(appContext, anotherSchema);

        Double version = identitySchemaRepository.getLatestSchemaVersion();
        Assert.assertNotNull(version);
        Assert.assertEquals(2.0, version, 0.01);
    }


    @Test
    public void testSaveIdentitySchemaWithNullData() {
        try {
            identitySchemaRepository.saveIdentitySchema(appContext, null);
            Assert.fail("Expected an exception to be thrown");
        } catch (Exception e) {
            Assert.assertTrue(e instanceof NullPointerException || e instanceof IllegalArgumentException);
        }
    }

    @Test
    public void testGetLatestSchemaVersionWhenNoSchemaExists() {
        Double version = identitySchemaRepository.getLatestSchemaVersion();
        Assert.assertNull(version);
    }

    @Test
    public void testSaveAndRetrieveFieldSpecWithDifferentVersions() throws Exception {
        IdSchemaResponse anotherSchema = new IdSchemaResponse();
        anotherSchema.setId("2");
        anotherSchema.setIdVersion(2.0);
        ProcessSpecDto processSpecDto = new ProcessSpecDto();
        ScreenSpecDto screen = new ScreenSpecDto();
        FieldSpecDto field = new FieldSpecDto();
        field.setId("maritalStatus");
        screen.setFields(Collections.singletonList(field));
        processSpecDto.setScreens(Collections.singletonList(screen));
        anotherSchema.setNewProcess(processSpecDto);

        identitySchemaRepository.saveIdentitySchema(appContext, idSchemaResponse);
        identitySchemaRepository.saveIdentitySchema(appContext, anotherSchema);

        List<FieldSpecDto> fieldsV1 = identitySchemaRepository.getAllFieldSpec(appContext, 1.5);
        List<FieldSpecDto> fieldsV2 = identitySchemaRepository.getAllFieldSpec(appContext, 2.0);

        Assert.assertNotNull(fieldsV1);
        Assert.assertFalse(fieldsV1.isEmpty());
        Assert.assertEquals("residenceStatus", fieldsV1.get(0).getId());

        Assert.assertNotNull(fieldsV2);
        Assert.assertFalse(fieldsV2.isEmpty());
        Assert.assertEquals("maritalStatus", fieldsV2.get(0).getId());
    }

    @Test
    public void testGetNewProcessSpecWithNoSchema() {
        try {
            identitySchemaRepository.getNewProcessSpec(appContext,1.5);
            Assert.fail("Expected an exception to be thrown");
        } catch (Exception e) {
            Assert.assertEquals("Identity schema not found for version : 1.5", e.getMessage());
        }
    }
}
