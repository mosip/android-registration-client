package io.mosip.registration_client.telemetry;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.io.File;
import java.net.URL;

import io.tus.android.client.TusPreferencesURLStore;
import io.tus.java.client.TusClient;
import io.tus.java.client.TusUpload;
import io.tus.java.client.TusUploader;

/**
 * Android equivalent of desktop's TelemetryUploadService.
 *
 * Desktop pipeline:
 *   LoggingJsonMeterRegistry → metrics.log → TelemetryUploadService → TUSD
 *
 * Android pipeline:
 *   AndroidMetricCollector → metrics.log → TelemetryUploadWorker → TUSD
 *
 * Runs every 15 minutes via WorkManager.
 * Uses TUS resumable protocol — survives network drops.
 * Deletes local file ONLY after confirmed upload.
 */
public class TelemetryUploadWorker extends Worker {

    private static final String TAG = "TelemetryUploadWorker";
    private static final String LOG_FILE_NAME = "metrics.log";
    private static final String LOG_DIR_NAME = ".metrics";
    private static final int CHUNK_SIZE = 1024; // 1KB — matches desktop config

    //  Replace with your actual TUSD server URL
    // Same URL as mosip.registration.tus.server.url on desktop
    private static final String TUS_SERVER_URL = "/files/";

    public TelemetryUploadWorker(
            @NonNull Context context,
            @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
// 1. Instantiate the collector locally to access the atomic handoff file lock
        AndroidMetricCollector collector = new AndroidMetricCollector(getApplicationContext());
        
        // 2. Call your custom handoff method to rotate metrics.log -> metrics.log.processing safely
        File uploadFile = collector.prepareFileForUpload();

        // If the handoff returns null, it means the file was empty or didn't exist
        if (uploadFile == null) {
            Log.d(TAG, "No metrics to upload — skipping");
            return Result.success();
        }

        Log.d(TAG, "Starting TUS upload: " + uploadFile.getAbsolutePath()
            + " (" + uploadFile.length() + " bytes)");

        try {
            TusClient client = new TusClient();
            client.setUploadCreationURL(new URL(TUS_SERVER_URL));

            SharedPreferences prefs = getApplicationContext()
                .getSharedPreferences("tus_telemetry_prefs", Context.MODE_PRIVATE);
            client.enableResuming(new TusPreferencesURLStore(prefs));

            // 3. Point TUS to the isolated .processing file
            TusUpload upload = new TusUpload(uploadFile);
            TusUploader uploader = client.resumeOrCreateUpload(upload);
            uploader.setChunkSize(CHUNK_SIZE);

            while (uploader.uploadChunk() > -1) {
                Log.d(TAG, "Uploaded offset: " + uploader.getOffset()
                    + " / " + upload.getSize());
            }

            uploader.finish();
            Log.i(TAG, "Upload complete: " + uploader.getUploadURL());

            // 4. Delete the .processing staging file explicitly after confirmed success
            if (uploadFile.delete()) {
                Log.i(TAG, "metrics.log.processing deleted after successful upload");
            } else {
                Log.w(TAG, "Could not delete metrics.log.processing — will retry next cycle");
            }

            return Result.success();

        } catch (Exception e) {
            Log.e(TAG, "Upload failed, will retry: " + e.getMessage(), e);
            return Result.retry();
        }
     }
 }