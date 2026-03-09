package io.mosip.registration.clientmanager.util;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import dalvik.system.DexClassLoader;
import io.mosip.kernel.biometrics.spi.IBioApiV2;
public class BioSdkLoader {

    private static final String TAG = BioSdkLoader.class.getSimpleName();
    private static final String ASSETS_FOLDER = "biosdk";
    private static final String DEX_ENTRY_NAME = "classes.dex";

    /**
     * Called once before the vendor loop — finds all SDK files from assets.
     */
    public static List<File> findAllSdkFiles(Context context) {
        if (context == null) return Collections.emptyList();
        return findAllSdkFilesFromAssets(context);
    }

    /**
     * Called per vendor inside the loop — loads single class from already-found SDK files.
     * className guaranteed non-empty by repository.
     */
    public static IBioApiV2 loadProvider(Context context, String className, List<File> sdkFiles) {
        if (context == null || className == null || (className = className.trim()).isEmpty()
                || sdkFiles == null || sdkFiles.isEmpty())
            return null;

        for (File sdkFile : sdkFiles) {
            if (!sdkFile.exists() || !sdkFile.canRead() || !isDexFile(sdkFile)) continue;
            try {
                DexClassLoader classLoader = new DexClassLoader(
                        sdkFile.getAbsolutePath(),
                        context.getCodeCacheDir().getAbsolutePath(),
                        null,
                        context.getClassLoader());
                Class<?> sdkClass = classLoader.loadClass(className);
                Object instance = sdkClass.getDeclaredConstructor().newInstance();
                if (instance instanceof IBioApiV2) {
                    Log.i(TAG, "Loaded: " + className + " from: " + sdkFile.getName());
                    return (IBioApiV2) instance;
                } else {
                    Log.w(TAG, "Class does not implement IBioApiV2: " + className);
                }
            } catch (ClassNotFoundException e) {
                Log.d(TAG, "Class not in " + sdkFile.getName() + ": " + className);
            } catch (Exception e) {
                Log.w(TAG, "Failed to load: " + className + " from " + sdkFile.getName(), e);
            }
        }
        Log.d(TAG, "Class not found in any SDK file: " + className);
        return null;
    }

    /**
     * Returns all valid DEX/JAR files from assets (biosdk folder first, then assets root).
     * Each file is copied to app storage. Enables loading different vendor classes from different SDK files.
     */
    private static List<File> findAllSdkFilesFromAssets(Context context) {
        AssetManager assetManager = context.getAssets();
        List<File> list = new ArrayList<>();
        list.addAll(collectSdkFilesInFolder(context, assetManager, ASSETS_FOLDER));
        list.addAll(collectSdkFilesInFolder(context, assetManager, ""));
        if (!list.isEmpty()) {
            Log.d(TAG, "Found " + list.size() + " SDK file(s) in assets");
        }
        return list;
    }

    private static List<File> collectSdkFilesInFolder(Context context, AssetManager assetManager, String folder) {
        List<File> result = new ArrayList<>();
        try {
            String[] files = assetManager.list(folder);
            if (files == null) return result;
            for (String name : files) {
                String lower = name.toLowerCase(Locale.ROOT);
                if (!lower.endsWith(".dex") && !lower.endsWith(".jar")
                        && !lower.endsWith(".apk") && !lower.endsWith(".zip")) {
                    continue;
                }
                String assetPath = folder.isEmpty() ? name : folder + "/" + name;
                if (isValidDexAsset(context, assetPath)) {
                    File copiedFile = copyFileFromAssets(context, folder, name);
                    if (copiedFile != null && copiedFile.exists() && isDexFile(copiedFile)) {
                        result.add(copiedFile);
                        Log.d(TAG, "Valid DEX source: " + name);
                    }
                } else {
                    Log.w(TAG, "Asset " + name + " is not a valid DEX container; skipping.");
                }
            }
        } catch (IOException e) {
            Log.w(TAG, "Failed to list assets in folder: " + (folder.isEmpty() ? "(root)" : folder), e);
        }
        return result;
    }

    /**
     * Checks the asset's magic number (DEX or ZIP) without copying the full file.
     * For .dex: validates "dex\n". For .jar/.apk/.zip: scans ZIP for classes.dex entry.
     */
    private static boolean isValidDexAsset(Context context, String assetPath) {
        try (InputStream is = context.getAssets().open(assetPath)) {
            byte[] header = new byte[4];
            if (is.read(header) == 4) {
                String magic = new String(header, StandardCharsets.US_ASCII);
                if ("dex\n".equals(magic)) {
                    return true;
                }
                if (header[0] == 0x50 && header[1] == 0x4B && header[2] == 0x03 && header[3] == 0x04) {
                    return hasClassesDexInZip(context, assetPath);
                }
            }
        } catch (IOException e) {
            Log.e(TAG, "Quick check failed for " + assetPath, e);
        }
        return false;
    }

    /**
     * Scans ZIP asset for classes.dex entry without copying the full file to disk.
     */
    private static boolean hasClassesDexInZip(Context context, String assetPath) {
        try (java.util.zip.ZipInputStream zis = new java.util.zip.ZipInputStream(context.getAssets().open(assetPath))) {
            java.util.zip.ZipEntry entry;
            byte[] buffer = new byte[8192];
            while ((entry = zis.getNextEntry()) != null) {
                if (DEX_ENTRY_NAME.equals(entry.getName())) {
                    return true;
                }
                while (zis.read(buffer) != -1) {
                    // Skip entry data to advance to next entry
                }
            }
        } catch (IOException e) {
            Log.w(TAG, "ZIP scan failed for " + assetPath, e);
        }
        return false;
    }

    private static File copyFileFromAssets(Context context, String folder, String fileName) {
        File biosdkDir = new File(context.getFilesDir(), ASSETS_FOLDER);
        if (!biosdkDir.exists() && !biosdkDir.mkdirs()) {
            Log.e(TAG, "Failed to create bioSdk directory");
            return null;
        }

        File outputFile = new File(biosdkDir, fileName);
        if (outputFile.exists()) {
            outputFile.setWritable(true, false);
            outputFile.delete();
        }

        String assetPath = folder == null || folder.isEmpty() ? fileName : (folder + "/" + fileName);
        InputStream inputStream;
        try {
            inputStream = context.getAssets().open(assetPath);
        } catch (IOException e) {
            try {
                inputStream = context.getAssets().open(fileName);
            } catch (IOException e2) {
                Log.w(TAG, "File not found in assets: " + assetPath);
                return null;
            }
        }

        try (InputStream in = inputStream; FileOutputStream out = new FileOutputStream(outputFile)) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
            out.flush();
            outputFile.setWritable(false, false);
            outputFile.setReadable(true, false);
            return outputFile;
        } catch (IOException e) {
            Log.e(TAG, "Failed to copy file from assets: " + fileName, e);
            if (outputFile.exists()) {
                outputFile.delete();
            }
            return null;
        }
    }

    /**
     * Checks if a file is a valid DEX file.
     * A DEX file starts with "dex\n" magic number, or a JAR/ZIP containing classes.dex.
     *
     * @param file The file to check
     * @return true if the file appears to be a DEX file, false otherwise
     */
    private static boolean isDexFile(File file) {
        if (file == null || !file.exists() || file.length() < 4) {
            return false;
        }

        try {
            // First check if it's a JAR/APK/ZIP containing classes.dex
            try (ZipFile zipFile = new ZipFile(file)) {
                ZipEntry dexEntry = zipFile.getEntry(DEX_ENTRY_NAME);
                if (dexEntry != null) {
                    return true;
                }
                return false;
            } catch (java.util.zip.ZipException e) {
                // Not a ZIP file, check if it's a direct DEX file
                try (java.io.FileInputStream fis = new java.io.FileInputStream(file)) {
                    byte[] magic = new byte[4];
                    if (fis.read(magic) == 4) {
                        String magicString = new String(magic, StandardCharsets.US_ASCII);
                        return "dex\n".equals(magicString);
                    }
                }
            }
        } catch (Exception e) {
            Log.w(TAG, "Error checking if file is DEX: " + file.getName(), e);
        }

        return false;
    }
}
