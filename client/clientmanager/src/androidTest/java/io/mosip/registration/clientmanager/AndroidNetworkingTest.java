package io.mosip.registration.clientmanager;

import static org.junit.Assert.assertNotNull;

import android.content.Context;
import android.os.Environment;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.androidnetworking.AndroidNetworking;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
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
        RequestDto requestDto = new RequestDto("http://jsonplaceholder.typicode.com/todos/1",null,null,false,false,false);
        Map<String, Object> response = restService.get(requestDto);

        assertNotNull(response.get("get"));
    }

    @Test
    public void download_test() {

        String directory = "/storage/emulated/0/Download";
        System.out.println("download_directory = " + directory);
        String filename = "dummy";
        RequestDto requestDto = new RequestDto("http://speedtest.ftp.otenet.gr/files/test100k.db",null,null,false,false,false);
        restService.fileDownload(requestDto,directory,filename);
    }
}
