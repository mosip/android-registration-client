package io.mosip.registration.clientmanager.util;

import android.util.Log;

import androidx.annotation.NonNull;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.ANRequest;
import com.androidnetworking.common.ANResponse;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.DownloadProgressListener;
import com.androidnetworking.interfaces.UploadProgressListener;

import io.mosip.registration.clientmanager.exception.RestServiceException;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import io.mosip.registration.clientmanager.dto.http.RequestDto;
import okhttp3.Response;

public class RestService {

    /*private static final String TAG = RestService.class.getSimpleName();

    // GET request for jsonObject
    public static Map<String, Object> get(@NonNull RequestDto requestDto) {
        final Map<String, Object> responseObj = new HashMap<String, Object>();
        try {
            ANRequest request = AndroidNetworking.get(requestDto.getUrl())
                    .setTag(RestService.class)
                    .setPriority(Priority.MEDIUM)
                    .build();

            ANResponse<JSONObject> response = request.executeForJSONObject();

            if (response.isSuccess()) {
                JSONObject jsonObject = response.getResult();
                Log.i(TAG, "response : " + jsonObject.toString());
                responseObj.put("get", jsonObject);
                Response okHttpResponse = response.getOkHttpResponse();
                Log.i(TAG, "headers : " + okHttpResponse.headers().toString());

            } else {
                ANError error = response.getError();
                Log.e(TAG, "onError: Error on get request ", error);
            }
        } catch (Exception e) {
            Log.e(TAG, "get: get request failed");
        }
        return responseObj;
    }

    // POST request for Json Object
    public static Map<String, Object> post(@NonNull RequestDto requestDto) {
        try {
            ANRequest request = AndroidNetworking.post(requestDto.getUrl())
                    .addJSONObjectBody(requestDto.getBody())
                    .setTag(RestService.class)
                    .setPriority(Priority.MEDIUM)
                    .build();

            ANResponse<JSONObject> response = request.executeForJSONObject();

            if (response.isSuccess()) {
                JSONObject jsonObject = response.getResult();
                final Map<String, Object> responseObj = new HashMap<String, Object>();
                responseObj.put(RESPONSE_BODY, jsonObject);
                return responseObj;
            }

            ANError error = response.getError();
            Log.e(TAG, "ANError: Error on post request >> "+ requestDto.getUrl(), error);

        } catch (Exception e) {
            Log.e(TAG, "Post request failed >> "+ requestDto.getUrl(), e);
        }
        return null;
    }

    // UPLOAD multipart file
    public static Map<String, Object> fileUpload(@NonNull RequestDto requestDto) {
        final Map<String, Object> responseObj = new HashMap<String, Object>();
        try {

            ANRequest.MultiPartBuilder requestBuilder = AndroidNetworking.upload(requestDto.getUrl())
                    .setTag(RestService.class)
                    .setPriority(Priority.MEDIUM);

            // key corresponds to file identifier and value corresponds to path string of file
            JSONObject files = requestDto.getBody();
            JSONArray keys = files.names();
            for (int i = 0; i < keys.length(); i++) {
                String key = keys.getString(i);
                File file = new File(files.getString (key));

                requestBuilder.addMultipartFile(key, file);
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
                responseObj.put("upload", jsonObject);
                Response okHttpResponse = response.getOkHttpResponse();
                Log.d(TAG, "headers : " + okHttpResponse.headers().toString());
            } else {
                ANError error = response.getError();
                Log.e(TAG, "onError: Error on fileUpload request ", error);
            }
        } catch (Exception e) {
            Log.e(TAG, "fileUpload: upload request failed");
        }

        return responseObj;
    }

    // DOWNLOAD request for a File
    public static boolean fileDownload(@NonNull RequestDto requestDto) {
        try {
            // Directory: file download directory, Filename: filename to save to
            ANRequest request = AndroidNetworking
                    .download(requestDto.getUrl(), requestDto.getBody().getString("Directory"),
                            requestDto.getBody().getString("Filename"))
                    .setTag(RestService.class)
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .setDownloadProgressListener(new DownloadProgressListener() {
                        @Override
                        public void onProgress(long bytesDownloaded, long totalBytes) {
                            // do anything with progress
                        }
                    });
            ANResponse<String> response = request.executeForDownload();

            if (response.isSuccess()) {
                Response okHttpResponse = response.getOkHttpResponse();
                Log.d(TAG, "headers : " + okHttpResponse.headers().toString());
                return true;
            } else {
                ANError error = response.getError();
                Log.e(TAG, "onError: Error on fileDownload request ", error);
                return false;
            }
        }
        catch (Exception e) {
            Log.e(TAG, "fileDownload: download request failed");
        }
        return false;
    }

    public static Object getResponseObject(Map<String, Object> response) throws RestServiceException {
        try {
            if(response != null && response.get(RestService.RESPONSE_BODY) != null) {
                JSONObject jsonObject = (JSONObject) response.get(RestService.RESPONSE_BODY);
                if(jsonObject.get("errors") != null) {
                    JSONArray errors = jsonObject.getJSONArray("errors");
                    Log.i(TAG, "errors from server >>> " + errors);
                    JSONObject error = errors.getJSONObject(0);
                    throw new RestServiceException(error.getString("errorCode"),
                            error.getString("message"));
                }

                if(jsonObject.get("response") == null)
                    throw new RestServiceException("MOS-REG-999", "Empty response from server");

                return jsonObject.get("response");
            }
        } catch (Exception exception) {
            Log.e(TAG, "Failed to parse response", exception);
        }
        throw new RestServiceException("MOS-REG-999", "Failed to parse response");
    }*/

}
