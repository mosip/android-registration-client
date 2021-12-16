package io.mosip.registration.clientmanager.service.packet;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.FileUtils;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystemNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.mosip.registration.clientmanager.spi.packet.ObjectStoreAdapter;
import io.mosip.registration.clientmanager.util.EncryptionHelper;
import io.mosip.registration.clientmanager.util.ObjectStoreUtil;

@Singleton
public class PosixAdapter implements ObjectStoreAdapter {

    private static final String TAG = PosixAdapter.class.getSimpleName();
    private static final String SEPARATOR = "/";
    private static final String ZIP = ".zip";
    private static final String JSON = ".json";
    private static final String TAGS = "_tags";
    private static Context appContext;

    private ObjectMapper objectMapper;
    private String BASE_LOCATION;
    private EncryptionHelper helper;

    @Inject
    public PosixAdapter(Context appContext) {
        Log.i(TAG, "PosixAdapter: Constructor call successful");
        try {
            initPosixAdapterService(appContext);
        } catch (Exception e) {
            Log.e(TAG, "PosixAdapter: Failed Initialization", e);
        }
    }

    private void initPosixAdapterService(Context context) {
        this.appContext = context;
        helper = new EncryptionHelper(appContext);
        objectMapper = new ObjectMapper();

        //TODO Take base location from Config

        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            BASE_LOCATION = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getPath();
        } else {
            Log.e(TAG, "External Storage not mounted");
        }
        Log.i(TAG, "initLocalClientCryptoService: Initialization call successful");
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public boolean putObject(String account, String container, String source, String process, String objectName, InputStream data) {
        try {
            createContainerZipWithSubPacket(account, container, source, process, objectName + ZIP, data);
            return true;
        } catch (Exception e) {
            Log.e(TAG, "exception occurred. Will create a new connection. ::" + e.getMessage());
        }
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public Map<String, Object> addObjectMetaData(String account, String container, String source, String process, String objectName, Map<String, Object> metadata) {
        try {
            JSONObject jsonObject = objectMetadata(account, container, source, process, objectName, metadata);
            createContainerZipWithSubPacket(account, container, source, process, objectName + JSON,
                    new ByteArrayInputStream(jsonObject.toString().getBytes()));
            return metadata;
        } catch (
                IOException e) {
            Log.e(TAG, "exception occurred to add metadata for id - " + container);
        } catch (Exception e1) {
            Log.e(TAG, "exception occurred to add metadata for id - " + container);
        }
        return null;
    }

    //@Override
    private Map<String, Object> getMetaData(String account, String container, String source, String process, String objectName) {
        Map<String, Object> metaMap = null;
        try {
            File accountLoc = new File(BASE_LOCATION + SEPARATOR + account);
            if (!accountLoc.exists())
                return null;
            File containerZip = new File(accountLoc.getPath() + SEPARATOR + container + ZIP);
            if (!containerZip.exists())
                throw new RuntimeException("Container not present in destination.");

            InputStream ios = new FileInputStream(containerZip);
            Map<ZipEntry, ByteArrayOutputStream> entries = getAllExistingEntries(ios);

            Optional<ZipEntry> zipEntry = entries.keySet().stream().filter(e -> e.getName().contains(objectName + JSON)).findAny();

            if (zipEntry.isPresent() && zipEntry.get() != null) {
                String string = entries.get(zipEntry.get()).toString();
                JSONObject jsonObject = objectMapper.readValue(objectMapper.writeValueAsString(string), JSONObject.class);
                metaMap = objectMapper.readValue(jsonObject.toString(), HashMap.class);
            }
        } catch (FileSystemNotFoundException e) {
            Log.e(TAG, "exception occurred. Will create a new connection");
            throw e;
        } catch (IOException e) {
            Log.e(TAG, "exception occurred to add metadata for id - " + container);
        }
        return metaMap;
    }

    @Override
    public boolean removeContainer(String account, String container, String source, String process) {
        try {
            File accountLoc = new File(BASE_LOCATION + SEPARATOR + account);
            if (!accountLoc.exists())
                return false;
            File containerZip = new File(accountLoc.getPath() + SEPARATOR + container + ZIP);
            if (!containerZip.exists())
                throw new RuntimeException("Files not found in destinations");

            boolean deleted = deleteFile(containerZip);
            if (!deleted) {
                Log.e(TAG, "Not able to remove Container File");
            }
            return true;
        } catch (Exception e) {
            Log.e(TAG, "exception occured while packing");
            return false;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public boolean pack(String account, String container, String source, String process, String refId) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            File accountLoc = new File(BASE_LOCATION + SEPARATOR + account);
            if (!accountLoc.exists())
                return false;
            File containerZip = new File(accountLoc.getPath() + SEPARATOR + container + ZIP);

            if (!containerZip.exists())
                throw new RuntimeException("Files not found in destinations");

            InputStream ios = new FileInputStream(containerZip);
            outputStream.write(ios.read());
            //TODO Encrypt packet
            byte[] encryptedPacket = helper.encrypt(refId, outputStream.toByteArray());

            outputStream.flush();
            FileUtils.copy(new ByteArrayInputStream(encryptedPacket), outputStream);
            return encryptedPacket != null;
        } catch (Exception e) {
            Log.e(TAG, "exception occurred while packing");
            return false;
        }
    }

    //Private methods

    private Map<ZipEntry, ByteArrayOutputStream> getAllExistingEntries(InputStream packetStream) throws IOException {
        Map<ZipEntry, ByteArrayOutputStream> entries = new HashMap<>();
        try (ZipInputStream zis = new ZipInputStream(packetStream)) {
            ZipEntry ze = zis.getNextEntry();
            while (ze != null) {
                int len;
                byte[] buffer = new byte[2048];
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                while ((len = zis.read(buffer)) > 0) {
                    out.write(buffer, 0, len);
                }
                entries.put(ze, out);
                zis.closeEntry();
                ze = zis.getNextEntry();
                out.close();
            }
            zis.closeEntry();
        } finally {
            packetStream.close();
        }
        return entries;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void createContainerZipWithSubPacket(String account, String container, String source, String process, String objectName, InputStream data)
            throws
            IOException {

        String state = Environment.getExternalStorageState();
        //external storage availability check
        if (!Environment.MEDIA_MOUNTED.equals(state)) {
            return;
        }

        new File(BASE_LOCATION + SEPARATOR + account).mkdir();

        File containerZip = new File(BASE_LOCATION + SEPARATOR + account, container + ZIP);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        if (!containerZip.exists()) {

            ByteArrayOutputStream byteArrayOutStream = new ByteArrayOutputStream();
            try (ZipOutputStream packetZip = new ZipOutputStream(new BufferedOutputStream(out))) {
                byteArrayOutStream.write(data.read());
                addEntryToZip(String.format(objectName),
                        byteArrayOutStream.toByteArray(), packetZip, source, process);
            } finally {
                byteArrayOutStream.close();
            }
        } else {
            InputStream ios = new FileInputStream(containerZip);
            Map<ZipEntry, ByteArrayOutputStream> entries = getAllExistingEntries(ios);
            ByteArrayOutputStream byteArrayOutStream = new ByteArrayOutputStream();
            try (ZipOutputStream packetZip = new ZipOutputStream(out)) {
                entries.entrySet().forEach(e -> {
                    try {
                        packetZip.putNextEntry(e.getKey());
                        packetZip.write(e.getValue().toByteArray());
                    } catch (IOException e1) {
                        Log.e(TAG, "exception occurred. Create a new zip. :::" + e1.getMessage());
                    }
                });


                byteArrayOutStream.write(data.read());

                addEntryToZip(String.format(objectName),
                        byteArrayOutStream.toByteArray(), packetZip, source, process);
            } finally {
                byteArrayOutStream.close();
            }
        }

        FileOutputStream fileOutputStream = null;
        try {
            containerZip.createNewFile();
            fileOutputStream = new FileOutputStream(containerZip);
            fileOutputStream.write(out.toByteArray());
        } catch (Exception ex) {
            Log.e(TAG, "createContainerZipWithSubPacket : Exception while writing a file");
        } finally {
            fileOutputStream.flush();
            fileOutputStream.close();
        }
    }

    private void addEntryToZip(String fileName, byte[] data, ZipOutputStream zipOutputStream, String source, String process) {
        try {
            if (data != null) {
                ZipEntry zipEntry = new ZipEntry(ObjectStoreUtil.getName(source, process, fileName));
                zipOutputStream.putNextEntry(zipEntry);
                zipOutputStream.write(data);
            }
        } catch (IOException e) {
            Log.e(TAG, "exception occurred. Will create a new connection. :: " + e.getMessage());
        }
    }

    private JSONObject objectMetadata(String account, String container, String source, String process,
                                      String objectName, Map<String, Object> metadata) {
        JSONObject jsonObject = new JSONObject(metadata);
        Map<String, Object> existingMetaData = getMetaData(account, container, source, process, objectName);

        if (existingMetaData != null && !existingMetaData.isEmpty())
            existingMetaData.entrySet().forEach(entry -> {
                try {
                    jsonObject.put(entry.getKey(), entry.getValue());
                } catch (JSONException e) {
                    Log.e(TAG, "exception occurred to add metadata for id - " + container);
                }
            });
        return jsonObject;
    }

    private boolean deleteFile(File file) throws IOException {
        boolean deleted = file.delete();
        if (!deleted) {
            boolean deleted2 = file.getCanonicalFile().delete();
            if (!deleted2) {
                boolean deleted3 = appContext.deleteFile(file.getName());
                if (!deleted3) {
                    return false;
                }
            }
        }
        return true;
    }
}