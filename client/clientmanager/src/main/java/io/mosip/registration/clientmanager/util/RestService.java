package io.mosip.registration.clientmanager.util;

import android.util.Log;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.DownloadListener;
import com.androidnetworking.interfaces.DownloadProgressListener;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONArray;
import org.json.JSONObject;

public class RestService {
    private static final String TAG = RestService.class.getSimpleName();

    // GET request for jsonObjectArray
    public void requestGET() {
        AndroidNetworking.get("https://jsonplaceholder.typicode.com/todos/1")
//                .addPathParameter("id", "1")
                .addQueryParameter("limit", "3")
                .setPriority(Priority.LOW)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        // do anything with response
                        System.out.println(response);
                    }
                    @Override
                    public void onError(ANError e) {
                        // handle error
                        Log.e(TAG, "onError: Error on JSOMArray get request ", e);
                    }
                });
    }

    // GET request for UserDefined class
//    public void requestClassGET() {
//        AndroidNetworking.get("link")
//                .setTag(this)
//                .setPriority(Priority.LOW)
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
//                .setPriority(Priority.LOW)
//                .build()
//                .getAsObjectList(myClass.class, new ParsedRequestListener<List<myClass>>() {
//                    @Override
//                    public void onResponse(List<myClass> myClassList) {
//                        // do anything with response
//                        Log.d(TAG, "userList size : " + myClassList.size());
//                        for (myClass classObj : myClassList) {
//                            Log.d(TAG, "id : " + classObj.details);
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
//                .setTag("test")
//                .setPriority(Priority.MEDIUM)
//                .build()
//                .getAsJSONArray(new JSONArrayRequestListener() {
//                    @Override
//                    public void onResponse(JSONArray response) {
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
//                .setTag("test")
//                .setPriority(Priority.MEDIUM)
//                .build()
//                .getAsJSONArray(new JSONArrayRequestListener() {
//                    @Override
//                    public void onResponse(JSONArray response) {
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
//    public void requestPOST() {
//        AndroidNetworking.post("link")
//                .addFileBody(myFile) // posting any type of file
//                .setTag("test")
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
//    public void requestDOWNLOAD() {
//        AndroidNetworking.download(url,dirPath,fileName)
//                .setTag("downloadTest")
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


}
