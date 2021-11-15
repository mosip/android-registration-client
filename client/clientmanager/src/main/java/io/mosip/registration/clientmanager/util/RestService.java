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

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class RestService {
    private static final String TAG = RestService.class.getSimpleName();

    // GET request for jsonObject
    public void requestGET(String url) {
        AndroidNetworking.get(url)
                .setTag(this)
                .setPriority(Priority.LOW)
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
    }

//  //   GET request for UserDefined class
//    public void requestClassGET() {
//        AndroidNetworking.get("link")
//                .setTag(this)
//                .setPriority(Priority.LOW) // LOW MEDIUM HIGH IMMEDIATE
//                .build()
//                .getAsObject(myClass.class, new ParsedRequestListener<myClass>() {
//                    @Override
//                    public void onResponse(myClass classObj) {
//                        // do anything with response
//                        Log.d(TAG, "id : " + classObj.details);
//                    }
//
//                    @Override
//                    public void onError(ANError e) {
//                        Log.e(TAG, "onError: Error on UserDefinedClass get request ", e);
//                    }
//                });
//    }
//
//    // GET request for UserDefined class list
//    public void requestClassListGET() {
//        AndroidNetworking.get("link")
//                .setTag(this)
//                .setPriority(Priority.MEDIUM)
//                .build()
//                .getAsObjectList(myClass.class, new ParsedRequestListener<List<myClass>>() {
//                    @Override
//                    public void onResponse(List<myClass> myClassList) {
//                        // do anything with response
//                        Log.d(TAG, "userList size : " + myClassList.size());
//                        for (myClass classObj : myClassList) {
////                            Log.d(TAG, "id : " + classObj.details);
//                        }
//                    }
//                    @Override
//                    public void onError(ANError e) {
//                        Log.e(TAG, "onError: Error on UserDefinedClassList get request ", e);
//                    }
//                });
//    }
//
//    // POST request for UserDefined class
//    public void requestClassPOST() {
//        AndroidNetworking.post("link")
//                .addBodyParameter(myClass) // posting java object
//                .setTag(this)
//                .setPriority(Priority.MEDIUM)
//                .build()
//                .getAsJSONObject(new JSONObjectRequestListener() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        // do anything with response
//                    }
//
//                    @Override
//                    public void onError(ANError e) {
//                        Log.e(TAG, "onError: Error on UserDefinedClass post request ", e);
//                    }
//                });
//    }
//
//    // POST request for Json Object
//    public void requestPOST() {
//        AndroidNetworking.post("link")
//                .addJSONObjectBody(myJsonObject) // posting json
//                .setTag(this)
//                .setPriority(Priority.MEDIUM)
//                .build()
//                .getAsJSONObject(new JSONObjectRequestListener() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        // do anything with response
//                    }
//
//                    @Override
//                    public void onError(ANError e) {
//                        Log.e(TAG, "onError: Error on UserDefinedClass post request ", e);
//                    }
//                });
//    }
//
//    // POST request for any File
//    public void requestFilePOST() {
//        AndroidNetworking.post("link")
//                .addFileBody(myFile) // posting any type of file
//                .setTag(this)
//                .setPriority(Priority.MEDIUM)
//                .build()
//                .getAsJSONObject(new JSONObjectRequestListener() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        // do anything with response
//                    }
//
//                    @Override
//                    public void onError(ANError e) {
//                        Log.e(TAG, "onError: Error on UserDefinedClass post request ", e);
//                    }
//                });
//    }
//
//    // DOWNLOAD request for a File
//    public void requestFileDOWNLOAD() {
//        AndroidNetworking.download(url,dirPath,fileName)
//                .setTag(this)
//                .setPriority(Priority.MEDIUM)
//                .build()
//                .setDownloadProgressListener(new DownloadProgressListener() {
//                    @Override
//                    public void onProgress(long bytesDownloaded, long totalBytes) {
//                        // do anything with progress
//                    }
//                })
//                .startDownload(new DownloadListener() {
//                    @Override
//                    public void onDownloadComplete() {
//                        // do anything after completion
//                    }
//                    @Override
//                    public void onError(ANError e) {
//                        Log.e(TAG, "onError: Error on file download request ", e);
//                    }
//                });
//    }
//
//    // UPLOAD multipart file
//    public void requestFileUPLOAD() {
//        Map<String, File> multiPartFileMap = new HashMap<>();
//        multiPartFileMap.put("image1" , file1);
//        multiPartFileMap.put("image2" , file2);
//        // ... more files
//
//        AndroidNetworking.upload(url)
//                //.addMultipartFileList(mMultiPartFileMap)
//                .addMultipartFile(multiPartFileMap)
//                .addMultipartParameter("key","STRING_VALUE")
//                .setTag(this)
//                .setPriority(Priority.HIGH)
//                .build()
//                .setUploadProgressListener(new UploadProgressListener() {
//                    @Override
//                    public void onProgress(long bytesUploaded, long totalBytes) {
//                        // do anything with progress
//                        // console("progress => " + bytesUploaded);
//                    }
//                })
//                .getAsString(new StringRequestListener() {
//                    @Override
//                    public void onResponse(String response) {
//                        // console("upload => " + response);
//                    }
//
//                    @Override
//                    public void onError(ANError e) {
//                        Log.e(TAG, "onError: Error on multipart file upload request ", e);
//                    }
//                });
//    }

}
