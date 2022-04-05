package io.mosip.registration.app.activites;

import android.content.Intent;
import android.util.Log;
import android.widget.Button;

import android.os.Bundle;

import dagger.android.support.DaggerAppCompatActivity;
import io.mosip.registration.app.R;
import io.mosip.registration.clientmanager.service.RegistrationService;
import io.mosip.registration.clientmanager.spi.MasterDataService;

import javax.inject.Inject;

public class RegistrationActivity extends DaggerAppCompatActivity {

    private static final String TAG = RegistrationActivity.class.getSimpleName();

    @Inject
    RegistrationService registrationService;

    @Inject
    MasterDataService masterDataService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration_activity);

        //to display back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Resident Consent");

        final Button startRegistration = findViewById(R.id.start_registration);
        startRegistration.setOnClickListener( v -> {
            startRegistration.setEnabled(false);
            Log.i(TAG, "Clicked on Registration form submit...");
            startRegistration();
        });
    }


    private void startRegistration() {
        registrationService.startRegistration();
        //TODO set consent
        goToNextActivity();
    }


    public void goToNextActivity() {
        Intent intent = new Intent(this, DemographicsActivity.class);
        startActivity(intent);
    }


    public void goToHome() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}