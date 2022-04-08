package io.mosip.registration.app.activites;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import com.scanlibrary.ScanActivity;
import com.scanlibrary.ScanConstants;
import dagger.android.support.DaggerAppCompatActivity;
import io.mosip.registration.app.R;
import io.mosip.registration.app.dynamicviews.DynamicComponentFactory;
import io.mosip.registration.app.dynamicviews.DynamicView;
import io.mosip.registration.clientmanager.spi.MasterDataService;
import io.mosip.registration.clientmanager.spi.RegistrationService;
import io.mosip.registration.clientmanager.util.UserInterfaceHelperService;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.inject.Inject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class DocumentsActivity extends DaggerAppCompatActivity  {

    private static final String TAG = DocumentsActivity.class.getSimpleName();
    private int SCAN_REQUEST_CODE = 99;
    private Button button = null;
    private Map<String, DynamicView> dynamicViews = new HashMap<>();
    private Map<Integer, String> requestCodeMap = new HashMap<>();
    private ViewGroup pnlPrimary = null;

    @Inject
    RegistrationService registrationService;

    @Inject
    UserInterfaceHelperService userInterfaceHelperService;

    @Inject
    MasterDataService masterDataService;

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        startActivity();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startActivity();
    }

    private void startActivity() {
        setContentView(R.layout.screen_activity);
        this.button = findViewById(R.id.submit);

        //Adding file permissions
        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE};
        ActivityCompat.requestPermissions(DocumentsActivity.this,
                permissions, 110);

        //to display back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Documents");

        this.button.setOnClickListener( v -> {
            this.button.setEnabled(false);
            Log.i(TAG, "Clicked on Registration form submit...");
            goToNextActivity();
        });

        pnlPrimary = findViewById(R.id.pnlPrimaryLanguagePanel);
        loadUI();

        if(dynamicViews.isEmpty()) {
            Log.i(TAG, "Nothing to view in this activity, moving to next view");
            goToNextActivity();
        }
    }

    public void goToNextActivity() {
        Intent intent = new Intent(this, BiometricsActivity.class);
        startActivity(intent);
    }

    public void goToHome() {
        Intent intent = new Intent(this, MainActivity.class);
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

        String spec = userInterfaceHelperService.loadJSONFromResource();
        try {
            JSONArray compFromJson = new JSONArray(spec);
            for (int i = 0; i < compFromJson.length(); i++) {
                JSONObject item = compFromJson.getJSONObject(i);
                if(item.getBoolean("inputRequired")) {
                    switch (item.getString("controlType")) {
                        case "fileupload" :
                            DynamicView docDynamicView = factory.getDocumentComponent(item.getJSONObject("label"), item.getJSONArray("validators"),
                                            item.getString("subType"))
                                    .getPrimaryView();
                            pnlPrimary.addView((View)docDynamicView);
                            dynamicViews.put(item.getString("id"), docDynamicView);
                            SCAN_REQUEST_CODE = SCAN_REQUEST_CODE+1;
                            requestCodeMap.put(SCAN_REQUEST_CODE, item.getString("id"));
                            setScanButtonListener(SCAN_REQUEST_CODE, (View)docDynamicView);
                            break;
                    }
                }
            }
        } catch (Exception ex) {
            Log.e(TAG, "Failed to build dynamic ui", ex);
        }
    }

    private void setScanButtonListener(int requestCode, View view) {
        Button button = view.findViewById(R.id.scan_doc);
        button.setOnClickListener( v -> {
            int preference = ScanConstants.OPEN_CAMERA;
            Intent intent = new Intent(this, ScanActivity.class);
            intent.putExtra(ScanConstants.OPEN_INTENT_PREFERENCE, preference);
            startActivityForResult(intent, requestCode);
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCodeMap.containsKey(requestCode)) {
            Uri uri = data.getExtras().getParcelable(ScanConstants.SCANNED_RESULT);
            try(InputStream  iStream = getContentResolver().openInputStream(uri)) {
                String fieldId = requestCodeMap.get(requestCode);
                this.registrationService.getRegistrationDto().addDocument(fieldId,
                        (String) dynamicViews.get(fieldId).getValue(), getBytes(iStream));
                ((View) dynamicViews.get(fieldId)).findViewById(R.id.doc_saved).setVisibility(View.VISIBLE);
            } catch (Exception e) {
               Log.e(TAG, "Failed to set document to registration dto", e);
            }
        }
    }

    private byte[] getBytes(InputStream inputStream) throws IOException {
        try(ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream()) {
            int bufferSize = 1024;
            byte[] buffer = new byte[bufferSize];

            int len = 0;
            while ((len = inputStream.read(buffer)) != -1) {
                byteBuffer.write(buffer, 0, len);
            }
            return byteBuffer.toByteArray();
        }
    }
}
