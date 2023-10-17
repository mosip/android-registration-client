package io.mosip.registration.clientmanager.repository;

import android.content.Context;
import android.util.Log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.registration.clientmanager.constant.RegistrationConstants;
import io.mosip.registration.clientmanager.dao.IdentitySchemaDao;
import io.mosip.registration.clientmanager.dto.AgeGroupConfigDto;
import io.mosip.registration.clientmanager.dto.http.IdSchemaResponse;
import io.mosip.registration.clientmanager.dto.uispec.ConditionalBioAttrDto;
import io.mosip.registration.clientmanager.dto.uispec.FieldSpecDto;
import io.mosip.registration.clientmanager.dto.uispec.ProcessSpecDto;
import io.mosip.registration.clientmanager.dto.uispec.RequiredDto;
import io.mosip.registration.clientmanager.dto.uispec.ScreenSpecDto;
import io.mosip.registration.clientmanager.entity.IdentitySchema;
import io.mosip.registration.packetmanager.util.HMACUtils2;
import io.mosip.registration.packetmanager.util.JsonUtils;
import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IdentitySchemaRepository {

    private static final String TAG = IdentitySchemaRepository.class.getSimpleName();

    private IdentitySchemaDao identitySchemaDao;
    private GlobalParamRepository globalParamRepository;

    public IdentitySchemaRepository(GlobalParamRepository globalParamRepository, IdentitySchemaDao identitySchemaDao) {
        this.globalParamRepository = globalParamRepository;
        this.identitySchemaDao = identitySchemaDao;
    }

    public void saveIdentitySchema(Context context, IdSchemaResponse idSchemaResponse) throws Exception {
        IdentitySchema identitySchema = new IdentitySchema(idSchemaResponse.getId());
        identitySchema.setSchemaVersion(idSchemaResponse.getIdVersion());
        if (idSchemaResponse.getSchema() != null && idSchemaResponse.getNewProcess() == null) {
            Log.i(TAG, "Entering into migration process");
            try {
                idSchemaResponse = migrate115UiSpecToLTSProcessSpec(idSchemaResponse);
            } catch (Exception e){
                Log.e(TAG, "Failed to complete migration process", e);
            }
        }
        String schema = JsonUtils.javaObjectToJsonString(idSchemaResponse);
        Log.i(TAG, "Schema path: " + context.getFilesDir());
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

    //Handle label change
    private IdSchemaResponse migrate115UiSpecToLTSProcessSpec(IdSchemaResponse idSchemaResponse) {
        try {
            List<FieldSpecDto> schema = idSchemaResponse.getSchema();

            String primaryLanguage = "eng";//this.globalParamRepository.getCachedStringGlobalParam(RegistrationConstants.PRIMARY_LANGUAGE);
            String secondaryLanguage = "fra";//this.globalParamRepository.getCachedStringGlobalParam(RegistrationConstants.SECONDARY_LANGUAGE);
            List<String> allowedBioAttributes = new ArrayList<>(Arrays.asList(this.globalParamRepository.getCachedStringGlobalParam(RegistrationConstants.ALLOWED_BIO_ATTRIBUTES).split(",")));

            HashMap<String, AgeGroupConfigDto> ageGroupAttributes = new HashMap<>();
            List<String> ageGroupRequiresGuardianAuth = new ArrayList<>();
            JSONObject jsonObject = new JSONObject(this.globalParamRepository.getCachedStringGlobalParam(RegistrationConstants.AGEGROUP_CONFIG));
            jsonObject.keys().forEachRemaining(key -> {
                ObjectMapper objectMapper = new ObjectMapper();
                try {
                    Log.i(TAG, "Agegroup Config: "+jsonObject.get(key).toString());
                    AgeGroupConfigDto ageGroupConfigDto = objectMapper.readValue(jsonObject.get(key).toString(), AgeGroupConfigDto.class);
                    ageGroupAttributes.put(key, ageGroupConfigDto);
                    if (ageGroupConfigDto.getIsGuardianAuthRequired()) {
                        ageGroupRequiresGuardianAuth.add(key);
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Failed to parse agegroup config", e);
                }
            });
            Log.i(TAG, "Agegroup config parsing completed");

            HashMap<String, String> processLabel = new HashMap<>();
            processLabel.put(primaryLanguage, "New Registration");
            processLabel.put(secondaryLanguage, "Nouvelle inscription"); //TODO set labels from properties

            ProcessSpecDto newProcess = new ProcessSpecDto("NEW", 1, "NEW", true, processLabel, processLabel, "NewReg.png", null, null);

            List<FieldSpecDto> demographics = new ArrayList<>();
            List<FieldSpecDto> documents = new ArrayList<>();
            List<FieldSpecDto> biometrics = new ArrayList<>();
            for (FieldSpecDto field : schema) {
                if (field.getInputRequired()) {
                    Map<String, String> labels = field.getLabel();
                    labels.put(primaryLanguage, labels.get("primary"));
                    if (labels.containsKey("secondary"))
                        labels.put(secondaryLanguage, labels.get("secondary"));
                    field.setLabel(labels);
                    if (field.getVisible() != null) {
                        field.setVisible(changeMvelExpression(field.getVisible()));
                    }
                    if (field.getRequiredOn() != null && !field.getRequiredOn().isEmpty()) {
                        List<RequiredDto> requiredOnExpressions = field.getRequiredOn();
                        for (int i=0; i < requiredOnExpressions.size(); i++) {
                            RequiredDto requiredDto = changeMvelExpression(requiredOnExpressions.get(i));
                            requiredOnExpressions.set(i, requiredDto);
                        }
                        field.setRequiredOn(requiredOnExpressions);
                    }
                    if (field.getType().equalsIgnoreCase("documentType")) {
                        documents.add(field);
                    } else if (field.getType().equalsIgnoreCase("biometricsType")) {
                        if (field.getId().equalsIgnoreCase(this.globalParamRepository.getCachedStringGlobalParam(RegistrationConstants.INDIVIDUAL_BIOMETRICS_ID))) {
                            List<ConditionalBioAttrDto> conditionalBioAttributes = new ArrayList<>();
                            for (Map.Entry<String, AgeGroupConfigDto> entry : ageGroupAttributes.entrySet()) {
                                if (entry.getValue().getBioAttributes().size() < 13) {
                                    ConditionalBioAttrDto conditionalBioAttribute = new ConditionalBioAttrDto(entry.getKey(), "ALL", String.join(" && ", entry.getValue().getBioAttributes()), entry.getValue().getBioAttributes());
                                    conditionalBioAttributes.add(conditionalBioAttribute);
                                }
                            }
                            field.setConditionalBioAttributes(conditionalBioAttributes);
                            field.setRequired(true);
                            field.setRequiredOn(new ArrayList<>());
                            field.setExceptionPhotoRequired(true);
                            field.setSubType("applicant");
                        } else if (field.getId().equalsIgnoreCase(this.globalParamRepository.getCachedStringGlobalParam(RegistrationConstants.INTRODUCER_BIOMETRICS_ID))) {
                            List<ConditionalBioAttrDto> conditionalBioAttributes = new ArrayList<>();
                            for (String ageGroup : ageGroupRequiresGuardianAuth) {
                                ConditionalBioAttrDto conditionalBioAttribute = new ConditionalBioAttrDto(ageGroup, "ALL", String.join(" || ", allowedBioAttributes), allowedBioAttributes);
                                conditionalBioAttributes.add(conditionalBioAttribute);
                            }
                            field.setConditionalBioAttributes(conditionalBioAttributes);
                            field.setRequired(false);
                            field.setSubType("introducer");
                            List<RequiredDto> requiredOn = new ArrayList<>();
                            List<String> expressions = new ArrayList<>();
                            for (Map.Entry<String, AgeGroupConfigDto> entry : ageGroupAttributes.entrySet()) {
                                if (entry.getValue().getIsGuardianAuthRequired()) {
                                    expressions.add("identity.get('ageGroup') == '"+entry.getKey()+"'");
                                }
                            }
                            requiredOn.add(new RequiredDto("MVEL", String.join(" || ", expressions)));
                            field.setRequiredOn(requiredOn);
                        }
                        biometrics.add(field);
                    } else {
                        if (field.getFieldType().equalsIgnoreCase("dynamic")) {
                            field.setSubType(field.getId());
                        }
                        demographics.add(field);
                    }
                }
            }
            Log.i(TAG, "Parsing fields completed");

            List<ScreenSpecDto> screens = new ArrayList<>();
            //Consent Screen
            Map<String, String> consentScreenLabel = new HashMap<>();
            consentScreenLabel.put(primaryLanguage, "Consent");
            consentScreenLabel.put(secondaryLanguage, "Consentement");
            List<FieldSpecDto> consentFields = new ArrayList<>();
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonArray = objectMapper.readTree(this.globalParamRepository.getCachedStringGlobalParam(RegistrationConstants.CONSENT_SCREEN_CONTENT));
                for (JsonNode element : jsonArray) {
                    FieldSpecDto field = objectMapper.treeToValue(element, FieldSpecDto.class);
                    consentFields.add(field);
                }
                ScreenSpecDto consentScreen = new ScreenSpecDto("Consent", consentScreenLabel, consentFields, 1);
                screens.add(consentScreen);
            } catch (JsonProcessingException e) {
                Log.e(TAG, "Failed to build consent screen", e);
            }
            Log.i(TAG, "Building consent screen completed");

            //Demographic screen
            Map<String, String> demoScreenLabel = new HashMap<>();
            demoScreenLabel.put(primaryLanguage, "Demographic Details");
            demoScreenLabel.put(secondaryLanguage, "Détails démographiques");
            ScreenSpecDto demographicScreen = new ScreenSpecDto("DemographicDetails", demoScreenLabel, demographics, 2);
            screens.add(demographicScreen);
            Log.i(TAG, "Building demographics screen completed");

            //Documents screen
            Map<String, String> docScreenLabel = new HashMap<>();
            docScreenLabel.put(primaryLanguage, "Document Upload");
            docScreenLabel.put(secondaryLanguage, "Des documents");
            ScreenSpecDto documentsScreen = new ScreenSpecDto("Documents", docScreenLabel, documents, 3);
            screens.add(documentsScreen);
            Log.i(TAG, "Building documents screen completed");

            //Biometrics screen
            Map<String, String> bioScreenLabel = new HashMap<>();
            bioScreenLabel.put(primaryLanguage, "Biometric Details");
            bioScreenLabel.put(secondaryLanguage, "Détails biométriques");
            ScreenSpecDto biometricsScreen = new ScreenSpecDto("BiometricDetails", bioScreenLabel, biometrics, 4);
            screens.add(biometricsScreen);
            Log.i(TAG, "Building biometrics screen completed");

            newProcess.setScreens(screens);
            idSchemaResponse.setNewProcess(newProcess);
        } catch (Exception e) {
            Log.e(TAG, "Failed to migrate 1.1.5 schema to NewProcessSpec", e);
        }
        return idSchemaResponse;
    }

    private RequiredDto changeMvelExpression(RequiredDto requiredDto) {
        String expr = requiredDto.getExpr();
        String infantAgegroupName = this.globalParamRepository.getCachedStringGlobalParam(RegistrationConstants.INFANT_AGEGROUP_NAME);
        expr = expr.replace("identity.?isChild", "identity.get('ageGroup') == '"+infantAgegroupName+"'");
        expr = expr.replace("identity.isChild", "identity.get('ageGroup') == '"+infantAgegroupName+"'");
        requiredDto.setExpr(expr);
        return requiredDto;
    }
}
