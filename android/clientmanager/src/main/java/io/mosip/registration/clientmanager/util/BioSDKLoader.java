package io.mosip.registration.clientmanager.util;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
    private static final String AAR_CLASSES_JAR = "classes.jar";

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
                Log.w(TAG, "No SDK .jar/.aar found in assets for modality: " + modalityKey);
                return null;
            }

            // Validate file exists and is readable
            if (!sdkFile.exists() || !sdkFile.canRead()) {
                Log.e(TAG, "SDK file is not accessible: " + sdkFile.getAbsolutePath());
                return null;
            }

            // Check if file is a valid DEX/JAR
            if (sdkFile.length() == 0) {
                Log.e(TAG, "SDK file is empty: " + sdkFile.getAbsolutePath());
                return null;
            }

            // Validate that the file is a DEX-compatible file
            if (!isDexFile(sdkFile)) {
                String fileName = sdkFile.getName();
                String errorMsg = "SDK file is not a valid DEX file: " + sdkFile.getAbsolutePath() + 
                    ". The file appears to be a regular JAR with .class files (Java bytecode). " +
                    "Android runtime requires DEX format (classes.dex). ";
                
                if (fileName.contains("-classes.jar") || fileName.endsWith(".aar")) {
                    errorMsg += "NOTE: AAR files and their extracted classes.jar are not suitable for runtime loading. " +
                        "AARs are designed for compile-time inclusion. For runtime loading, the SDK must be provided " +
                        "as a DEX-compatible JAR file (containing classes.dex) instead of an AAR.";
                } else {
                    errorMsg += "Please ensure the SDK is compiled for Android and contains DEX format.";
                }
                
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
                    Object sdkInstance = sdkClass.newInstance();
                    if (sdkInstance instanceof IBioApiV2) {
                        Log.i(TAG, "Successfully loaded BioSDK: " + className + " for modality: " + modalityKey);
                        return (IBioApiV2) sdkInstance;
                    }
                    Log.w(TAG, "Loaded class does not implement IBioApiV2: " + className);
                } catch (ClassNotFoundException e) {
                    Log.e(TAG, "Class not found in SDK: " + className + ". File: " + sdkFile.getName() + 
                        ". Make sure the SDK file contains the class and is a valid DEX file.", e);
                } catch (Exception e) {
                    Log.e(TAG, "Failed to load class from SDK: " + className + ". File: " + sdkFile.getName(), e);
                }
            } catch (Exception e) {
                Log.e(TAG, "Failed to create DexClassLoader for file: " + sdkFile.getAbsolutePath() + 
                    ". The file may not be a valid DEX file. Error: " + e.getMessage(), e);
                // If it's a DEX loading error, suggest checking the file format
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

            // Priority 1: Look for DEX-compatible JAR files first (these work for runtime loading)
            for (String name : files) {
                String lower = name.toLowerCase(Locale.ROOT);
                if (lower.endsWith(".jar")) {
                    File jarFile = copyFileFromAssets(context, name);
                    if (jarFile != null) {
                        Log.d(TAG, "Found JAR file in assets: " + name);
                        return jarFile;
                    }
                }
            }

            // Priority 2: Look for AAR files (extract classes.jar, but note: may not be DEX-compatible)
            for (String name : files) {
                String lower = name.toLowerCase(Locale.ROOT);
                if (lower.endsWith(".aar")) {
                    Log.d(TAG, "Found AAR file in assets: " + name + ". Extracting classes.jar...");
                    File aarFile = copyFileFromAssets(context, name);
                    if (aarFile != null) {
                        File extractedJar = extractClassesJarFromAar(context, aarFile);
                        if (extractedJar != null) {
                            Log.w(TAG, "Extracted classes.jar from AAR. WARNING: AAR files contain regular JARs " +
                                "with .class files, not DEX format. For runtime loading, use DEX-compatible JAR files instead.");
                            return extractedJar;
                        }
                    }
                }
            }
        } catch (IOException e) {
            Log.w(TAG, "Failed to list assets in folder: " + folder, e);
        }
        return null;
    }

    private static File extractClassesJarFromAar(Context context, File aarFile) {
        File outputJarFile = null;
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
            
            // First, check if AAR contains a pre-compiled DEX file (unlikely but possible)
            ZipEntry dexEntry = zipFile.getEntry("classes.dex");
            if (dexEntry != null) {
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
                outputStream.close();
                inputStream.close();
                Log.i(TAG, "Extracted DEX file from AAR: " + extractedDexName);
                return outputDexFile;
            }

            // AAR files contain classes.jar with .class files (Java bytecode), not DEX format
            // For runtime loading, Android requires DEX format. AARs are meant for compile-time inclusion.
            // We'll extract it but it won't work with DexClassLoader - it needs to be converted to DEX.
            String extractedJarName = aarFile.getName().replace(".aar", "-classes.jar");
            outputJarFile = new File(biosdkDir, extractedJarName);

            if (outputJarFile.exists() && outputJarFile.length() > 0) {
                // File already extracted, but it's still a regular JAR, not DEX
                Log.w(TAG, "Using extracted classes.jar from AAR. Note: AAR files contain regular JARs " +
                    "with .class files, not DEX format. For runtime loading, the SDK should be provided " +
                    "as a DEX-compatible JAR file instead of an AAR.");
                return outputJarFile;
            }

            ZipEntry classesJarEntry = zipFile.getEntry(AAR_CLASSES_JAR);
            if (classesJarEntry == null) {
                Log.e(TAG, "classes.jar not found in AAR: " + aarFile.getName());
                return null;
            }

            inputStream = zipFile.getInputStream(classesJarEntry);
            outputStream = new FileOutputStream(outputJarFile);
            
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.flush();

            Log.w(TAG, "Extracted classes.jar from AAR. IMPORTANT: AAR files are not suitable for runtime loading. " +
                "The extracted JAR contains .class files (Java bytecode), but Android runtime requires DEX format. " +
                "For runtime loading, the SDK should be provided as a DEX-compatible JAR file (containing classes.dex) " +
                "instead of an AAR file. AARs are designed for compile-time inclusion in Android projects.");

            return outputJarFile;

        } catch (IOException e) {
            Log.e(TAG, "Failed to extract classes.jar from AAR: " + aarFile.getName(), e);
            if (outputJarFile != null && outputJarFile.exists()) {
                outputJarFile.delete();
            }
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

    private static File copyFileFromAssets(Context context, String fileName) {
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

            String assetPath = ASSETS_FOLDER + "/" + fileName;
            try {
                inputStream = context.getAssets().open(assetPath);
            } catch (IOException e) {
                try {
                    inputStream = context.getAssets().open(fileName);
                } catch (IOException e2) {
                    Log.w(TAG, "File not found in assets: " + fileName);
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
            // First check if it's a JAR/ZIP containing classes.dex
            try (ZipFile zipFile = new ZipFile(file)) {
                ZipEntry dexEntry = zipFile.getEntry("classes.dex");
                if (dexEntry != null) {
                    return true;
                }
                // It's a ZIP but doesn't contain classes.dex - not a valid DEX file
                return false;
            } catch (java.util.zip.ZipException e) {
                // Not a ZIP file, check if it's a direct DEX file
                try (java.io.FileInputStream fis = new java.io.FileInputStream(file)) {
                    byte[] magic = new byte[4];
                    if (fis.read(magic) == 4) {
                        String magicString = new String(magic);
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

