package io.mosip.registration.clientmanager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import android.content.Context;
import android.util.Log;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.ANResponse;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Map;

import io.mosip.registration.clientmanager.database.AuthDatabase;
import io.mosip.registration.clientmanager.database.entities.UserToken;
import io.mosip.registration.clientmanager.dto.http.RequestDto;
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

//        trying to get json object from swagger ui to store in database
        JSONObject body = new JSONObject();;
        JSONObject item = new JSONObject();;

        try {
            body.put("id","string");
            body.put("version","string");
            body.put("requesttime","2021-12-05T04:54:39.337Z");
            body.put("metadata",new JSONObject());

            item.put("userName","string");
            item.put("password","string");
            item.put("appId","string");

            body.put("request",item);

            System.out.println(body.toString());
        } catch (JSONException e) {
            Log.e("insert_test","JSON obj creation failed", e);
        }
        RequestDto requestDto = new RequestDto("https://dev.mosip.net/v1/authmanager/authenticate/useridPwd",body,null,false,false,false);

        Map<String, Object> response = restService.post(requestDto);
        assertNotNull(response.get("post"));

        JSONObject url_json = (JSONObject) response.get("post");
        assertNotNull(url_json);

        // inserting into database
        authDatabase.userTokenDao().deleteAll();
        try {
            UserToken usertoken = new UserToken(url_json.getString("id"), url_json.getString("version"),
                    url_json.getString("responsetime"), "1", "response", "1");
            authDatabase.userTokenDao().insertAll(usertoken);
        } catch (JSONException e) {
            Log.e("insert_test","usertoken generation failed", e);
        }
        assertEquals(authDatabase.userTokenDao().loadById("string").getVersion(),"string");
    }
}
