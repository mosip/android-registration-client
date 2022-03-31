package io.mosip.registration.app.activites;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import dagger.android.support.DaggerAppCompatActivity;
import io.mosip.registration.app.R;
import io.mosip.registration.app.dynamicviews.DynamicComponentFactory;
import io.mosip.registration.app.dynamicviews.DynamicView;
import io.mosip.registration.clientmanager.spi.MasterDataService;
import io.mosip.registration.clientmanager.util.DateUtils;
import io.mosip.registration.packetmanager.dto.PacketWriter.Document;
import io.mosip.registration.packetmanager.spi.PacketWriterService;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;

public class DocumentsActivity extends DaggerAppCompatActivity  {

    private static final String TAG = DocumentsActivity.class.getSimpleName();
    private String process = "";
    private String schemaVersion = "";
    private String RID = "";
    private Button button = null;
    private Map<String, DynamicView> dynamicViews = new HashMap<>();
    private ViewGroup pnlPrimary = null;

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

    private void startActivity() {
        setContentView(R.layout.registration_controller);
        this.button = findViewById(R.id.submit);

        //Adding file permissions
        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE};
        ActivityCompat.requestPermissions(DocumentsActivity.this,
                permissions, 110);

        //to display back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        this.button.setOnClickListener( v -> {
            this.button.setEnabled(false);
            Log.i(TAG, "Clicked on Registration form submit...");
            saveDocuments();
        });

        pnlPrimary = findViewById(R.id.pnlPrimaryLanguagePanel);
        loadUI();

        if(dynamicViews.isEmpty()) {
            Log.i(TAG, "Nothing to view in this activity, moving to next view");
            goToNextActivity();
        }
    }

    //TODO replace the logic with valid RID generator
    private String generateRID() {
        String timestamp = DateUtils.formatToISOStringWithoutMillis(LocalDateTime.now(ZoneOffset.UTC));
        timestamp = timestamp.replaceAll(":|T|Z|-", "");
        return String.format("100011007710031%s", timestamp);
    }

    private void saveDocuments() {
        for (String fieldId : dynamicViews.keySet()) {
            switch (dynamicViews.get(fieldId).getDataType()) {
                case "documentType" :
                    packetWriterService.setDocument(RID, fieldId,
                            (Document) dynamicViews.get(fieldId).getValue());
                    break;
            }
        }

        goToNextActivity();
    }

    public void goToNextActivity() {
        Intent intent = new Intent(this, BiometricsActivity.class);
        intent.putExtra("process", this.process);
        intent.putExtra("schemaVersion", this.schemaVersion);
        intent.putExtra("RID", this.RID);
        startActivity(intent);
    }

    public void goToHome() {
        Intent intent = new Intent(this, DemographicsActivity.class);
        startActivity(intent);
    }

    public boolean onOptionsItemSelected(MenuItem item){
        goToHome();
        return true;
    }


    public String loadJSONFromResource(int resourceNumber) {
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
                        case "fileupload" :
                            DynamicView docDynamicView = factory.getDocumentComponent(item.getJSONObject("label"), item.getJSONArray("validators"))
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
}
