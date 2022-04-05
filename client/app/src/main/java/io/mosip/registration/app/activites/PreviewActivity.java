package io.mosip.registration.app.activites;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import dagger.android.support.DaggerAppCompatActivity;
import io.mosip.registration.app.R;
import io.mosip.registration.clientmanager.service.RegistrationService;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PreviewActivity extends DaggerAppCompatActivity {

    private static final String TAG = PreviewActivity.class.getSimpleName();
    private TextView textView = null;

    @Inject
    RegistrationService registrationService;


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
        setContentView(R.layout.activity_preview);
        textView = findViewById(R.id.registration_preview);

        //to display back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Preview Resident Data");

        final Button button = findViewById(R.id.createpacket);
        button.setOnClickListener( v -> {
            button.setEnabled(false);
            Log.i(TAG, "Clicked on Registration form submit...");
            submitForm();
        });
    }

    private void submitForm() {
        try {
            registrationService.submitRegistrationDto();
            Toast.makeText(getApplicationContext(), "Registration packet created successfully", Toast.LENGTH_LONG);
        } catch (Exception e) {
            Log.e(TAG, "Failed on registration submission", e);
            Toast.makeText(getApplicationContext(), "Packet creation failed", Toast.LENGTH_LONG);
        }
        goToHome();
    }

    public void goToHome() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
