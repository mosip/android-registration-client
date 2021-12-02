package io.mosip.registration.clientmanager;

import static org.junit.Assert.assertEquals;

import android.content.Context;
import android.util.Log;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.androidnetworking.AndroidNetworking;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.mosip.registration.clientmanager.database.AuthDatabase;
import io.mosip.registration.clientmanager.database.entities.UserToken;
import io.mosip.registration.clientmanager.util.RestService;

@RunWith(AndroidJUnit4.class)
public class DaoTest {
    private static AuthDatabase authDatabase;

    @Before
    public void init() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

       authDatabase = AuthDatabase.getDatabase(appContext);
    }

    @Test
    public void insert_test() {
        authDatabase.userTokenDao().deleteAll();
        UserToken usertoken=new UserToken("1","!","1","1","response","1");
        authDatabase.userTokenDao().insertAll(usertoken);
        assertEquals(authDatabase.userTokenDao().loadById("1").getResponse(),"response");
    }
}
