package io.mosip.registration.app.activites;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import dagger.android.support.DaggerAppCompatActivity;
import io.mosip.registration.app.R;
import io.mosip.registration.app.dynamicviews.DynamicComponentFactory;
import io.mosip.registration.app.dynamicviews.DynamicView;
import io.mosip.registration.clientmanager.spi.MasterDataService;
import io.mosip.registration.clientmanager.util.DateUtils;
import io.mosip.registration.packetmanager.dto.PacketWriter.BiometricRecord;
import io.mosip.registration.packetmanager.spi.PacketWriterService;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

import static android.content.pm.PackageManager.MATCH_DEFAULT_ONLY;

public class BiometricsActivity extends DaggerAppCompatActivity  {

    private static final String TAG = BiometricsActivity.class.getSimpleName();
    private static final String NEW_LINE = "\n";

    private Map<String, DynamicView> dynamicViews = new HashMap<>();
    private ViewGroup pnlPrimary = null;

    private String process = "";
    private String schemaVersion = "";
    private String RID = "";
    private JSONArray bioattributes = null;
    private String fieldId = null;
    private String currentModality = null;

    private Button rcaptureButton = null;
    private TextView textView = null;
    private Button button = null;

    String callbackId = null;
    String deviceStatus = null;
    String serialNo = null;

    @Inject
    public PacketWriterService packetWriterService;

    @Inject
    public MasterDataService masterDataService;

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        this.process = intent.getStringExtra("process");
        this.schemaVersion = intent.getStringExtra("schemaVersion");
        this.RID = intent.getStringExtra("RID");
        startActivity();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.process = "NEW";
        this.schemaVersion = "0.1";
        this.RID = generateRID();
        startActivity();
    }

    //TODO replace the logic with valid RID generator
    private String generateRID() {
        String timestamp = DateUtils.formatToISOStringWithoutMillis(LocalDateTime.now(ZoneOffset.UTC));
        timestamp = timestamp.replaceAll(":|T|Z|-", "");
        return String.format("100011007710031%s", timestamp);
    }


    private void startActivity() {
        setContentView(R.layout.registration_controller);
        this.button = findViewById(R.id.submit);


        //Adding file permissions
        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE};
        ActivityCompat.requestPermissions(BiometricsActivity.this,
                permissions, 110);

        //to display back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        this.button.setOnClickListener( v -> {
            this.button.setEnabled(false);
            Log.i(TAG, "Clicked on Registration form submit...");
            saveBiometrics();
        });

        pnlPrimary = findViewById(R.id.pnlPrimaryLanguagePanel);
        loadUI();

        textView = findViewById(R.id.sbi_result);
        rcaptureButton = findViewById(R.id.rcapture);

        if(this.currentModality == null) {
            this.currentModality = "Face";
            this.rcaptureButton.setText("Capture Face");
        }

        this.rcaptureButton.setOnClickListener( v -> {
            this.rcaptureButton.setEnabled(false);
            Log.i(TAG, "Clicked on Discover button");
            discoverSBI();
        });

        if(dynamicViews.isEmpty()) {
            Log.i(TAG, "Nothing to view in this activity, moving to next view");
            goToNextActivity();
        }
    }

    private void saveBiometrics() {
        for (String fieldId : dynamicViews.keySet()) {
            switch (dynamicViews.get(fieldId).getDataType()) {
                case "biometricType" :
                    packetWriterService.setBiometric(RID, fieldId,
                            (BiometricRecord) dynamicViews.get(fieldId).getValue());
            }
        }

        goToNextActivity();
    }

    public void goToNextActivity() {
        Intent intent = new Intent(this, PreviewActivity.class);
        intent.putExtra("process", this.process);
        intent.putExtra("schemaVersion", this.schemaVersion);
        intent.putExtra("RID", this.RID);
        startActivity(intent);
    }

    public void goToHome() {
        Intent intent = new Intent(this, DocumentsActivity.class);
        startActivity(intent);
    }

    public boolean onOptionsItemSelected(MenuItem item){
        goToHome();
        return true;
    }

    private void loadUI() {
        dynamicViews.clear();
        pnlPrimary.removeAllViews();

        DynamicComponentFactory factory = new DynamicComponentFactory(getApplicationContext(), masterDataService);

        String spec = loadJSONFromResource(R.raw.ui_specification);
        try {
            JSONArray compFromJson = new JSONArray(spec);
            for (int i = 0; i < compFromJson.length(); i++) {
                JSONObject item = compFromJson.getJSONObject(i);
                if(item.getBoolean("inputRequired")) {
                    switch (item.getString("controlType")) {
                        case "biometrics":
                            this.bioattributes = item.getJSONArray("bioAttributes");
                            this.fieldId = item.getString("id");
                            DynamicView docDynamicView = factory.getBiometricsComponent(item.getJSONObject("label"), item.getJSONArray("validators"))
                                    .getPrimaryView();
                            pnlPrimary.addView((View)docDynamicView);
                            dynamicViews.put(item.getString("id"), docDynamicView);
                            break;
                    }
                }
            }
        } catch (Exception ex) {
            Log.e(TAG, "Failed to build dynamic ui", ex);
        }
    }

    private String loadJSONFromResource(int resourceNumber) {
        String json = null;
        try(InputStream is = getApplicationContext().getResources().openRawResource(resourceNumber)) {
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            json = new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            Log.e(TAG, "Failed to load ui spec json", ex);
        }
        return json;
    }


    private void discoverSBI() {
        this.textView.append("Started to discover " + this.currentModality + " SBI" + NEW_LINE);
        Intent intent = new Intent();
        intent.setAction("sbi.reg.device");
        List activities = this.getPackageManager().queryIntentActivities(intent, MATCH_DEFAULT_ONLY);
        if (activities.size() > 0) {
            intent.putExtra("input", "{\"type\":\""+this.currentModality+"\"}");
            this.startActivityForResult(intent, 1);
        } else {
            this.textView.append("Supported apps not found!" + NEW_LINE);
        }
    }

    private void info() {
        if (this.callbackId == null)
        {
            this.textView.append("No SBI found!" + NEW_LINE);
            return;
        }
        Intent localIntent = new Intent();
        localIntent.setAction(this.callbackId + ".info");
        List activities = getPackageManager().queryIntentActivities(localIntent, MATCH_DEFAULT_ONLY);
        if (activities.size() > 0) {
            this.textView.append("=======================================" + NEW_LINE);
            this.textView.append("Initiating Device info request for " + this.currentModality + NEW_LINE);
            startActivityForResult(localIntent, 2);
        } else {
            this.textView.append("Supported apps not found!" + NEW_LINE);
        }
    }

    private void rcapture() {
        if (this.serialNo == null)
        {
            this.textView.append("No SBI found!" + NEW_LINE);
            return;
        }

        Intent localIntent = new Intent();
        localIntent.setAction(this.callbackId + ".rcapture");
        List activities = getPackageManager().queryIntentActivities(localIntent, MATCH_DEFAULT_ONLY);
        if (activities.size() > 0) {
            this.textView.append("=======================================" + NEW_LINE);
            this.textView.append("Initiating capture request for " + this.currentModality + NEW_LINE);
            localIntent.putExtra("input", "{\"env\":\"Developer\",\"purpose\":\"Registration\",\"specVersion\":\"0.9.5\",\"timeout\":10000,\"captureTime\":\"2021-07-21T15:00:03Z\",\"transactionId\":\"1626879603493\",\"bio\":[{\"type\":\"Face\",\"count\":1,\"exception\":[],\"requestedScore\":40,\"deviceId\":\"" + this.serialNo + "\",\"deviceSubId\":0,\"previousHash\":\"\",\"bioSubType\":[]}],\"customOpts\":null}");
            startActivityForResult(localIntent, 3);
        } else {
            this.textView.append("Supported apps not found!" + NEW_LINE);
        }
    }

    private byte[] getPayloadBuffer(String jwt) {
        try {
            String[] parts = jwt.split("\\.");
            return Base64.getUrlDecoder().decode(parts[1]);
        } catch (Exception e) {
            Log.e(TAG, "Failed to decode payload");
        }
        return null;
    }

    private void parseDiscoverResponse(String response) {
        try {
            JSONObject jsonObject = new JSONArray(response).getJSONObject(0);
            if (jsonObject.getJSONObject("error").getInt("errorCode") == 0) {
                this.callbackId = jsonObject.getString("callbackId");
                this.deviceStatus = jsonObject.getString("deviceStatus");
                if (!this.callbackId.isEmpty()) {
                    this.textView.append("Discovered below apps : "+ NEW_LINE);
                    this.textView.append("App ID : " + this.callbackId+ NEW_LINE);
                    this.textView.append("Device status : " + this.deviceStatus+ NEW_LINE);

                    info();
                }
            }
        } catch (JSONException e) {
            Log.e(TAG, "Failed to parse discover response");
        }
    }

    private void parseDeviceInfoResponse(String response) {
        try {
            JSONObject jsonObject = new JSONArray(response).getJSONObject(0);
            if (jsonObject.getJSONObject("error").getInt("errorCode") == 0) {
                byte[] payloadBuffer = this.getPayloadBuffer(jsonObject.getString("deviceInfo"));
                JSONObject payload = new JSONObject(new String(payloadBuffer));
                byte[] digitalIdBuffer = this.getPayloadBuffer(payload.getString("digitalId"));
                JSONObject digitalId = new JSONObject(new String(digitalIdBuffer));
                this.serialNo = digitalId.getString("serialNo");
                this.textView.append("DigitalId : " + digitalId + NEW_LINE);

                rcapture();
            }
        } catch (JSONException e) {
            Log.e(TAG, "Failed to parse device info response");
        }
    }

    private void parseRCaptureResponse(String response) {
        try {
            JSONArray jsonObject = new JSONObject(response).getJSONArray("biometrics");
            if (jsonObject.getJSONObject(0).getJSONObject("error").getInt("errorCode") == 0) {
                byte[] payloadBuffer = this.getPayloadBuffer(jsonObject.getJSONObject(0).getString("data"));
                JSONObject payload = new JSONObject(new String(payloadBuffer));
                this.textView.append("Capture response : " + payload+ NEW_LINE);
            }
        } catch (JSONException e) {
            Log.e(TAG, "Failed to parse rcapture response");
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if(resultCode != -1) {
            this.textView.append("RequestCode : " + requestCode + " failed with result code : " + resultCode+ NEW_LINE);
            return;
        }

        switch (requestCode) {
            case 1 :
                parseDiscoverResponse(intent.getStringExtra("Response"));
                break;
            case 2:
                parseDeviceInfoResponse(intent.getStringExtra("Response"));
                break;
            case 3:
                parseRCaptureResponse(intent.getStringExtra("Response"));
                break;
        }
    }
}
