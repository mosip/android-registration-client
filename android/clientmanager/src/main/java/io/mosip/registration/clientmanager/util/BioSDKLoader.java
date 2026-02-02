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
                Log.w(TAG, "No runtime-loadable SDK (.dex or DEX-jar) found in assets for modality: " + modalityKey);
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
            if (files == null || files.length == 0) {
                return null;
            }

            // Priority 1: raw .dex files (directly loadable)
            for (String name : files) {
                String lower = name.toLowerCase(Locale.ROOT);
                if (lower.endsWith(".dex")) {
                    File dexFile = copyFileFromAssets(context, folder, name);
                    if (dexFile != null && isDexFile(dexFile)) {
                        Log.d(TAG, "Found DEX file in assets: " + name);
                        return dexFile;
                    }
                }
            }

            // Priority 2: JAR/APK/ZIP that already contains classes.dex (DEX-jar)
            for (String name : files) {
                String lower = name.toLowerCase(Locale.ROOT);
                if (lower.endsWith(".jar") || lower.endsWith(".apk") || lower.endsWith(".zip")) {
                    File container = copyFileFromAssets(context, folder, name);
                    if (container != null && isDexFile(container)) {
                        Log.d(TAG, "Found DEX container in assets: " + name);
                        return container;
                    }
                }
            }

            // Priority 3: AAR only if it already contains classes.dex (rare)
            for (String name : files) {
                String lower = name.toLowerCase(Locale.ROOT);
                if (lower.endsWith(".aar")) {
                    File aarFile = copyFileFromAssets(context, folder, name);
                    if (aarFile != null) {
                        File extractedDex = extractDexFromAarIfPresent(context, aarFile);
                        if (extractedDex != null && isDexFile(extractedDex)) {
                            Log.d(TAG, "Extracted classes.dex from AAR: " + name);
                            return extractedDex;
                        }
                    }
                }
            }
        } catch (IOException e) {
            Log.w(TAG, "Failed to list assets in folder: " + folder, e);
        }
        return null;
    }

    private static File extractDexFromAarIfPresent(Context context, File aarFile) {
        ZipFile zipFile = null;
        InputStream inputStream = null;
        FileOutputStream outputStream = null;

        try {
            File biosdkDir = new File(context.getFilesDir(), ASSETS_FOLDER);
            if (!biosdkDir.exists() && !biosdkDir.mkdirs()) {
                Log.e(TAG, "Failed to create bioSdk directory");
                return null;
            }

            zipFile = new ZipFile(aarFile);
            ZipEntry dexEntry = zipFile.getEntry(DEX_ENTRY_NAME);
            if (dexEntry == null) {
                return null;
            }

            String extractedDexName = aarFile.getName().replace(".aar", ".dex");
            File outputDexFile = new File(biosdkDir, extractedDexName);
            if (outputDexFile.exists() && outputDexFile.length() > 0) {
                return outputDexFile;
            }

            inputStream = zipFile.getInputStream(dexEntry);
            outputStream = new FileOutputStream(outputDexFile);
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.flush();
            return outputDexFile;

        } catch (IOException e) {
            Log.e(TAG, "Failed to extract classes.dex from AAR: " + aarFile.getName(), e);
            return null;
        } finally {
            try {
                if (inputStream != null) inputStream.close();
                if (outputStream != null) outputStream.close();
                if (zipFile != null) zipFile.close();
            } catch (IOException e) {
                Log.e(TAG, "Error closing streams", e);
            }
        }
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
            if (outputFile.exists() && outputFile.length() > 0) {
                return outputFile;
            }

            String assetPath = folder == null || folder.isEmpty() ? fileName : (folder + "/" + fileName);
            try {
                inputStream = context.getAssets().open(assetPath);
            } catch (IOException e) {
                // Backward compatible fallback: some callers may still pass only fileName.
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

