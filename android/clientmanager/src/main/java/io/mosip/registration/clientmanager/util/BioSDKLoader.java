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
                Log.e(TAG, "Class not found in SDK: " + className, e);
            } catch (Exception e) {
                Log.e(TAG, "Failed to load class from SDK: " + className, e);
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

            for (String name : files) {
                String lower = name.toLowerCase(Locale.ROOT);
                if (lower.endsWith(".jar")) {
                    return copyFileFromAssets(context, name);
                }
            }

            for (String name : files) {
                String lower = name.toLowerCase(Locale.ROOT);
                if (lower.endsWith(".aar")) {
                    File aarFile = copyFileFromAssets(context, name);
                    if (aarFile != null) {
                        return extractClassesJarFromAar(context, aarFile);
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

            String extractedJarName = aarFile.getName().replace(".aar", "-classes.jar");
            outputJarFile = new File(biosdkDir, extractedJarName);

            if (outputJarFile.exists() && outputJarFile.length() > 0) {
                return outputJarFile;
            }

            zipFile = new ZipFile(aarFile);
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
}

