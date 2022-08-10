package io.mosip.registration.app.util;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * @author Anshul vanawat
 */

public class FileUtility {

    private static final String TAG = FileUtility.class.getSimpleName();

    public static void SaveFileInAppStorage(final Context context, String fileName, String fileContents) throws IOException {
        try (FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE)) {
            fos.write(fileContents.getBytes(StandardCharsets.UTF_8));
        }
    }

    public static String getFileContentFromAppStorage(final Context context, String fileName) throws IOException {
        FileInputStream fis = context.openFileInput(fileName);
        InputStreamReader inputStreamReader = new InputStreamReader(fis, StandardCharsets.UTF_8);
        StringBuilder stringBuilder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(inputStreamReader)) {
            String line = reader.readLine();
            while (line != null) {
                stringBuilder.append(line).append('\n');
                line = reader.readLine();
            }

            return stringBuilder.toString();
        } catch (IOException e) {
            Log.e(TAG, "getFileContentFromAppStorage: Error file opening raw file for reading", e);
        }
        return null;
    }

    public static void deleteFileInAppStorage(final Context context, String fileName) {
        context.deleteFile(fileName);
    }
}
