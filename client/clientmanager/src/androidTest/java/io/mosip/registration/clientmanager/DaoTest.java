package io.mosip.registration.clientmanager;

import static org.junit.Assert.assertEquals;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.mosip.registration.clientmanager.config.ClientDatabase;
import io.mosip.registration.clientmanager.entity.UserToken;

@RunWith(AndroidJUnit4.class)
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
        assertEquals("string", clientDatabase.userTokenDao().findByUsername("test"));
    }
}
