package io.mosip.registration.clientmanager;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.util.Log;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.androidnetworking.AndroidNetworking;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Map;

import io.mosip.registration.clientmanager.dto.http.RequestDto;

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
        // postman test
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
            Log.e("post_test", "JSON obj creation failed", e);
        }
        // postman test
        RequestDto requestDto = new RequestDto("https://9b44531f-ce8a-4170-8801-0cce58e7fcea.mock.pstmn.io",body,null,false,false,false);
        Map<String, Object> response = restService.post(requestDto);

        assertNotNull(response.get("post"));
    }

    @Test
    public void mosip_post_test(){
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
            Log.e("mosip_post_test", "JSON obj creation failed", e);
        }
        RequestDto requestDto = new RequestDto("https://dev.mosip.net/v1/authmanager/authenticate/useridPwd",body,null,false,false,false);
        Map<String, Object> response = restService.post(requestDto);

        assertNotNull(response.get("post"));
    }

    @Test
    public void upload_test() {
        JSONObject body = new JSONObject();
        try {
            body.put("file1", "path1");
            body.put("file2", "path2");
//            more file paths
        } catch (JSONException e) {
            Log.e("upload_test", "JSON obj creation failed", e);

        }

        RequestDto requestDto = new RequestDto("test_url",body,null,false,false,false);
        Map<String, Object> response = restService.fileUpload(requestDto);

        assertNotNull(response.get("upload"));
    }

    @Test
    public void download_test() {

        String directory = "dummy";
        String filename = "dummy";

        JSONObject body = new JSONObject();
        try {
            body.put("Directory", directory);
            body.put("Filename", filename);
        } catch (JSONException e) {
            Log.e("download_test", "JSON obj creation failed", e);

        }
        RequestDto requestDto = new RequestDto("test_url",body,null,false,false,false);
        boolean response = restService.fileDownload(requestDto);
        assertTrue(response);
    }


}
