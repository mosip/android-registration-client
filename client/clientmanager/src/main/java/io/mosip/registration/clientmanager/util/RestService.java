package io.mosip.registration.clientmanager.util;

import android.util.Log;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.DownloadListener;
import com.androidnetworking.interfaces.DownloadProgressListener;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.androidnetworking.interfaces.StringRequestListener;
import com.androidnetworking.interfaces.UploadProgressListener;
import com.androidnetworking.model.MultipartFileBody;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import io.mosip.registration.clientmanager.dto.http.RequestDto;

public class RestService {
    private static final String TAG = RestService.class.getSimpleName();

    // GET request for jsonObject
    public Map<String, Object> get(RequestDto request) {
        AndroidNetworking.get(request.getUrl())
                .setTag(this)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // do anything with response
                        System.out.println(response);
                        Log.i(TAG,"onResponse: Successful get request");

                    }
                    @Override
                    public void onError(ANError e) {
                        // handle error
                        Log.e(TAG, "onError: Error on JSONArray get request ", e);
                    }
                });
        return null;
    }

    // POST request for Json Object
    public Map<String, Object> post(RequestDto request) {
        AndroidNetworking.post(request.getUrl())
                .addJSONObjectBody(request.getBody()) // posting json
                .setTag(this)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // do anything with response
                    }

                    @Override
                    public void onError(ANError e) {
                        Log.e(TAG, "onError: Error on UserDefinedClass post request ", e);
                    }
                });
        return null;
    }

    // UPLOAD multipart file
    public Map<String, Object> fileUpload(RequestDto request, Map<String, File> multiPartFileMap) {
        AndroidNetworking.upload(request.getUrl())
                .addMultipartFile(multiPartFileMap)
                .addMultipartParameter("key","value")
                .setTag(this)
                .setPriority(Priority.MEDIUM)
                .build()
                .setUploadProgressListener(new UploadProgressListener() {
                    @Override
                    public void onProgress(long bytesUploaded, long totalBytes) {
                        // do anything with progress
                        // console("progress => " + bytesUploaded);
                    }
                })
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        // console("upload => " + response);
                    }

                    @Override
                    public void onError(ANError e) {
                        Log.e(TAG, "onError: Error on multipart file upload request ", e);
                    }
                });
        return null;
    }

    // DOWNLOAD request for a File
    public Stream fileDownload(RequestDto request) {
        AndroidNetworking.download(request.getUrl(), request.getBody(), request.getBody())
                .setTag(this)
                .setPriority(Priority.MEDIUM)
                .build()
                .setDownloadProgressListener(new DownloadProgressListener() {
                    @Override
                    public void onProgress(long bytesDownloaded, long totalBytes) {
                        // do anything with progress
                    }
                })
                .startDownload(new DownloadListener() {
                    @Override
                    public void onDownloadComplete() {
                        // do anything after completion
                    }
                    @Override
                    public void onError(ANError e) {
                        Log.e(TAG, "onError: Error on file download request ", e);
                    }
                });
        return null;
    }
}
