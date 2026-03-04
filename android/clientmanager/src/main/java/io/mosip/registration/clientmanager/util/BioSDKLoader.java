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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import dalvik.system.DexClassLoader;
import io.mosip.kernel.biometrics.spi.IBioApiV2;
import io.mosip.registration.clientmanager.constant.Modality;
import io.mosip.registration.clientmanager.repository.GlobalParamRepository;

public class BioSdkLoader {

    private static final String TAG = BioSdkLoader.class.getSimpleName();
    private static final String ASSETS_FOLDER = "biosdk";
    private static final String DEX_ENTRY_NAME = "classes.dex";

    /**
     * Loads all configured SDK providers for the given modality. Tries each class name against
     * every DEX/JAR in assets (biosdk folder, then root), so different vendors can come from
     * different SDK files (e.g. vendor1 from sdk_match.jar, vendor2 from sdk_quality.jar).
     * Returns a list in vendor order; failed loads are null at that index.
     */
    public static List<IBioApiV2> loadAllProvidersForModality(Context context, Modality modality,
            GlobalParamRepository globalParamRepository) {
        if (context == null || globalParamRepository == null) return Collections.emptyList();
        String modalityKey = getModalityKey(modality);
        if (modalityKey == null) return Collections.emptyList();

        List<String> classNames = globalParamRepository.getBioSDKProviderClassNames(modalityKey);
        if (classNames == null || classNames.isEmpty()) return Collections.emptyList();

        List<File> sdkFiles = findAllSdkFilesFromAssets(context);
        if (sdkFiles == null || sdkFiles.isEmpty()) {
            Log.w(TAG, "No SDK (.dex or DEX-jar) in assets for modality: " + modalityKey);
            return Collections.emptyList();
        }

        return loadProvidersFromDexFiles(context, sdkFiles, classNames, modalityKey);
    }

    /**
     * Loads the first available BioSDK for the modality (tries configured class names in order).
     * Uses one DexClassLoader. Prefer {@link #loadAllProvidersForModality} when loading multiple vendors.
     */
    public static IBioApiV2 loadBioSdk(Context context, Modality modality, GlobalParamRepository globalParamRepository) {
        List<IBioApiV2> list = loadAllProvidersForModality(context, modality, globalParamRepository);
        for (IBioApiV2 provider : list) {
            if (provider != null) return provider;
        }
        return null;
    }

    /**
     * Tries each class name against each DEX file in order; first DEX that contains the class is used.
     * Enables different vendors to be loaded from different SDK files (e.g. vendor1 from sdk_match.jar, vendor2 from sdk_quality.jar).
     * Caches one DexClassLoader per DEX file. Returns list same size as classNames (null = not found in any DEX).
     */
    private static List<IBioApiV2> loadProvidersFromDexFiles(Context context, List<File> sdkFiles,
            List<String> classNames, String modalityKey) {
        List<IBioApiV2> result = new ArrayList<>(classNames.size());
        for (int i = 0; i < classNames.size(); i++) {
            result.add(null);
        }
        Map<String, DexClassLoader> loaderCache = new HashMap<>();

        for (int i = 0; i < classNames.size(); i++) {
            String cn = classNames.get(i);
            boolean loaded = false;
            for (File sdkFile : sdkFiles) {
                if (!sdkFile.exists() || !sdkFile.canRead() || !isDexFile(sdkFile)) continue;
                String path = sdkFile.getAbsolutePath();
                DexClassLoader classLoader = loaderCache.get(path);
                if (classLoader == null) {
                    try {
                        classLoader = new DexClassLoader(
                                path,
                                context.getCodeCacheDir().getAbsolutePath(),
                                null,
                                context.getClassLoader());
                        loaderCache.put(path, classLoader);
                    } catch (Exception e) {
                        Log.e(TAG, "DexClassLoader failed for " + sdkFile.getName(), e);
                        continue;
                    }
                }
                try {
                    Class<?> sdkClass = classLoader.loadClass(cn);
                    Object instance = sdkClass.getDeclaredConstructor().newInstance();
                    if (instance instanceof IBioApiV2) {
                        result.set(i, (IBioApiV2) instance);
                        Log.i(TAG, "Loaded BioSDK: " + cn + " for modality: " + modalityKey + " from: " + sdkFile.getName());
                        loaded = true;
                        break;
                    } else {
                        Log.w(TAG, "Class does not implement IBioApiV2: " + cn + " in " + sdkFile.getName());
                    }
                } catch (ClassNotFoundException e) {
                    Log.d(TAG, "Class not in " + sdkFile.getName() + ": " + cn);
                } catch (ReflectiveOperationException e) {
                    Log.w(TAG, "Failed to load: " + cn + " from " + sdkFile.getName(), e);
                }
            }
            if (!loaded) {
                Log.d(TAG, "Class not found in any SDK: " + cn);
            }
        }
        return result;
    }

    /**
     * One DexClassLoader, try each class name; return list same size as classNames (null = failed).
     * Used only when a single DEX is available; prefer {@link #loadProvidersFromDexFiles} for multiple SDKs.
     */
    private static List<IBioApiV2> loadFromDexFile(Context context, File sdkFile, List<String> classNames, String modalityKey) {
        return loadProvidersFromDexFiles(context, Collections.singletonList(sdkFile), classNames, modalityKey);
    }

    private static File findAndValidateSdkFile(Context context, String modalityKey) {
        File sdkFile = findAnySdkFromAssets(context);
        if (sdkFile == null) {
            Log.w(TAG, "No SDK (.dex or DEX-jar) in assets for modality: " + modalityKey);
            return null;
        }
        if (!sdkFile.exists() || !sdkFile.canRead()) {
            Log.e(TAG, "SDK file not accessible: " + sdkFile.getAbsolutePath());
            return null;
        }
        if (sdkFile.length() == 0) {
            Log.e(TAG, "SDK file empty: " + sdkFile.getAbsolutePath());
            return null;
        }
        if (!isDexFile(sdkFile)) {
            Log.e(TAG, "Not a valid DEX file (need .dex or JAR with classes.dex): " + sdkFile.getAbsolutePath());
            return null;
        }
        return sdkFile;
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
