package io.mosip.registration.clientmanager.util;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import dalvik.system.DexClassLoader;
import io.mosip.kernel.biometrics.spi.IBioApiV2;
import io.mosip.registration.clientmanager.constant.Modality;
import io.mosip.registration.clientmanager.repository.GlobalParamRepository;

public class BioSDKLoader {

    private static final String TAG = BioSDKLoader.class.getSimpleName();
    private static final String ASSETS_FOLDER = "biosdk";
    private static final String DEX_ENTRY_NAME = "classes.dex";

    public static IBioApiV2 loadBioSDK(Context context, Modality modality, GlobalParamRepository globalParamRepository) {
        try {
            String modalityKey = getModalityKey(modality);
            if (modalityKey == null) {
                Log.e(TAG, "Unsupported modality: " + modality);
                return null;
            }

            if (globalParamRepository == null) {
                Log.w(TAG, "GlobalParamRepository is null");
                return null;
            }

            String className = globalParamRepository.getBioSDKProviderClassName(modalityKey);
            if (className == null || className.isEmpty()) {
                Log.w(TAG, "No BioSDK class configured for modality: " + modalityKey);
                return null;
            }

            File sdkFile = findAnySdkFromAssets(context);
            if (sdkFile == null) {
                Log.w(TAG, "No runtime-loadable SDK (.dex or DEX-jar) found in assets (requested for modality: " + modalityKey + ")");
                return null;
            }

            if (!sdkFile.exists() || !sdkFile.canRead()) {
                Log.e(TAG, "SDK file is not accessible: " + sdkFile.getAbsolutePath());
                return null;
            }

            if (sdkFile.length() == 0) {
                Log.e(TAG, "SDK file is empty: " + sdkFile.getAbsolutePath());
                return null;
            }

            if (!isDexFile(sdkFile)) {
                String fileName = sdkFile.getName();
                String errorMsg = "SDK file is not a valid DEX file: " + sdkFile.getAbsolutePath() + 
                    ". The file appears to be a regular JAR with .class files (Java bytecode). " +
                    "Android runtime requires DEX format (classes.dex). ";
                
                errorMsg += "Please provide a DEX-compatible SDK (a .dex file or a .jar/.apk/.zip containing classes.dex).";
                
                Log.e(TAG, errorMsg);
                return null;
            }

            try {
                DexClassLoader classLoader = new DexClassLoader(
                        sdkFile.getAbsolutePath(),
                        context.getCodeCacheDir().getAbsolutePath(),
                        null,
                        context.getClassLoader()
                );

                try {
                    Class<?> sdkClass = classLoader.loadClass(className);
                    Object sdkInstance = sdkClass.getDeclaredConstructor().newInstance();
                    if (sdkInstance instanceof IBioApiV2) {
                        Log.i(TAG, "Successfully loaded BioSDK: " + className + " for modality: " + modalityKey);
                        return (IBioApiV2) sdkInstance;
                    }
                    Log.w(TAG, "Loaded class does not implement IBioApiV2: " + className);
                } catch (ClassNotFoundException e) {
                    Log.e(TAG, "Class not found in SDK: " + className + ". File: " + sdkFile.getName() + 
                        ". Make sure the SDK file contains the class and is a valid DEX file.", e);
                } catch (ReflectiveOperationException e) {
                    Log.e(TAG, "Failed to load class from SDK: " + className + ". File: " + sdkFile.getName(), e);
                }
            } catch (Exception e) {
                Log.e(TAG, "Failed to create DexClassLoader for file: " + sdkFile.getAbsolutePath() + 
                    ". The file may not be a valid DEX file. Error: " + e.getMessage(), e);
                if (e.getMessage() != null && e.getMessage().contains("classes.dex")) {
                    Log.e(TAG, "The SDK file appears to be a regular JAR (with .class files) rather than a DEX file. " +
                        "Android requires DEX format. Please ensure the SDK is properly compiled for Android.");
                }
            }

            return null;

        } catch (Exception e) {
            Log.e(TAG, "Failed to load BioSDK for modality: " + modality, e);
            return null;
        }
    }

    private static String getModalityKey(Modality modality) {
        switch (modality) {
            case FINGERPRINT_SLAB_LEFT:
            case FINGERPRINT_SLAB_RIGHT:
            case FINGERPRINT_SLAB_THUMBS:
                return "finger";
            case IRIS_DOUBLE:
                return "iris";
            case FACE:
            case EXCEPTION_PHOTO:
                return "face";
            default:
                return null;
        }
    }

    private static File findAnySdkFromAssets(Context context) {
        AssetManager assetManager = context.getAssets();
        File file = findSdkInFolder(context, assetManager, ASSETS_FOLDER);
        if (file != null) {
            return file;
        }
        return findSdkInFolder(context, assetManager, "");
    }

    private static File findSdkInFolder(Context context, AssetManager assetManager, String folder) {
        try {
            String[] files = assetManager.list(folder);
            if (files == null) {
                Log.d(TAG, "Assets folder missing: '" + (folder.isEmpty() ? "(root)" : folder) + "'");
                return null;
            }
            if (files.length > 0) {
                Log.d(TAG, "Assets in '" + (folder.isEmpty() ? "(root)" : folder) + "': " + java.util.Arrays.toString(files));
            }

            for (String name : files) {
                String lower = name.toLowerCase(Locale.ROOT);
                if (!lower.endsWith(".dex") && !lower.endsWith(".jar")
                        && !lower.endsWith(".apk") && !lower.endsWith(".zip")) {
                    continue;
                }

                String assetPath = folder.isEmpty() ? name : folder + "/" + name;
                if (isValidDexAsset(context, assetPath)) {
                    File copiedFile = copyFileFromAssets(context, folder, name);
                    if (copiedFile != null) {
                        Log.d(TAG, "Successfully validated DEX source: " + name);
                        return copiedFile;
                    }
                } else {
                    Log.w(TAG, "Asset " + name + " is not a valid DEX container; skipping copy.");
                }
            }
        } catch (IOException e) {
            Log.w(TAG, "Failed to list assets in folder: " + folder, e);
        }
        return null;
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
        File outputFile = null;
        InputStream inputStream = null;
        FileOutputStream outputStream = null;

        try {
            File biosdkDir = new File(context.getFilesDir(), ASSETS_FOLDER);
            if (!biosdkDir.exists() && !biosdkDir.mkdirs()) {
                Log.e(TAG, "Failed to create bioSdk directory");
                return null;
            }

            outputFile = new File(biosdkDir, fileName);
            // Delete if exists (may be read-only from previous run) so we can overwrite
            if (outputFile.exists()) {
                outputFile.setWritable(true, false);
                outputFile.delete();
            }

            String assetPath = folder == null || folder.isEmpty() ? fileName : (folder + "/" + fileName);
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

            outputStream = new FileOutputStream(outputFile);
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.flush();
            outputStream.close();
            outputStream = null;
            outputFile.setWritable(false, false);
            outputFile.setReadable(true, false);

            return outputFile;

        } catch (IOException e) {
            Log.e(TAG, "Failed to copy file from assets: " + fileName, e);
            if (outputFile != null && outputFile.exists()) {
                outputFile.delete();
            }
            return null;
        } finally {
            try {
                if (inputStream != null) inputStream.close();
                if (outputStream != null) outputStream.close();
            } catch (IOException e) {
                Log.e(TAG, "Error closing streams", e);
            }
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

