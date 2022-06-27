package io.mosip.registration.app.activites;


import android.app.job.JobScheduler;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import javax.inject.Inject;

import dagger.android.support.DaggerAppCompatActivity;
import io.mosip.registration.app.R;
import io.mosip.registration.app.util.JobServiceHelper;
import io.mosip.registration.clientmanager.constant.AuditEvent;
import io.mosip.registration.clientmanager.constant.Components;
import io.mosip.registration.clientmanager.spi.AuditManagerService;
import io.mosip.registration.clientmanager.spi.JobTransactionService;
import io.mosip.registration.clientmanager.spi.MasterDataService;
import io.mosip.registration.clientmanager.spi.PacketService;
import io.mosip.registration.clientmanager.spi.RegistrationService;


public class MainActivity extends DaggerAppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    @Inject
    MasterDataService masterDataService;

    @Inject
    RegistrationService registrationService;

    @Inject
    AuditManagerService auditManagerService;

    @Inject
    PacketService packetService;

    @Inject
    JobTransactionService jobTransactionService;

    JobServiceHelper jobServiceHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        registrationService.clearRegistration();
        getSupportActionBar().setTitle(R.string.home_title);
        jobServiceHelper = new JobServiceHelper(this, (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE), packetService, jobTransactionService);

        auditManagerService.audit(AuditEvent.LOADED_HOME, Components.HOME);
    }

    public void click_sync_masterdata(View view) {
        auditManagerService.audit(AuditEvent.MASTER_DATA_SYNC, Components.HOME);
        jobServiceHelper.syncJobServices();

        Toast.makeText(this, R.string.masterdata_sync_start, Toast.LENGTH_LONG).show();
        try {
            masterDataService.manualSync();
        } catch (Exception e) {
            Log.e(TAG, "Masterdata sync failed", e);
            Toast.makeText(this, R.string.masterdata_sync_fail, Toast.LENGTH_LONG).show();
        }
    }

    public void click_new_registration(View view) {
        auditManagerService.audit(AuditEvent.MASTER_DATA_SYNC, Components.HOME);

        registrationService.clearRegistration();
        Intent intent = new Intent(this, RegistrationActivity.class);
        startActivity(intent);
    }

    public void click_list_packets(View view) {
        auditManagerService.audit(AuditEvent.LIST_REGISTRATION, Components.HOME);

        Intent intent = new Intent(this, ListingActivity.class);
        startActivity(intent);
    }

    public void click_list_JobService(View view) {
        Intent intent = new Intent(this, JobServiceActivity.class);
        startActivity(intent);
    }
}