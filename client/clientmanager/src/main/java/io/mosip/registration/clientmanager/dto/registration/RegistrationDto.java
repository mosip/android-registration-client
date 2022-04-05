package io.mosip.registration.clientmanager.dto.registration;

import android.net.Uri;
import androidx.annotation.NonNull;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

public class RegistrationDto {

    private static final String BIO_KEY = "%s_%s";

    private String rId;
    private String process;
    private String schemaVersion;
    private LocalDateTime dateTime;
    private List<String> selectedLanguages;
    private ConsentDto consentDto;
    private Map<String, Object> demographics;
    private Map<String, DocumentDto> documents;
    private Map<String, BiometricsDto> biometrics;
    private OperatorDto maker;
    private OperatorDto reviewer;

    public RegistrationDto(@NonNull String rid, @NonNull String process, @NonNull String schemaVersion,
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
        this.demographics.put(fieldId, value);
    }

    public void addDemographicField(String fieldId, GenericDto genericDto) {
        List<GenericDto> list = (List<GenericDto>) this.demographics.getOrDefault(fieldId, new ArrayList<GenericDto>());
        list.add(genericDto);
        this.demographics.put(fieldId, list);
    }

    public void removeDemographicField(String fieldId) {
        this.demographics.remove(fieldId);
    }

    public void addDocument(String fieldId, String docType, Uri uri) {
        this.documents.put(fieldId, new DocumentDto(docType, "pdf", "", "path", new byte[0]));
    }

    public void removeDocumentField(String fieldId) {
        this.documents.remove(fieldId);
    }

    public void addBiometric(String fieldId, String modality, String attribute, String bioResponse) {
        this.biometrics.put(String.format(BIO_KEY, fieldId, attribute), new BiometricsDto(modality, attribute,
                false, bioResponse, null, null));
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

    public String getSchemaVersion() {
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

}
