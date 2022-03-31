package io.mosip.registration.clientmanager;

import static org.junit.Assert.assertEquals;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.androidnetworking.AndroidNetworking;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.mosip.registration.clientmanager.config.AuthDatabase;
import io.mosip.registration.clientmanager.entity.UserToken;
import io.mosip.registration.clientmanager.util.RestService;

@RunWith(AndroidJUnit4.class)
public class DaoTest {
    private static AuthDatabase authDatabase;
    private static RestService restService;

    @Before
    public void init() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        authDatabase = AuthDatabase.getDatabase(appContext);
        AndroidNetworking.initialize(appContext);
        restService = new RestService();
    }

    @Test
    public void insert_test() {

        // inserting into database
        authDatabase.userTokenDao().deleteAll();
        UserToken usertoken = new UserToken();
        authDatabase.userTokenDao().insertAll(usertoken);
        assertEquals(authDatabase.userTokenDao().loadById("string"),"string");
    }
}
