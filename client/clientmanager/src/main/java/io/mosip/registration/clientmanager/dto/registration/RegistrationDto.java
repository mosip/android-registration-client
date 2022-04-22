package io.mosip.registration.clientmanager.dto.registration;

import android.util.Log;
import androidx.annotation.NonNull;
import io.mosip.registration.packetmanager.util.JsonUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ValueRange;
import java.util.*;

public class RegistrationDto extends Observable {

    private static final String TAG = RegistrationDto.class.getSimpleName();
    private static final String APPLICANT_DOB_SUBTYPE = "dateOfBirth";
    private static final String BIO_KEY = "%s_%s";
    public static final String dateFormatConfig = "dd/MM/yyyy";
    public static final String ageGroupConfig = "{'INFANT':'0-5','MINOR':'6-17','ADULT':'18-200'}";

    private String rId;
    private String flowType;
    private String process;
    private Double schemaVersion;
    private LocalDateTime dateTime;
    private List<String> selectedLanguages;
    private ConsentDto consentDto;
    private Map<String, Object> demographics;
    private Map<String, DocumentDto> documents;
    private Map<String, BiometricsDto> biometrics;
    public Map<String, Object> AGE_GROUPS = new HashMap<>();
    private OperatorDto maker;
    private OperatorDto reviewer;

    public RegistrationDto(@NonNull String rid, @NonNull String flowType, @NonNull String process,
                           @NonNull Double schemaVersion, @NonNull List<String> languages) {
        this.rId = rid;
        this.dateTime = LocalDateTime.now(ZoneOffset.UTC);
        this.flowType = flowType;
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

    public void setDateField(String fieldId, String subType, String day, String month, String year) {
        if(isValidValue(day) && isValidValue(month) && isValidValue(year)) {
            LocalDate date = LocalDate.of(Integer.valueOf(year), Integer.valueOf(month), Integer.valueOf(day));
            this.demographics.put(fieldId, date.format(DateTimeFormatter.ofPattern(dateFormatConfig)));
            try {
                JSONObject configJson = new JSONObject(ageGroupConfig);
                Iterator<String> itr = configJson.keys();
                while(itr.hasNext()) {
                    String group = itr.next();
                    String[] range = configJson.getString(group).split("-");
                    int ageInYears = Period.between(date, LocalDate.now(ZoneId.of("UTC"))).getYears();
                    if(ValueRange.of(Long.valueOf(range[0]), Long.valueOf(range[1])).isValidIntValue(ageInYears)) {
                        AGE_GROUPS.put(String.format("%s_%s", fieldId, "ageGroup"), group);
                        AGE_GROUPS.put(String.format("%s_%s", fieldId, "age"), ageInYears);

                        if(APPLICANT_DOB_SUBTYPE.equals(subType)) {
                            AGE_GROUPS.put("ageGroup", group);
                            AGE_GROUPS.put("age", ageInYears);
                        }
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Failed to deduce age group", e);
            }
        }
        else {
            this.demographics.remove(fieldId);
        }

        clearAndNotifyAllObservers();
    }

    public void addDemographicField(String fieldId, String value) {
        if(isValidValue(value))
            this.demographics.put(fieldId, value);
        else
            this.demographics.remove(fieldId);

        clearAndNotifyAllObservers();
    }

    public void addDemographicField(String fieldId, String value, String language) {
        List<GenericDto> list = (List<GenericDto>) this.demographics.getOrDefault(fieldId, new ArrayList<GenericDto>());

        Optional<GenericDto> result = list.stream()
                .filter( g -> g.getLangCode().equalsIgnoreCase(language) )
                .findFirst();

        if(result.isPresent()) {
            list.remove(result.get());
        }

        if(isValidValue(value)) {
            ((List<GenericDto>)this.demographics.getOrDefault(fieldId, new ArrayList<GenericDto>()))
                    .add(new GenericDto(value, language));
        }
        clearAndNotifyAllObservers();
    }

    public void removeDemographicField(String fieldId) {
        this.demographics.remove(fieldId);
    }

    public void setConsent(String consentText) {
        this.consentDto = new ConsentDto(consentText, LocalDateTime.now(ZoneOffset.UTC));
    }

    public void addDocument(String fieldId, String docType, byte[] bytes) {
        if( docType != null && bytes != null ) {
            this.documents.put(fieldId, new DocumentDto(docType, "pdf", "", "path", bytes));
        }
    }

    public boolean hasDocument(String fieldId) {
        return this.documents.containsKey(fieldId);
    }

    public boolean hasBiometric(String fieldId, String bioAttribute) {
        return this.biometrics.containsKey(String.format(BIO_KEY, fieldId, bioAttribute));
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
        this.AGE_GROUPS.clear();
        this.selectedLanguages.clear();
        deleteObservers();
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

    public Map<String, Object> getMVELDataContext() {
        Map<String, Object> allIdentityDetails = new LinkedHashMap<String, Object>();
        allIdentityDetails.put("IDSchemaVersion", this.schemaVersion);
        allIdentityDetails.put("_flow", this.flowType);
        allIdentityDetails.put("_process", this.process);
        allIdentityDetails.put("langCodes", this.selectedLanguages);
        allIdentityDetails.putAll(this.demographics);
        allIdentityDetails.putAll(this.documents);
        allIdentityDetails.putAll(this.biometrics);
        allIdentityDetails.putAll(this.AGE_GROUPS);
        return allIdentityDetails;
    }

    private boolean isValidValue(String value) {
        return value != null && !value.trim().isEmpty();
    }

    private void clearAndNotifyAllObservers() {
        Log.i(TAG, "clearAndNotifyAllObservers invoked");
        if(hasChanged()) { clearChanged(); }
        setChanged();
        notifyObservers(getMVELDataContext());
    }
}
