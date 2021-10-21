package io.mosip.registration.app;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import io.mosip.registration.clientmanager.service.packet.PosixAdapter;

public class ObjectStoreDemo extends AppCompatActivity {

    @Inject
    public PosixAdapter posixAdapter;

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


    private static final String TAG = ObjectStoreDemo.class.getSimpleName();

    private static final String PACKET_MANAGER_ACCOUNT = "PACKET_MANAGER_ACCOUNT";
    private static final String source = "reg-client";
    private static final String process = "NEW";
    private static final String id = "110111101120191111121111";
    private static final String objectName = "Test";
    private static final String refId = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_object_store_demo);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void click_addObjectMetaData(View view) {
        test_addObjectMetaData(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void click_getObject(View view) {
        test_getObject(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void click_pack(View view) {
        test_pack(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void test_addObjectMetaData(View view) {
        try {
            Map<String, Object> metaMap = new HashMap<>();
            metaMap.put(ID, id);
            metaMap.put(SOURCE, source);
            metaMap.put(PROCESS, process);

            posixAdapter.addObjectMetaData(PACKET_MANAGER_ACCOUNT,
                    id, source, process, id + objectName, metaMap);
            Snackbar snackbar = Snackbar.make(view, "Object Meta Data added successfully", Snackbar.LENGTH_SHORT);
            snackbar.show();
            return;
        } catch (Exception e) {
            Log.e(TAG, "test_addObjectMetaData failed: ", e);
        }
        Snackbar snackbar = Snackbar.make(view, "Object Meta Data test failed", Snackbar.LENGTH_SHORT);
        snackbar.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void test_getObject(View view) {
        try {
            InputStream inputStream = posixAdapter.getObject(PACKET_MANAGER_ACCOUNT, id, source, process, id + objectName);

            if (inputStream != null) {
                Snackbar snackbar = Snackbar.make(view, "Object fetched successfully", Snackbar.LENGTH_SHORT);
                snackbar.show();
                return;
            }
        } catch (Exception e) {
            Log.e(TAG, "test_getObject failed: ", e);
        }
        Snackbar snackbar = Snackbar.make(view, "Object fetching failed", Snackbar.LENGTH_SHORT);
        snackbar.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void test_pack(View view) {
        try {
            boolean success = posixAdapter.pack(PACKET_MANAGER_ACCOUNT, id, source, process, refId);

            if (success) {
                Snackbar snackbar = Snackbar.make(view, "Packed successfully", Snackbar.LENGTH_SHORT);
                snackbar.show();
                return;
            }
        } catch (Exception e) {
            Log.e(TAG, "test_StoreInZip: StoringInZip Failed ", e);
        }
        Snackbar snackbar = Snackbar.make(view, "Packing failed", Snackbar.LENGTH_SHORT);
        snackbar.show();
    }
}