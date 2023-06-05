package io.mosip.registration.clientmanager.repository;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import io.mosip.registration.clientmanager.dao.IdentitySchemaDao;
import io.mosip.registration.clientmanager.dto.http.IdSchemaResponse;
import io.mosip.registration.clientmanager.dto.uispec.FieldSpecDto;
import io.mosip.registration.clientmanager.dto.uispec.ProcessSpecDto;
import io.mosip.registration.clientmanager.entity.IdentitySchema;
import io.mosip.registration.packetmanager.util.HMACUtils2;
import io.mosip.registration.packetmanager.util.JsonUtils;
import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class IdentitySchemaRepository {

    private static final String TAG = IdentitySchemaRepository.class.getSimpleName();

    private IdentitySchemaDao identitySchemaDao;

    public IdentitySchemaRepository(IdentitySchemaDao identitySchemaDao) {
        this.identitySchemaDao = identitySchemaDao;
    }

    public void saveIdentitySchema(Context context, IdSchemaResponse idSchemaResponse) throws Exception {
        IdentitySchema identitySchema = new IdentitySchema(idSchemaResponse.getId());
        identitySchema.setSchemaVersion(idSchemaResponse.getIdVersion());
        String schema = JsonUtils.javaObjectToJsonString(idSchemaResponse);
        File file = new File(context.getFilesDir(), "schema_"+identitySchema.getSchemaVersion());
        try(FileWriter fileWriter = new FileWriter(file)) {
            fileWriter.write(schema);
        }
        identitySchema.setFileLength(file.length());
        identitySchema.setFileHash(HMACUtils2.digestAsPlainText(schema.getBytes(StandardCharsets.UTF_8)));
        identitySchemaDao.insertIdentitySchema(identitySchema);
    }

    public Double getLatestSchemaVersion() {
        IdentitySchema identitySchema = identitySchemaDao.findLatestSchema();
        return (identitySchema == null) ? null : identitySchema.getSchemaVersion();
    }

    public String getSchemaJson(Context context, Double version) throws Exception {
        IdentitySchema identitySchema =  identitySchemaDao.findIdentitySchema(version);


        if(identitySchema == null)
            throw new Exception("Identity schema not found for version : " + version);

        return getIdSchemaResponse(context, identitySchema).getSchemaJson();
    }

    public ProcessSpecDto getNewProcessSpec(Context context, Double version) throws Exception {
        IdentitySchema identitySchema =  identitySchemaDao.findIdentitySchema(version);

        if(identitySchema == null)
            throw new Exception("Identity schema not found for version : " + version);

        return getIdSchemaResponse(context, identitySchema).getNewProcess();
    }

    public List<FieldSpecDto> getAllFieldSpec(Context context, Double version) throws Exception {
        List<FieldSpecDto> schemaFields = new ArrayList<>();
        ProcessSpecDto processSpec = getNewProcessSpec(context, version);

        if (processSpec == null)
            throw new Exception("Process spec not found for version : " + version);

        processSpec.getScreens().forEach(screen -> {
            schemaFields.addAll(screen.getFields());
        });
        return schemaFields;
    }

    private IdSchemaResponse getIdSchemaResponse(Context context, IdentitySchema identitySchema) throws Exception {
        File file = new File(context.getFilesDir(), "schema_"+identitySchema.getSchemaVersion());
        if(file.length() != identitySchema.getFileLength())
            throw new Exception("Schema file is tampered");

        try(FileReader fileReader = new FileReader(file)) {
            String content = IOUtils.toString(fileReader);
            String hash = HMACUtils2.digestAsPlainText(content.getBytes(StandardCharsets.UTF_8));
            if(!hash.equalsIgnoreCase(identitySchema.getFileHash()))
                throw new Exception("Schema file is tampered");

            return JsonUtils.jsonStringToJavaObject(content, IdSchemaResponse.class);
        } catch (IOException e) {
            Log.e(TAG, "Failed to get identity schema", e);
        }
        throw new Exception("Failed to load Identity schema for version : " + identitySchema.getSchemaVersion());
    }
}
