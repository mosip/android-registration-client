package io.mosip.registration.app.activites;

import android.Manifest;
import android.content.Intent;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;

import android.os.Bundle;
import android.view.ViewGroup;

import androidx.core.app.ActivityCompat;
import dagger.android.support.DaggerAppCompatActivity;
import io.mosip.registration.app.R;
import io.mosip.registration.app.dynamicviews.DynamicView;
import io.mosip.registration.clientmanager.spi.MasterDataService;
import io.mosip.registration.clientmanager.util.DateUtils;
import io.mosip.registration.packetmanager.spi.PacketWriterService;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

public class RegistrationController extends DaggerAppCompatActivity {

    private static final String TAG = RegistrationController.class.getSimpleName();

    private Map<String, DynamicView> dynamicViews = new HashMap<>();
    private ViewGroup pnlPrimary = null;
    private static final String source = "REGISTRATION_CLIENT";
    private static String process = "";
    private static String schemaVersion = "";
    private static String RID = "";

    @Inject
    public PacketWriterService packetWriterService;

    @Inject
    public MasterDataService masterDataService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration_controller);

        //Adding file permissions
        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE};
        ActivityCompat.requestPermissions(RegistrationController.this,
                permissions, 110);

        //to display back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        process = "NEW";
        schemaVersion = "0.1";
        RID = generateRID();

        final Button submitButton = findViewById(R.id.submit);
        submitButton.setOnClickListener( v -> {
            submitButton.setEnabled(false);
            Log.i(TAG, "Clicked on Registration form submit...");
            submitForm();
        });
        pnlPrimary = findViewById(R.id.pnlPrimaryLanguagePanel);
        loadUI();
    }

    private void loadUI() {
        //TODO - Need to load consent screen
        //TODO - display language selection
    }

    //TODO replace the logic with valid RID generator
    private String generateRID() {
        String timestamp = DateUtils.formatToISOStringWithoutMillis(LocalDateTime.now(ZoneOffset.UTC));
        timestamp = timestamp.replaceAll(":|T|Z|-", "");
        return String.format("100011007710031%s", timestamp);
    }

    private void submitForm() {
        //save the selected language and accepted consent
        goToNextActivity();
    }


    public void goToNextActivity() {
        Intent intent = new Intent(this, DemographicsActivity.class);
        intent.putExtra("process", this.process);
        intent.putExtra("schemaVersion", this.schemaVersion);
        intent.putExtra("RID", this.RID);
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
}