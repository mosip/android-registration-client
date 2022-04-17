package io.mosip.registration.app.activites;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import dagger.android.support.DaggerAppCompatActivity;
import io.mosip.registration.app.R;
import io.mosip.registration.clientmanager.spi.MasterDataService;
import io.mosip.registration.clientmanager.spi.RegistrationService;

import javax.inject.Inject;


public class MainActivity extends DaggerAppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    @Inject
    MasterDataService masterDataService;

    @Inject
    RegistrationService registrationService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        registrationService.clearRegistration();
        getSupportActionBar().setTitle("Home");
    }

    public void click_sync_masterdata(View view) {
        Toast.makeText(this, "Masterdata sync started", Toast.LENGTH_LONG).show();
        try {
            masterDataService.manualSync();
        } catch (Exception e) {
            Log.e(TAG, "Masterdata sync failed", e);
            Toast.makeText(this, "Masterdata sync failed", Toast.LENGTH_LONG).show();
        }
    }

    public void click_new_registration(View view) {
        registrationService.clearRegistration();
        Intent intent = new Intent(this, RegistrationActivity.class);
        startActivity(intent);
    }

    public void click_list_packets(View view) {
        Intent intent = new Intent(this, ListingActivity.class);
        startActivity(intent);
    }


}