package io.mosip.registration.clientmanager.dto.registration;

import android.net.Uri;
import android.util.Log;
import androidx.annotation.NonNull;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.mosip.registration.packetmanager.util.JsonUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

public class RegistrationDto {

    private static final String BIO_KEY = "%s_%s";

    private String rId;
    private String process;
    private Double schemaVersion;
    private LocalDateTime dateTime;
    private List<String> selectedLanguages;
    private ConsentDto consentDto;
    private Map<String, Object> demographics;
    private Map<String, DocumentDto> documents;
    private Map<String, BiometricsDto> biometrics;
    private OperatorDto maker;
    private OperatorDto reviewer;

    public RegistrationDto(@NonNull String rid, @NonNull String process, @NonNull Double schemaVersion,
                           @NonNull List<String> languages) {
        this.rId = rid;
        this.dateTime = LocalDateTime.now(ZoneOffset.UTC);
        this.process = process;
        this.schemaVersion = schemaVersion;
        this.selectedLanguages = languages;
        this.demographics = new HashMap<>();
        this.documents = new HashMap<>();
        this.biometrics = new HashMap<>();
    }

    public void setMakerDetails() {

    }

    public void setReviewerDetails() {

    }

    public void addDemographicField(String fieldId, String value) {
        if(value != null && !value.trim().isEmpty())
            this.demographics.put(fieldId, value);
    }

    public void addDemographicField(String fieldId, GenericDto genericDto) {
        if(genericDto == null || genericDto.getName().trim().isEmpty())
            return;

        List<GenericDto> list = (List<GenericDto>) this.demographics.getOrDefault(fieldId, new ArrayList<GenericDto>());
        list.add(genericDto);
        this.demographics.put(fieldId, list);
    }

    public void removeDemographicField(String fieldId) {
        this.demographics.remove(fieldId);
    }

    public void setConsent(String consentText) {
        this.consentDto = new ConsentDto(consentText, LocalDateTime.now(ZoneOffset.UTC));
    }

    public void addDocument(String fieldId, String docType, byte[] bytes) {
        this.documents.put(fieldId, new DocumentDto(docType, "pdf", "", "path", bytes));
    }

    public void removeDocumentField(String fieldId) {
        this.documents.remove(fieldId);
    }

    public void addBiometric(String fieldId, String attribute, BiometricsDto biometricsDto) {
        this.biometrics.put(String.format(BIO_KEY, fieldId, attribute), biometricsDto);
    }

    public void removeBiometricField(String fieldId) {
        this.biometrics.remove(fieldId);
    }

    public Set<Map.Entry<String, Object>> getAllDemographicFields() {
        return this.demographics.entrySet();
    }

    public Set<Map.Entry<String, DocumentDto>> getAllDocumentFields() {
        return this.documents.entrySet();
    }

    public Set<Map.Entry<String, BiometricsDto>> getAllBiometricFields() {
        return this.biometrics.entrySet();
    }

    public String getRId() {
        return rId;
    }

    public String getProcess() {
        return process;
    }

    public Double getSchemaVersion() {
        return schemaVersion;
    }

    public List<String> getSelectedLanguages() {
        return selectedLanguages;
    }

    public void cleanup() {
        this.demographics.clear();
        this.documents.clear();
        this.biometrics.clear();
    }

    @Override
    public String toString() {
        try {
           String demographicJsonString = JsonUtils.javaObjectToJsonString(demographics);
            JSONObject jsonObject = new JSONObject(demographicJsonString);
            for (Map.Entry<String, DocumentDto> e : this.documents.entrySet()) {
                try {
                    jsonObject.put(e.getKey(), e.getValue().getType());
                } catch (JSONException ex) {
                    Log.e("", "failed to get document value", ex);
                }
            }
            return jsonObject.toString(4);
        } catch (Exception e) {
            Log.e("", "toString failed", e);
        }
       return "{}";
    }
}
