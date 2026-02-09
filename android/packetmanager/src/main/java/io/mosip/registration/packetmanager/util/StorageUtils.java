package io.mosip.registration.packetmanager.util;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;

public class StorageUtils {
    private static final String TAG = "StorageUtils";

    public static File getPacketStorageDir(Context context) {
        String location = ConfigService.getProperty("objectstore.base.location", context);
        if (location == null) {
            location = "packets";
        }

        // 1. Try SD card Documents folder
        File baseDir = getSDCardDir(context, location);
        if (baseDir != null && ensureDirWritable(baseDir)) {
            return baseDir;
        }

        // 2. Try Primary Shared Documents folder (Legacy/Scoped Storage might restrict this)
        baseDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), location);
        if (ensureDirWritable(baseDir)) {
            return baseDir;
        }

        // 3. Fallback to App-private external storage
        baseDir = context.getExternalFilesDir(location);
        if (baseDir != null && ensureDirWritable(baseDir)) {
            return baseDir;
        }

        // 4. Ultimate fallback to App internal storage
        baseDir = new File(context.getFilesDir(), location);
        ensureDirWritable(baseDir);
        return baseDir;
    }

    private static File getSDCardDir(Context context, String location) {
        File[] externalFilesDirs = context.getExternalFilesDirs(null);
        if (externalFilesDirs != null) {
            for (File file : externalFilesDirs) {
                if (file != null && Environment.isExternalStorageRemovable(file)) {
                    try {
                        String path = file.getAbsolutePath();
                        int androidIndex = path.indexOf("/Android/data/");
                        if (androidIndex != -1) {
                            String sdRoot = path.substring(0, androidIndex);
                            return new File(new File(sdRoot, "Documents"), location);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error calculating SD root: " + file.getAbsolutePath(), e);
                    }
                }
            }
        }
        return null;
    }

    private static boolean ensureDirWritable(File dir) {
        try {
            if (!dir.exists() && !dir.mkdirs()) {
                return false;
            }
            return dir.canWrite();
        } catch (Exception e) {
            Log.e(TAG, "Error ensuring directory writability: " + dir.getAbsolutePath(), e);
            return false;
        }
    }
}
