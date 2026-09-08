package io.mosip.registration_client.telemetry;

import android.content.Context; 
import android.content.Intent; 
import android.content.IntentFilter; 
import android.os.BatteryManager; 
import android.os.Build; 
import android.os.SystemClock; 
import android.app.ActivityManager; 
import android.util.Log;
import android.content.pm.PackageManager;
import android.content.pm.PackageInfo;

import java.io.File; 
import java.io.FileOutputStream; 
import java.io.IOException; 
import java.nio.charset.StandardCharsets; 
import java.text.SimpleDateFormat; 
import java.util.Date; 
import java.util.Locale; 
import java.util.TimeZone; 
import java.util.concurrent.ExecutorService; 
import java.util.concurrent.Executors;

public class AndroidMetricCollector {
    private static final String TAG = "AndroidMetricCollector";
    private static final String LOG_FILE_NAME = "metrics.log";
    private static final String LOG_DIR_NAME = ".metrics";
    private static final long MAX_LOG_SIZE_BYTES = 5L * 1024 * 1024; // 5MB

    private static final String LOGGER_NAME = "io.mosip.registration_client.telemetry.AndroidMetricCollector";
    private static final String THREAD_NAME = "android-metrics-publisher";

    private static final String PREFS_NAME = "telemetry_prefs";
    private static final String KEY_MACHINE_ID = "cached_machine_id";
    private static Context appContext;

    private final Context context;
    private final ExecutorService writeExecutor = Executors.newSingleThreadExecutor();
    private final Object fileLock = new Object();
    private static volatile String cachedMachineId = null;

    public static void setMachineId(String machineId) {
        if (machineId != null && !machineId.trim().isEmpty()){
            cachedMachineId = machineId;
            if (appContext != null) {
                appContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                    .edit()
                    .putString(KEY_MACHINE_ID, machineId)
                    .apply();
            }
        }
    }

    public AndroidMetricCollector(Context context) {
        this.context = context.getApplicationContext();
        if (appContext == null) {
            appContext = this.context;
        }
    }

    public File getLogFile() {
        File dir = new File(context.getFilesDir(), LOG_DIR_NAME);
        if (!dir.exists()) dir.mkdirs();
        return new File(dir, LOG_FILE_NAME);
    }

    // ─── Called by TelemetryHandler (from Flutter via Pigeon) ───────────────
    public void logRawMetric(String metricJson) {
        writeExecutor.execute(() -> {
            //Log.d(TAG, "logRawMetric called");//for adb logcat -s AndroidMetricCollector (logs)
            String processedInner = metricJson;
            // Inject inner metadata envelope seamlessly if it is a standard JSON dictionary object
            if (metricJson != null && metricJson.startsWith("{") && metricJson.endsWith("}")) {
                String strip = metricJson.substring(1, metricJson.length() - 1);
                processedInner = "{" +
                    "\"@timestamp\":\"" + generateUtcTimestamp() + "\"" +
                    "," + strip +
                    ",\"device_model\":\"" + getDeviceId() + "\"" +
                    "}";
            }
            appendLine(buildEnvelope(processedInner));
        });
    }

    // ─── Called natively for system metrics ─────────────────────────────────
    public void logMetric(String name, String type, double value, String unit) {
        String innerJson = buildInnerMessage(name, type, value, unit, null);
        writeExecutor.execute(() -> appendLine(buildEnvelope(innerJson)));
    }

    // ─── Collect system metrics (mirrors desktop JVM metrics) ───────────────
    public void collectAndLogSystemMetrics() {
        writeExecutor.execute(() -> {
            // 1. system.uptime
            double uptimeSeconds = SystemClock.elapsedRealtime() / 1000.0;
            appendLine(buildEnvelope(
                buildInnerMessage("system.uptime", "gauge", uptimeSeconds, "seconds", null)
            ));

            // 2. android.memory.used
            ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            ActivityManager.MemoryInfo memInfo = new ActivityManager.MemoryInfo();
            if (am != null) {
                am.getMemoryInfo(memInfo);
                double usedMemMB = (memInfo.totalMem - memInfo.availMem) / (1024.0 * 1024.0);
                appendLine(buildEnvelope(
                    buildInnerMessage("android.memory.used", "gauge", usedMemMB, "MB", null)
                ));

                // 3. android.memory.available
                double availMemMB = memInfo.availMem / (1024.0 * 1024.0);
                appendLine(buildEnvelope(
                    buildInnerMessage("android.memory.available", "gauge", availMemMB, "MB", null)
                ));
            }

            // 4. android.battery.level
            IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
            Intent batteryStatus = context.registerReceiver(null, ifilter);
            if (batteryStatus != null) {
                int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
                if (level >= 0 && scale > 0) {
                    double batteryPct = (level / (double) scale) * 100;
                    appendLine(buildEnvelope(
                        buildInnerMessage("android.battery.level", "gauge", batteryPct, "percent", null)
                    ));
                }
            }
            // 5. Single device.info event for static device/app metadata
            try{
            String osRelease = Build.VERSION.RELEASE;//release without "android"
            int sdkInt = Build.VERSION.SDK_INT;
            PackageManager pm = context.getPackageManager();
            PackageInfo pInfo;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
               pInfo = pm.getPackageInfo(context.getPackageName(), PackageManager.PackageInfoFlags.of(0));
            } else {
             pInfo = pm.getPackageInfo(context.getPackageName(), 0);
            }
            String versionName = (pInfo.versionName != null) ? pInfo.versionName : "unknown";
            long versionCode = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) 
            ? pInfo.getLongVersionCode() 
            : pInfo.versionCode;
            String machineId = (getMachineId() != null) ? getMachineId() : "";
            String deviceInfoJson = "{" +
            "\"@timestamp\":\"" + generateUtcTimestamp() + "\"," +
            "\"name\":\"device.info\"," +
            "\"type\":\"event\"," +
            "\"machine\":\"" + machineId + "\"," +
            "\"device_model\":\"" + getDeviceId() + "\"," +
            "\"os_release\":\"" + osRelease + "\"," +
            "\"sdk_int\":" + sdkInt + "," +
            "\"app_version_name\":\"" + versionName + "\"," +
            "\"app_version_code\":" + versionCode +
            "}";
            appendLine(buildEnvelope(deviceInfoJson));
            
           }catch (PackageManager.NameNotFoundException e) {
                Log.e(TAG, "Failed to collect device metadata", e);
            }
        });
    }

    private String buildInnerMessage(String name, String type, double value, String unit, String state) {
        StringBuilder sb = new StringBuilder();
        sb.append("{")
          .append("\"@timestamp\":\"").append(generateUtcTimestamp()).append("\"")
          .append(",\"name\":\"").append(name).append("\"")
          .append(",\"type\":\"").append(type).append("\"")
          .append(",\"device_model\":\"").append(getDeviceId()).append("\"");

        if (state != null && !state.isEmpty()) {
            sb.append(",\"state\":\"").append(state).append("\"");
        }
        sb.append(",\"value\":").append(value);
        if (unit != null && !unit.isEmpty()) {
            sb.append(",\"unit\":\"").append(unit).append("\"");
        }
        sb.append("}");
        return sb.toString();
    }

    private String buildEnvelope(String innerJson) {
        String escapedInner = innerJson
            .replace("\\", "\\\\")
            .replace("\"", "\\\"");

        return "{" +
            "\"@timestamp\":\"" + generateLocalTimestamp() + "\"" +
            ",\"@version\":\"1\"" +
            ",\"message\":\"" + escapedInner + "\"" +
            ",\"logger_name\":\"" + LOGGER_NAME + "\"" +
            ",\"thread_name\":\"" + THREAD_NAME + "\"" +
            ",\"level\":\"INFO\"" +
            ",\"level_value\":20000" +
            "}";
    }

    private void appendLine(String jsonLine) {
        synchronized (fileLock) {
            try {
                rotateIfNeeded();
                try (FileOutputStream fos = new FileOutputStream(getLogFile(), true)) {
                    fos.write((jsonLine + "\n").getBytes(StandardCharsets.UTF_8));
                }
            } catch (IOException e) {
                Log.e(TAG, "Failed to write metric", e);
            }
        }
    }

    private void rotateIfNeeded() {
        File logFile = getLogFile();
        if (logFile.exists() && logFile.length() > MAX_LOG_SIZE_BYTES) {
            File rotated = new File(new File(context.getFilesDir(), LOG_DIR_NAME), LOG_FILE_NAME + ".1");
            if (rotated.exists()) rotated.delete();
            logFile.renameTo(rotated);
        }
    }

    private String generateUtcTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        return sdf.format(new Date());
    }

    private String generateLocalTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.US);
        sdf.setTimeZone(TimeZone.getDefault());
        return sdf.format(new Date());
    }
    
    private String getMachineId() {
        if (cachedMachineId == null && appContext != null) {
            cachedMachineId = appContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                                    .getString(KEY_MACHINE_ID, null);
        }
        return cachedMachineId;
    }

    private String getDeviceId() {
        return Build.MODEL != null ? Build.MODEL.replace(" ", "_") : "Android_Device";
    }
    
    public void shutdown() {
        if (writeExecutor != null && !writeExecutor.isShutdown()) {
            writeExecutor.shutdown();
        }
    }

    public File prepareFileForUpload() {
    synchronized (fileLock) {
        File currentLog = getLogFile();
            File processingFile = new File(currentLog.getParent(), "metrics.log.processing");

            // CRITICAL FIX: If a previous upload failed, the processing file still exists.
            // Return it immediately so the worker can finish uploading it FIRST.
            if (processingFile.exists()) {
                return processingFile;
            }

            // If file doesn't exist or is empty, nothing to upload
            if (!currentLog.exists() || currentLog.length() == 0) {
                return null;
            }
           
            // Atomically rename the current log so the Collector can create a new one
            if (currentLog.renameTo(processingFile)) {
                return processingFile;
            } else {
                return null; // Rename failed
            }
    }
}
}