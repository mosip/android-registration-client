package io.mosip.registration.clientmanager;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.os.Environment;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.androidnetworking.AndroidNetworking;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import io.mosip.registration.clientmanager.dto.http.RequestDto;
import io.mosip.registration.clientmanager.util.RestService;

@RunWith(AndroidJUnit4.class)
public class AndroidNetworkingTest {
    private static RestService restService;

    @Before
    public void init() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

        AndroidNetworking.initialize(appContext);
        restService = new RestService();
    }

    @Test
    public void get_test(){
        RequestDto requestDto = new RequestDto("https://9b44531f-ce8a-4170-8801-0cce58e7fcea.mock.pstmn.io",null,null,false,false,false);
        Map<String, Object> response = restService.get(requestDto);

        assertNotNull(response.get("get"));
    }

    @Test
    public void post_test(){
        JSONObject body = null;
        try {
            body = new JSONObject(
                    "{" +
                            "Directory: test_android," +
                            "Filename: test-android" +
                            "}"
            );
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestDto requestDto = new RequestDto("https://9b44531f-ce8a-4170-8801-0cce58e7fcea.mock.pstmn.io",body,null,false,false,false);
        Map<String, Object> response = restService.post(requestDto);

        assertNotNull(response.get("post"));
    }

    @Test
    public void upload_test() {
        RequestDto requestDto = new RequestDto("test_url",null,null,false,false,false);
        Map<String, File> multiPartFileMap = new HashMap<String, File> ();
        Map<String, Object> response = restService.fileUpload(requestDto, multiPartFileMap);

        assertNotNull(response.get("upload"));
    }

    @Test
    public void download_test() {

        String directory = "dummy";
        String filename = "dummy";

        JSONObject body = null;
        try {
            body = new JSONObject(
                    "{" +
                            "Directory: " + directory + "," +
                            "Filename: " + filename +
                            "}"
            );
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestDto requestDto = new RequestDto("test_url",body,null,false,false,false);
        boolean response = restService.fileDownload(requestDto);
        assertTrue(response);
    }


}
