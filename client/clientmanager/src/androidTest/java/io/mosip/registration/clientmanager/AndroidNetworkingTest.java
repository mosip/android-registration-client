package io.mosip.registration.clientmanager;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.androidnetworking.AndroidNetworking;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

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
    public void fast_android_networking_test(){
        RequestDto requestDto = new RequestDto("http://jsonplaceholder.typicode.com/todos/1",null,null,false,false,false);
        Map<String, Object> response = restService.get(requestDto);
        System.out.println(response.get("String"));
    }
}
