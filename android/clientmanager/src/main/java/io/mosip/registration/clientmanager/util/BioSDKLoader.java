package io.mosip.registration.clientmanager.util;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import dalvik.system.DexClassLoader;
import io.mosip.kernel.biometrics.spi.IBioApiV2;
import io.mosip.registration.clientmanager.constant.Modality;
import io.mosip.registration.clientmanager.repository.GlobalParamRepository;

/**
 * Utility class to dynamically load BioSDK implementations from assets folder
 */
public class BioSDKLoader {

    private static final String TAG = BioSDKLoader.class.getSimpleName();
    private static final String ASSETS_FOLDER = "bioSdk";
    private static final String MOCK_VENDOR_NAME = "mockvendor";
    private static final String MOCK_SDK_FILE_NAME = "matchsdk-debug.aar";
    private static final String DEFAULT_MOCK_CLASS_NAME = "io.mosip.mock.sdk.impl.SampleSDK";
    private static final String AAR_CLASSES_JAR = "classes.jar";

    public static IBioApiV2 loadBioSDK(Context context, Modality modality, GlobalParamRepository globalParamRepository) {
        try {
            String modalityKey = getModalityKey(modality);
            if (modalityKey == null) {
                Log.e(TAG, "Unsupported modality: " + modality);
                return null;
            }

            String vendorName = null;
            String className = null;
            
            if (globalParamRepository != null) {
                vendorName = globalParamRepository.getBioSDKVendorName(modalityKey);
                if (vendorName != null && !vendorName.isEmpty()) {
                    className = globalParamRepository.getBioSDKProviderClassName(modalityKey, vendorName);
                }
            }
            
            File sdkFile = null;
            
            if (vendorName != null && !vendorName.isEmpty() && className != null && !className.isEmpty()) {
                Log.d(TAG, String.format("Loading BioSDK - Modality: %s, Vendor: %s, Class: %s", 
                        modalityKey, vendorName, className));
                
                sdkFile = loadSdkFromAssets(context, vendorName, modalityKey);
                if (sdkFile != null) {
                    Log.i(TAG, "Successfully loaded vendor SDK: " + vendorName + "-" + modalityKey);
                }
            }
            
            if (sdkFile == null) {
                Log.w(TAG, "Vendor SDK not found or not configured, trying mock SDK (" + MOCK_SDK_FILE_NAME + ")");
                sdkFile = loadMockSdkFromAssets(context, modalityKey, globalParamRepository);
                if (sdkFile != null) {
                    if (globalParamRepository != null) {
                        className = globalParamRepository.getBioSDKProviderClassName(modalityKey, MOCK_VENDOR_NAME);
                    }
                    if (className == null || className.isEmpty()) {
                        className = DEFAULT_MOCK_CLASS_NAME;
                    }
                    Log.i(TAG, "Using mock SDK (" + MOCK_SDK_FILE_NAME + ") for modality " + modalityKey + " with class: " + className);
                } else {
                    Log.w(TAG, "Mock SDK (" + MOCK_SDK_FILE_NAME + ") not found in assets, will use default MatchSDK");
                    return null;
                }
            }

            DexClassLoader classLoader = new DexClassLoader(
                    sdkFile.getAbsolutePath(),
                    context.getCodeCacheDir().getAbsolutePath(),
                    null,
                    context.getClassLoader()
            );

            Class<?> sdkClass = classLoader.loadClass(className);
            Object sdkInstance = sdkClass.newInstance();

            if (sdkInstance instanceof IBioApiV2) {
                Log.i(TAG, "Successfully loaded BioSDK: " + className + " for modality: " + modalityKey);
                return (IBioApiV2) sdkInstance;
            } else {
                Log.e(TAG, "Loaded class does not implement IBioApiV2: " + className);
                return null;
            }

        } catch (ClassNotFoundException e) {
            Log.e(TAG, "Class not found in SDK JAR for modality: " + modality, e);
            return null;
        } catch (InstantiationException e) {
            Log.e(TAG, "Failed to instantiate SDK class for modality: " + modality, e);
            return null;
        } catch (IllegalAccessException e) {
            Log.e(TAG, "Illegal access when instantiating SDK class for modality: " + modality, e);
            return null;
        } catch (Exception e) {
            Log.e(TAG, "Failed to load BioSDK for modality: " + modality, e);
            return null;
        }
    }

    public static IBioApiV2 loadBioSDK(Context context, Modality modality) {
        return loadBioSDK(context, modality, null);
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

    private static File loadSdkFromAssets(Context context, String vendorName, String modalityKey) {
        String jarFileName = String.format("%s-%s.jar", vendorName, modalityKey);
        File jarFile = copyFileFromAssets(context, jarFileName);
        if (jarFile != null) {
            return jarFile;
        }
        
        String aarFileName = String.format("%s-%s.aar", vendorName, modalityKey);
        File aarFile = copyFileFromAssets(context, aarFileName);
        if (aarFile != null) {
            return extractClassesJarFromAar(context, aarFile);
        }
        
        return null;
    }

    private static File loadMockSdkFromAssets(Context context, String modalityKey, GlobalParamRepository globalParamRepository) {
        File aarFile = copyFileFromAssets(context, MOCK_SDK_FILE_NAME);
        
        if (aarFile != null) {
            return extractClassesJarFromAar(context, aarFile);
        }
        
        Log.w(TAG, String.format(
            "Mock SDK (%s) not found. Please copy it to: %s/%s or %s",
            MOCK_SDK_FILE_NAME,
            ASSETS_FOLDER, MOCK_SDK_FILE_NAME,
            MOCK_SDK_FILE_NAME
        ));
        
        return null;
    }

    private static File extractClassesJarFromAar(Context context, File aarFile) {
        File outputJarFile = null;
        ZipFile zipFile = null;
        InputStream inputStream = null;
        FileOutputStream outputStream = null;

        try {
            File biosdkDir = new File(context.getFilesDir(), ASSETS_FOLDER);
            if (!biosdkDir.exists()) {
                boolean created = biosdkDir.mkdirs();
                if (!created) {
                    Log.e(TAG, "Failed to create bioSdk directory");
                    return null;
                }
            }

            String extractedJarName = aarFile.getName().replace(".aar", "-classes.jar");
            outputJarFile = new File(biosdkDir, extractedJarName);

            if (outputJarFile.exists() && outputJarFile.length() > 0) {
                Log.d(TAG, "Extracted classes.jar already exists: " + outputJarFile.getAbsolutePath());
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
            long totalBytes = 0;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
                totalBytes += bytesRead;
            }
            outputStream.flush();

            Log.i(TAG, String.format("Successfully extracted classes.jar (%d bytes) from AAR: %s", 
                    totalBytes, aarFile.getName()));
            return outputJarFile;

        } catch (IOException e) {
            Log.e(TAG, "Failed to extract classes.jar from AAR: " + aarFile.getName(), e);
            if (outputJarFile != null && outputJarFile.exists()) {
                boolean deleted = outputJarFile.delete();
                if (!deleted) {
                    Log.w(TAG, "Failed to delete corrupted extracted JAR file: " + outputJarFile.getAbsolutePath());
                }
            }
            return null;
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
                if (zipFile != null) {
                    zipFile.close();
                }
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
            if (!biosdkDir.exists()) {
                boolean created = biosdkDir.mkdirs();
                if (!created) {
                    Log.e(TAG, "Failed to create bioSdk directory");
                    return null;
                }
            }

            outputFile = new File(biosdkDir, fileName);

            if (outputFile.exists() && outputFile.length() > 0) {
                Log.d(TAG, "File already exists: " + outputFile.getAbsolutePath());
                return outputFile;
            }

            String assetPath = ASSETS_FOLDER + "/" + fileName;
            try {
                inputStream = context.getAssets().open(assetPath);
                Log.d(TAG, "Found file in assets/biosdk/: " + assetPath);
            } catch (IOException e) {
                Log.d(TAG, "File not found in " + assetPath + ", trying root assets folder");
                try {
                    inputStream = context.getAssets().open(fileName);
                    Log.d(TAG, "Found file in root assets: " + fileName);
                } catch (IOException e2) {
                    Log.w(TAG, "File not found in assets: " + fileName, e2);
                    return null;
                }
            }

            outputStream = new FileOutputStream(outputFile);
            byte[] buffer = new byte[8192];
            int bytesRead;
            long totalBytes = 0;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
                totalBytes += bytesRead;
            }
            outputStream.flush();

            Log.i(TAG, String.format("Successfully copied file (%d bytes) to: %s", 
                    totalBytes, outputFile.getAbsolutePath()));
            return outputFile;

        } catch (IOException e) {
            Log.e(TAG, "Failed to copy file from assets: " + fileName, e);
            if (outputFile != null && outputFile.exists()) {
                boolean deleted = outputFile.delete();
                if (!deleted) {
                    Log.w(TAG, "Failed to delete corrupted file: " + outputFile.getAbsolutePath());
                }
            }
            return null;
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                Log.e(TAG, "Error closing streams", e);
            }
        }
    }
}

