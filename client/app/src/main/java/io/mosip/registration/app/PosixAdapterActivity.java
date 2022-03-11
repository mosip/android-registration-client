package io.mosip.registration.app;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import io.mosip.registration.packetmanager.service.PosixAdapterServiceImpl;
import io.mosip.registration.packetmanager.spi.ObjectAdapterService;

public class PosixAdapterActivity extends AppCompatActivity {

    @Inject
    public ObjectAdapterService objectAdapterService;

    // Packet meta info constants
    private static final String ID = "id";
    private static final String PACKET_NAME = "packetname";
    private static final String SOURCE = "source";
    private static final String PROCESS = "process";
    private static final String SCHEMA_VERSION = "schemaversion";
    private static final String SIGNATURE = "signature";
    private static final String ENCRYPTED_HASH = "encryptedhash";
    private static final String PROVIDER_NAME = "providername";
    private static final String PROVIDER_VERSION = "providerversion";
    private static final String CREATION_DATE = "creationdate";
    private static final String REFID = "refid";
    private static final String PACKET_MANAGER_ACCOUNT = "PACKET_MANAGER_ACCOUNT";
    private static final String source = "reg-client";
    private static final String process = "NEW";
    private static final String id = "110111101120191111121111";
    private static final String objectSuffix = "Test";
    private static final String objectName = id + "_" + objectSuffix;
    private static final String refId = "1234512345_121212";

    private static final String TAG = PosixAdapterActivity.class.getSimpleName();

    TextView objectStoreTextView;
    Snackbar snackbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posix_adapter);
        objectAdapterService = new PosixAdapterServiceImpl(this);
        objectStoreTextView = (TextView) findViewById(R.id.objectStoreTextView);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void click_putObject(View view) {
        String resultMsg = test_putObject();
        snackbar = Snackbar.make(view, resultMsg.substring(0, Math.min(30, resultMsg.length())), Snackbar.LENGTH_SHORT);
        snackbar.show();
        CharSequence text = objectStoreTextView.getText();
        objectStoreTextView.setText(resultMsg + "\n" + text);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void click_addObjectMetaData(View view) {
        String resultMsg = test_addObjectMetaData();
        snackbar = Snackbar.make(view, resultMsg.substring(0, Math.min(30, resultMsg.length())), Snackbar.LENGTH_SHORT);
        snackbar.show();
        CharSequence text = objectStoreTextView.getText();
        objectStoreTextView.setText(resultMsg + "\n" + text);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void click_pack(View view) {
        String resultMsg = test_pack();
        snackbar = Snackbar.make(view, resultMsg.substring(0, Math.min(30, resultMsg.length())), Snackbar.LENGTH_SHORT);
        snackbar.show();
        CharSequence text = objectStoreTextView.getText();
        objectStoreTextView.setText(resultMsg + "\n" + text);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void click_removeContainer(View view) {
        String resultMsg = test_removeContainer();
        snackbar = Snackbar.make(view, resultMsg.substring(0, Math.min(30, resultMsg.length())), Snackbar.LENGTH_SHORT);
        snackbar.show();
        CharSequence text = objectStoreTextView.getText();
        objectStoreTextView.setText(resultMsg + "\n" + text);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public String test_putObject() {
        String resultMsg = "";
        try {
            Map<String, Object> metaMap = new HashMap<>();
            metaMap.put(ID, id);
            metaMap.put(SOURCE, source);
            metaMap.put(PROCESS, process);
            JSONObject jsonObject = new JSONObject(metaMap);
            boolean result = objectAdapterService.putObject(PACKET_MANAGER_ACCOUNT, id, source, process, objectName, new ByteArrayInputStream(jsonObject.toString().getBytes()));
            if (result == true) {
                resultMsg = "Put Object successful";
            } else {
                resultMsg = "Put Object test failed";
            }
        } catch (Exception e) {
            resultMsg = "test_putObject failed : " + e.getStackTrace();
            Log.e(TAG, resultMsg);
        }
        return resultMsg;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public String test_addObjectMetaData() {
        String resultMsg = "";
        try {
            Map<String, Object> metaMap = new HashMap<>();
            metaMap.put(ID, id);
            metaMap.put(SOURCE, source);
            metaMap.put(PROCESS, process);

            Map map = objectAdapterService.addObjectMetaData(PACKET_MANAGER_ACCOUNT,
                    id, source, process, objectName, metaMap);

            if (map != null) {
                resultMsg = "Object Meta Data added successfully : " + map.toString();
            } else {
                resultMsg = "Object Meta Data test failed";
            }

        } catch (Exception e) {
            resultMsg = "test_addObjectMetaData failed : " + e.getStackTrace();
            Log.e(TAG, resultMsg);
        }
        return resultMsg;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public String test_pack() {
        String resultMsg = "";
        try {
            boolean success = objectAdapterService.pack(PACKET_MANAGER_ACCOUNT, id, source, process);

            if (success) {
                resultMsg = "Packed successfully";
            } else {
                resultMsg = "Packing failed";
            }

        } catch (Exception e) {
            resultMsg = "test_pack : Failed " + e.getStackTrace();
            Log.e(TAG, resultMsg);
        }
        return resultMsg;
    }

    private String test_removeContainer() {
        String resultMsg = "";
        try {
            boolean deleted = objectAdapterService.removeContainer(PACKET_MANAGER_ACCOUNT, id, source, process);

            if (deleted) {
                resultMsg = "Container Removed successfully";
            } else {
                resultMsg = "Remove Container failed";
            }
        } catch (Exception e) {
            resultMsg = "test_removeContainer : Failed " + e.getStackTrace();
            Log.e(TAG, resultMsg);
        }
        return resultMsg;
    }
}