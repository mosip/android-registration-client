package io.mosip.registration.app.jobservice;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.util.Log;
import android.widget.Toast;

import androidx.work.Configuration;

import javax.inject.Inject;

import io.mosip.registration.clientmanager.spi.PacketService;


public class PacketStatusSyncJobService extends JobService {
    private static final String TAG = "PacketStatusSyncJobService";
    private boolean jobCancelled = false;
    private Thread jobThread;

    @Inject
    PacketService packetService;

    public PacketStatusSyncJobService(){
        Configuration.Builder builder = new Configuration.Builder();
        builder.setJobSchedulerJobIdRange(0, 1000);
    }
    @Override
    public boolean onStartJob(JobParameters params) {
        Log.d(TAG, "Job started");
        doBackgroundWork(params);

        return true;
    }

    private void doBackgroundWork(final JobParameters params) {
        jobThread = new Thread(() -> {

            Log.d(TAG, "Packet Status Sync Job Service Started");

            Toast.makeText(getBaseContext(),"Starting packet status sync", Toast.LENGTH_SHORT).show();
            try {
                packetService.syncAllPacketStatus();
            } catch (Exception e) {
                Log.e(TAG, "packet status sync failed", e);
                Toast.makeText(getBaseContext(), "packet status sync failed", Toast.LENGTH_SHORT).show();
            }

            Log.d(TAG, "Packet Status Sync Job Service Completed");
            jobFinished(params, false);
        });

        jobThread.start();
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        if (jobThread != null && jobThread.isAlive()) {
            jobThread.interrupt();
        }
        return true;
    }
}