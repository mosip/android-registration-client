package io.mosip.registration.clientmanager.service.packet;

import android.content.Context;
import android.os.Build;
import android.os.FileUtils;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.file.FileSystemNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import io.mosip.registration.clientmanager.dto.objectstore.ObjectDto;
import io.mosip.registration.clientmanager.spi.packet.ObjectStoreAdapter;
import io.mosip.registration.clientmanager.util.ConfigService;
import io.mosip.registration.clientmanager.util.EncryptionHelper;
import io.mosip.registration.clientmanager.util.ObjectStoreUtil;

public class PosixAdapter implements ObjectStoreAdapter {

    private static final String TAG = PosixAdapter.class.getSimpleName();
    private static final String SEPARATOR = "/";
    private static final String ZIP = ".zip";
    private static final String JSON = ".json";
    private static final String TAGS = "_tags";
    private static Context appContext;

    //@Autowired
    private ObjectMapper objectMapper;


    //@Value("${object.store.base.location:home}")
    private String BASE_LOCATION;

    //@Autowired
    private EncryptionHelper helper;

    public void PosixAdapter(Context appContext) {
        Log.i(TAG, "PosixAdapter: Constructor call successful");
        try {
            initPosixAdapterService(appContext);
            helper = new EncryptionHelper(appContext);
        } catch (Exception e) {
            Log.e(TAG, "PosixAdapter: Failed Initialization", e);
        }
    }

    private void initPosixAdapterService(Context context) {
        this.appContext = context;
        BASE_LOCATION = ConfigService.getProperty("object.store.base.location", context);
        Log.i(TAG, "initLocalClientCryptoService: Initialization call successful");
    }


    //Object store Adapter impl methods
    @Override
    public InputStream getObject(String account, String container, String source, String process, String objectName) {
        try {
            File accountLoc = new File(BASE_LOCATION + SEPARATOR + account);
            if (!accountLoc.exists())
                return null;
            File containerZip = new File(accountLoc.getPath() + SEPARATOR + container + ZIP);
            if (!containerZip.exists())
                throw new FileNotFoundException("containerZip File Not found");
//                throw new FileNotFoundInDestinationException(KhazanaErrorCodes.CONTAINER_NOT_PRESENT_IN_DESTINATION.getErrorCode(),
//                        KhazanaErrorCodes.CONTAINER_NOT_PRESENT_IN_DESTINATION.getErrorMessage());

            InputStream ios = new FileInputStream(containerZip);
            Map<ZipEntry, ByteArrayOutputStream> entries = getAllExistingEntries(ios);

            Optional<ZipEntry> zipEntry = entries.keySet().stream().filter(e ->
                    e.getName().contains(ObjectStoreUtil.getName(source, process, objectName) + ZIP)).findAny();

            if (zipEntry.isPresent() && zipEntry.get() != null)
                return new ByteArrayInputStream(entries.get(zipEntry.get()).toByteArray());

        } catch (FileNotFoundException e) {
            Log.e(TAG, "exception occurred to get object for id - " + container + ":::" + e.getMessage());
            //LOGGER.error("exception occured to get object for id - " + container, e);
        } catch (IOException e) {
            Log.e(TAG, "exception occurred to get object for id - " + container + ":::" + e.getMessage());
            //LOGGER.error("exception occured to get object for id - " + container, e);
        }
        return null;
    }

    @Override
    public boolean exists(String account, String container, String source, String process, String objectName) {
        return getObject(account, container, source, process, objectName) != null;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public boolean putObject(String account, String container, String source, String process, String objectName, InputStream data) {
        try {
            createContainerZipWithSubpacket(account, container, source, process, objectName + ZIP, data);
            return true;
        } catch (Exception e) {
            Log.e(TAG, "exception occurred. Will create a new connection. ::" + e.getMessage());
            //LOGGER.error("exception occured. Will create a new connection.", e);
        }
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public Map<String, Object> addObjectMetaData(String account, String container, String source, String process, String objectName, Map<String, Object> metadata) {
        try {
            JSONObject jsonObject = objectMetadata(account, container, source, process, objectName, metadata);
            createContainerZipWithSubpacket(account, container, source, process, objectName + JSON,
                    new ByteArrayInputStream(jsonObject.toString().getBytes()));
        } catch (//io.mosip.kernel.core.exception.IOException |
                IOException e) {
            Log.e(TAG, "exception occured to add metadata for id - " + container);
            //LOGGER.error("exception occured to add metadata for id - " + container, e);
        } catch (Exception e1) {
            Log.e(TAG, "exception occured to add metadata for id - " + container);
            //LOGGER.error("exception occured to add metadata for id - " + container, e);
        }

        return metadata;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public Map<String, Object> addObjectMetaData(String account, String container, String source, String process, String objectName, String key, String value) {
        try {
            Map<String, Object> metaMap = new HashMap<>();
            metaMap.put(key, value);
            JSONObject jsonObject = objectMetadata(account, container, source, process, objectName, metaMap);
            createContainerZipWithSubpacket(account, container, source, process, objectName + JSON, new ByteArrayInputStream(jsonObject.toString().getBytes()));
            return metaMap;
//        } catch (io.mosip.kernel.core.exception.IOException e) {
//            //LOGGER.error("exception occured to add metadata for id - " + container, e);
        } catch (IOException e) {
            Log.e(TAG, "exception occured to add metadata for id - " + container);
            //LOGGER.error("exception occured to add metadata for id - " + container, e);
        }
        return null;
    }

    @Override
    public Map<String, Object> getMetaData(String account, String container, String source, String process, String objectName) {
        Map<String, Object> metaMap = null;
        try {
            File accountLoc = new File(BASE_LOCATION + SEPARATOR + account);
            if (!accountLoc.exists())
                return null;
            File containerZip = new File(accountLoc.getPath() + SEPARATOR + container + ZIP);
            if (!containerZip.exists())
                throw new RuntimeException("Container not present in destination.");
//                throw new FileNotFoundInDestinationException(KhazanaErrorCodes.CONTAINER_NOT_PRESENT_IN_DESTINATION.getErrorCode(),
//                        KhazanaErrorCodes.CONTAINER_NOT_PRESENT_IN_DESTINATION.getErrorMessage());

            InputStream ios = new FileInputStream(containerZip);
            Map<ZipEntry, ByteArrayOutputStream> entries = getAllExistingEntries(ios);

            Optional<ZipEntry> zipEntry = entries.keySet().stream().filter(e -> e.getName().contains(objectName + JSON)).findAny();

            if (zipEntry.isPresent() && zipEntry.get() != null) {
                String string = entries.get(zipEntry.get()).toString();
                JSONObject jsonObject = objectMapper.readValue(objectMapper.writeValueAsString(string), JSONObject.class);
                metaMap = objectMapper.readValue(jsonObject.toString(), HashMap.class);
            }
        } catch (FileSystemNotFoundException e) {
            Log.e(TAG, "exception occured. Will create a new connection");
            //LOGGER.error("exception occured. Will create a new connection.", e);
            throw e;
        } catch (IOException e) {
            Log.e(TAG, "exception occured to add metadata for id - " + container);
            //LOGGER.error("exception occured to get metadata for id - " + container, e);
        }
        return metaMap;
    }

    @Override
    public Integer incMetadata(String account, String container, String source, String process, String objectName, String metaDataKey) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public Integer decMetadata(String account, String container, String source, String process, String objectName, String metaDataKey) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public boolean deleteObject(String account, String container, String source, String process, String objectName) {
        return true;
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
//                throw new FileNotFoundInDestinationException(KhazanaErrorCodes.CONTAINER_NOT_PRESENT_IN_DESTINATION.getErrorCode(),
            //      KhazanaErrorCodes.CONTAINER_NOT_PRESENT_IN_DESTINATION.getErrorMessage());

            boolean deleted = deleteFile(containerZip);
            if (!deleted) {
                Log.e(TAG, "Not able to remove Container File");
            }
            //FileUtils.forceDelete(containerZip);
            return true;
        } catch (Exception e) {
            Log.e(TAG, "exception occured while packing");
            //LOGGER.error("exception occured while packing.", e);
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
//                throw new FileNotFoundInDestinationException(KhazanaErrorCodes.CONTAINER_NOT_PRESENT_IN_DESTINATION.getErrorCode(),
//                        KhazanaErrorCodes.CONTAINER_NOT_PRESENT_IN_DESTINATION.getErrorMessage());

            InputStream ios = new FileInputStream(containerZip);
            outputStream.write(ios.read());
            byte[] encryptedPacket = helper.encrypt(refId, outputStream.toByteArray());
            outputStream.flush();
            FileUtils.copy(new ByteArrayInputStream(encryptedPacket), outputStream);
            return encryptedPacket != null;
        } catch (Exception e) {
            Log.e(TAG, "exception occured while packing");
            return false;
        }
    }

    @Override
    public List<ObjectDto> getAllObjects(String account, String container) {
        return null;
    }

    @Override
    public Map<String, String> addTags(String account, String container, Map<String, String> tags) {
        try {
            JSONObject jsonObject = containterTagging(account, container, tags);
            createContainerWithTagging(account, container, new ByteArrayInputStream(jsonObject.toString().getBytes()));
        } catch (Exception e) {
            Log.e(TAG, "exception occured while packing");
            //LOGGER.error("exception occured to add tags for id - " + container, e);
        }
        return tags;
    }

    @Override
    public Map<String, String> getTags(String account, String container) {
        Map<String, String> metaMap = new HashMap<String, String>();
        File accountLocation = new File(BASE_LOCATION + SEPARATOR + account);
        if (!accountLocation.exists())
            accountLocation.mkdir();
        File tagFile = new File(accountLocation.getPath() + SEPARATOR + container + TAGS + JSON);
        try {
            if (tagFile.createNewFile()) {
                Log.e(TAG, " tags file not yet present for  id - " + container);
                //LOGGER.info(" tags file not yet present for  id - " + container);
            } else {
                InputStream inputstream = new FileInputStream(tagFile);
                BufferedReader inputStreamReader = new BufferedReader(new InputStreamReader(inputstream, "UTF-8"));
                StringBuilder responseStrBuilder = new StringBuilder();

                String inputTags;
                while ((inputTags = inputStreamReader.readLine()) != null)
                    responseStrBuilder.append(inputTags);

                inputStreamReader.close();
                JSONObject jsonObject = objectMapper.readValue(objectMapper.writeValueAsString(responseStrBuilder.toString()),
                        JSONObject.class);
                metaMap = objectMapper.readValue(jsonObject.toString(), HashMap.class);
            }
        } catch (Exception e) {
            Log.e(TAG, "exception occured to get tags for id - " + container);
        }
        return metaMap;
    }

    @Override
    public void deleteTags(String account, String container, List<String> tags) {

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
    private void createContainerZipWithSubpacket(String account, String container, String source, String process, String objectName, InputStream data)
            throws //io.mosip.kernel.core.exception.IOException,
            IOException {
        File accountLocation = new File(BASE_LOCATION + SEPARATOR + account);
        if (!accountLocation.exists())
            accountLocation.mkdir();
        File containerZip = new File(accountLocation.getPath() + SEPARATOR + container + ZIP);
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
                        Log.e(TAG, "exception occurred. Will create a new connection. :::" + e1.getMessage());
                        //LOGGER.error("exception occurred. Will create a new connection.", e1);
                    }
                });


                byteArrayOutStream.write(data.read());

                addEntryToZip(String.format(objectName),
                        byteArrayOutStream.toByteArray(), packetZip, source, process);
            } finally {
                byteArrayOutStream.close();
            }
        }

        try (FileOutputStream fileOutputStream = new FileOutputStream(containerZip)) {
            InputStream inputStream = new ByteArrayInputStream(out.toByteArray());
            FileUtils.copy(inputStream, fileOutputStream);
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
            //LOGGER.error("exception occured. Will create a new connection.", e);
        }
    }

    private JSONObject objectMetadata(String account, String container, String source, String process,
                                      String objectName, Map<String, Object> metadata) {
        JSONObject jsonObject = new JSONObject(metadata);
        Map<String, Object> existingMetaData = getMetaData(account, container, source, process, objectName);
        //if (!CollectionUtils.isEmpty(existingMetaData))
        if (existingMetaData != null && !existingMetaData.isEmpty())
            existingMetaData.entrySet().forEach(entry -> {
                try {
                    jsonObject.put(entry.getKey(), entry.getValue());
                } catch (JSONException e) {
                    Log.e(TAG, "exception occured to add metadata for id - " + container);
                    //LOGGER.error("exception occured to add metadata for id - " + container, e);
                }
            });
        return jsonObject;
    }

    private JSONObject containterTagging(String account, String container, Map<String, String> tags) {
        JSONObject jsonObject = new JSONObject(tags);
        Map<String, String> existingTags = getTags(account, container);
        //if (!CollectionUtils.isEmpty(existingTags))
        if (existingTags != null && !existingTags.isEmpty())
            existingTags.entrySet().forEach(entry -> {
                try {
                    jsonObject.put(entry.getKey(), entry.getValue());
                } catch (JSONException e) {
                    Log.e(TAG, "exception occured to add metadata for id - " + container);
                    //LOGGER.error("exception occured to add tags for id - " + container, e);
                }
            });
        return jsonObject;
    }

    private void createContainerWithTagging(String account, String container, InputStream data) throws IOException {

        File accountLocation = new File(BASE_LOCATION + SEPARATOR + account);
        if (!accountLocation.exists())
            accountLocation.mkdir();
        File tagFile = new File(accountLocation.getPath() + SEPARATOR + container + TAGS + JSON);
        OutputStream outStream = new FileOutputStream(tagFile);
        outStream.write(data.read());
        outStream.close();

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
