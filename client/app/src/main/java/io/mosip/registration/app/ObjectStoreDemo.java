package io.mosip.registration.app;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

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
    private static final String objectSuffix = "Test";
    private static final String objectName = id + "_" + objectSuffix;
    private static final String refId = "1234512345_121212";

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
    public void click_getMetaData(View view) {
        test_getMetaData(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void click_removeContainer(View view) {
        test_removeContainer(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void click_addTags(View view) {
        test_addTags(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void click_getTags(View view) {
        test_getTags(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void test_addObjectMetaData(View view) {
        try {
            Map<String, Object> metaMap = new HashMap<>();
            metaMap.put(ID, id);
            metaMap.put(SOURCE, source);
            metaMap.put(PROCESS, process);

            Map map = posixAdapter.addObjectMetaData(PACKET_MANAGER_ACCOUNT,
                    id, source, process, objectName, metaMap);
            if (map != null) {
                Snackbar snackbar = Snackbar.make(view, "Object Meta Data added successfully : " + map.toString(), Snackbar.LENGTH_SHORT);
                snackbar.show();
                return;
            }
        } catch (Exception e) {
            Log.e(TAG, "test_addObjectMetaData failed : ", e);
        }
        Snackbar snackbar = Snackbar.make(view, "Object Meta Data test failed", Snackbar.LENGTH_SHORT);
        snackbar.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void test_getObject(View view) {
        try {
            InputStream inputStream = null; //posixAdapter.getObject(PACKET_MANAGER_ACCOUNT, id, source, process, objectName);

            if (inputStream != null) {
                Snackbar snackbar = Snackbar.make(view, "Object fetched successfully : " + inputStream.toString(), Snackbar.LENGTH_SHORT);
                snackbar.show();
                return;
            }
        } catch (Exception e) {
            Log.e(TAG, "test_getObject failed : ", e);
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
            Log.e(TAG, "test_pack : Failed ", e);
        }
        Snackbar snackbar = Snackbar.make(view, "Packing failed", Snackbar.LENGTH_SHORT);
        snackbar.show();
    }

    private void test_getMetaData(View view) {

        try {
            Map metaData = null; //posixAdapter.getMetaData(PACKET_MANAGER_ACCOUNT, id, source, process, objectName);

            if (metaData != null && metaData.containsKey(ID)) {
                Snackbar snackbar = Snackbar.make(view, "Meta data fetched successfully", Snackbar.LENGTH_SHORT);
                snackbar.show();
                return;
            }
        } catch (Exception e) {
            Log.e(TAG, "test_getMetaData : Failed ", e);
        }
        Snackbar snackbar = Snackbar.make(view, "Meta data fetching failed", Snackbar.LENGTH_SHORT);
        snackbar.show();
    }

    private void test_removeContainer(View view) {

        try {
            boolean deleted = posixAdapter.removeContainer(PACKET_MANAGER_ACCOUNT, id, source, process);

            if (deleted) {
                Snackbar snackbar = Snackbar.make(view, "Container Removed successfully", Snackbar.LENGTH_SHORT);
                snackbar.show();
                return;
            }
        } catch (Exception e) {
            Log.e(TAG, "test_removeContainer : Failed ", e);
        }
        Snackbar snackbar = Snackbar.make(view, "Remove Container failed", Snackbar.LENGTH_SHORT);
        snackbar.show();
    }

    private void test_addTags(View view) {

        try {
            Map<String, String> tags = new HashMap<>();
            tags.put(ID, id);
            tags.put(SOURCE, source);
            tags.put(PROCESS, process);

            Map _tags = null; //posixAdapter.addTags(PACKET_MANAGER_ACCOUNT, id, tags);

            if (_tags != null) {
                Snackbar snackbar = Snackbar.make(view, "Tags Added successfully", Snackbar.LENGTH_SHORT);
                snackbar.show();
                return;
            }
        } catch (Exception e) {
            Log.e(TAG, "test_addTags : Failed ", e);
        }
        Snackbar snackbar = Snackbar.make(view, "Add Tags failed", Snackbar.LENGTH_SHORT);
        snackbar.show();
    }

    private void test_getTags(View view) {

        try {
            Map tags = null; //posixAdapter.getTags(PACKET_MANAGER_ACCOUNT, id);

            if (tags != null) {
                Snackbar snackbar = Snackbar.make(view, "Tags fetched successfully : " + tags.toString() , Snackbar.LENGTH_SHORT);
                snackbar.show();
                return;
            }
        } catch (Exception e) {
            Log.e(TAG, "test_getTags : Failed ", e);
        }
        Snackbar snackbar = Snackbar.make(view, "Get Tags failed", Snackbar.LENGTH_SHORT);
        snackbar.show();
    }
}