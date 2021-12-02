package io.mosip.registration.clientmanager.util;

import android.util.Log;

import androidx.annotation.NonNull;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.ANRequest;
import com.androidnetworking.common.ANResponse;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.DownloadListener;
import com.androidnetworking.interfaces.DownloadProgressListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.androidnetworking.interfaces.StringRequestListener;
import com.androidnetworking.interfaces.UploadProgressListener;

import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import io.mosip.registration.clientmanager.dto.http.RequestDto;
import okhttp3.Request;
import okhttp3.Response;

public class RestService {
    private static final String TAG = RestService.class.getSimpleName();



    // GET request for jsonObject
    public Map<String, Object> get(@NonNull RequestDto requestDto) {
        final Map<String, Object> responseObj = new HashMap<String, Object>();
        ANRequest request = AndroidNetworking.get(requestDto.getUrl())
                .setTag(this)
                .setPriority(Priority.MEDIUM)
                .build();

        ANResponse<JSONObject> response = request.executeForJSONObject();

        if (response.isSuccess()) {
            JSONObject jsonObject = response.getResult();
            Log.i(TAG, "response : " + jsonObject.toString());
            responseObj.put("get",jsonObject);
            Response okHttpResponse = response.getOkHttpResponse();
            Log.i(TAG, "headers : " + okHttpResponse.headers().toString());

        } else {
            ANError error = response.getError();
            Log.e(TAG, "onError: Error on get request ", error);
        }
        System.out.println("Response at end = " + responseObj.get("get"));
        return responseObj;
    }

    // POST request for Json Object
    public Map<String, Object> post(@NonNull RequestDto requestDto) {
        final Map<String, Object> responseObj = new HashMap<String, Object>();
        ANRequest request = AndroidNetworking.post(requestDto.getUrl())
                .addJSONObjectBody(requestDto.getBody())
                .setTag(this)
                .setPriority(Priority.MEDIUM)
                .build();

        ANResponse<JSONObject> response = request.executeForJSONObject();

        if (response.isSuccess()) {
            JSONObject jsonObject = response.getResult();
            Log.i(TAG, "response : " + jsonObject.toString());
            responseObj.put("post", response);
            Response okHttpResponse = response.getOkHttpResponse();
            Log.i(TAG, "headers : " + okHttpResponse.headers().toString());
        } else {
            ANError error = response.getError();
            Log.e(TAG, "onError: Error on post request ", error);
        }
        return responseObj;
    }

    // UPLOAD multipart file
    public Map<String, Object> fileUpload(@NonNull RequestDto requestDto,@NonNull Map<String, File> multiPartFileMap) {
        final Map<String, Object> responseObj = new HashMap<String, Object>();
        ANRequest.MultiPartBuilder requestBuilder = AndroidNetworking.upload(requestDto.getUrl())
                .setTag(this)
                .setPriority(Priority.MEDIUM);

        for(Map.Entry<String,File> entry : multiPartFileMap.entrySet()) {
            requestBuilder.addMultipartFile(entry.getKey(),entry.getValue());
        }
        ANRequest request = requestBuilder.build();

        request.setUploadProgressListener(new UploadProgressListener() {
            @Override
            public void onProgress(long bytesUploaded, long totalBytes) {
                // do anything with progress
            }
        });

        ANResponse<JSONObject> response = request.executeForJSONObject();

        if (response.isSuccess()) {
            JSONObject jsonObject = response.getResult();
            Log.d(TAG, "response : " + jsonObject.toString());
            responseObj.put("upload", response);
            Response okHttpResponse = response.getOkHttpResponse();
            Log.d(TAG, "headers : " + okHttpResponse.headers().toString());
        } else {
            ANError error = response.getError();
            Log.e(TAG, "onError: Error on fileUpload request ", error);
        }
        return responseObj;
    }

    //     DOWNLOAD request for a File
    public void fileDownload(@NonNull RequestDto requestDto, String directory, String filename) {
        ANRequest request = AndroidNetworking
                .download(requestDto.getUrl(), directory, filename)
                .setTag(this)
                .setPriority(Priority.MEDIUM)
                .build()
                .setDownloadProgressListener(new DownloadProgressListener() {
                    @Override
                    public void onProgress(long bytesDownloaded, long totalBytes) {

                    }
                });

        ANResponse<String> response = request.executeForDownload();

        if (response.isSuccess()) {
            Response okHttpResponse = response.getOkHttpResponse();
            Log.d(TAG, "headers : " + okHttpResponse.headers().toString());
        } else {
            ANError error = response.getError();
            Log.e(TAG, "onError: Error on fileDownload request ", error);
        }
    }
}
