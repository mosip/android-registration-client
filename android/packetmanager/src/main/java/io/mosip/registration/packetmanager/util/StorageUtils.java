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
        File baseDir = null;

        // 1. Try to find/create Documents folder on SD card
        File[] externalFilesDirs = context.getExternalFilesDirs(null);
        if (externalFilesDirs != null) {
            for (File file : externalFilesDirs) {
                if (file != null) {
                    try {
                        if (Environment.isExternalStorageRemovable(file)) {
                            // Extract the root path of the SD card (everything before /Android/data/...)
                            String path = file.getAbsolutePath();
                            int androidIndex = path.indexOf("/Android/data/");
                            if (androidIndex != -1) {
                                String sdRoot = path.substring(0, androidIndex);
                                File sdDocs = new File(sdRoot, "Documents");
                                baseDir = new File(sdDocs, location);
                                break;
                            }
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error calculating SD root: " + file.getAbsolutePath(), e);
                    }
                }
            }
        }

        // 2. Fallback to Primary Internal Storage Documents folder (/storage/emulated/0/Documents)
        if (baseDir == null) {
            File publicDocs = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
            baseDir = new File(publicDocs, location);
        }

        if (!baseDir.exists()) {
            boolean created = baseDir.mkdirs();
            if (!created) {
                Log.e(TAG, "Failed to create directory: " + baseDir.getAbsolutePath() + ". Permission might be missing.");
            }
        }

        return baseDir;
    }
}
