package io.mosip.registration.clientmanager.repository;

import static org.junit.Assert.assertEquals;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import io.mosip.registration.clientmanager.config.ClientDatabase;
import io.mosip.registration.clientmanager.entity.UserToken;

@RunWith(RobolectricTestRunner.class)
public class DaoTest {
    private static ClientDatabase clientDatabase;

    @Before
    public void init() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        clientDatabase = ClientDatabase.getDatabase(appContext);
    }

    @Test
    public void insert_test() {
        // inserting into database
        clientDatabase.userTokenDao().deleteAll();
        UserToken usertoken = new UserToken("test", "test", "test", 0L, 0L);
        clientDatabase.userTokenDao().insert(usertoken);
        assertEquals("test", clientDatabase.userTokenDao().findByUsername("test").getToken());
    }
}
