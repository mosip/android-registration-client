package io.mosip.registration.clientmanager.dto.registration;

import android.util.Log;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ValueRange;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Observable;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import io.mosip.registration.clientmanager.constant.Modality;
import io.mosip.registration.clientmanager.constant.RegistrationConstants;
import io.mosip.registration.clientmanager.dto.sbi.DigitalId;
import io.mosip.registration.packetmanager.dto.SimpleType;
import io.mosip.registration.packetmanager.util.JsonUtils;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class RegistrationDto extends Observable {

    private static final String TAG = RegistrationDto.class.getSimpleName();
    private static final String APPLICANT_DOB_SUBTYPE = "dateOfBirth";
    private static final String BIO_KEY_ATTEMPT = "%s_%s_%d";
    private static final String BIO_KEY = "%s_%s";
    private static final String BIO_KEY_PATTERN = "%s_%s_";

    //TODO take it from config
    public static final String DATE_FORMAT_CONFIG = "yyyy/MM/dd";
    public static final String AGE_GROUP_CONFIG = "{'INFANT':'0-5','MINOR':'6-17','ADULT':'18-200'}";

    private String rId;
    private String packetId;
    private String flowType;
    private String process;
    private String preRegistrationId;
    private Double schemaVersion;
    private List<String> selectedLanguages;
    private ConsentDto consentDto;
    private Map<String, Object> demographics;
    private Map<String, DocumentDto> documents;
    private Map<String, BiometricsDto> biometrics;

    private boolean isBiometricMarkedForUpdate;
    private List<String> updatableFields;
    private List<String> updatableFieldGroups;
    private boolean isUpdateUINNonBiometric;
    private boolean isNameNotUpdated;
    private List<String> defaultUpdatableFieldGroups;

    //Supporting fields
    public Map<String, Object> AGE_GROUPS = new HashMap<>();
    public Map<String, AtomicInteger> ATTEMPTS;
    public Map<String, Set<String>> EXCEPTIONS;
    public Map<Modality, Integer> BIO_THRESHOLDS;
    public Set<String> CAPTURED_BIO_FIELDS;
    public Map<Modality, Object> BIO_DEVICES;
    private OperatorDto maker;
    private OperatorDto reviewer;
    public Map<String, String> SELECTED_CODES = new HashMap<>();

    private String applicationId;
    private String additionalInfoRequestId;

    public RegistrationDto(@NonNull String rid, @NonNull String flowType, @NonNull String process,
                           @NonNull Double schemaVersion, @NonNull List<String> languages,
                           @NonNull Map<Modality, Integer> bioThresholds, @NonNull String applicationId) {
        this.rId = rid;
        this.flowType = flowType;
        this.process = process;
        this.schemaVersion = schemaVersion;
        this.selectedLanguages = languages;
        this.demographics = new HashMap<>();
        this.documents = new HashMap<>();
        this.biometrics = new HashMap<>();
        this.updatableFields = new ArrayList<>();
        this.updatableFieldGroups = new ArrayList<>();
        this.defaultUpdatableFieldGroups = new ArrayList<>();
        this.ATTEMPTS = new HashMap<>();
        this.EXCEPTIONS = new HashMap<>();
        this.BIO_THRESHOLDS = bioThresholds;
        this.CAPTURED_BIO_FIELDS = new HashSet<>();
        this.BIO_DEVICES = new HashMap<>();
        this.applicationId = applicationId;
    }

    public void setMakerDetails() {
        //TODO
    }

    public void setReviewerDetails() {
        //TODO
    }

    public void setDateField(String fieldId, String subType, String day, String month, String year) {
        if(isValidValue(day) && isValidValue(month) && isValidValue(year)) {
            LocalDate date = LocalDate.of(Integer.valueOf(year), Integer.valueOf(month), Integer.valueOf(day));
            this.demographics.put(fieldId, date.format(DateTimeFormatter.ofPattern(DATE_FORMAT_CONFIG)));
            try {
                JSONObject configJson = new JSONObject(AGE_GROUP_CONFIG);
                Iterator<String> itr = configJson.keys();
                while(itr.hasNext()) {
                    String group = itr.next();
                    String[] range = configJson.getString(group).split("-");
                    int ageInYears = Period.between(date, LocalDate.now(ZoneId.of("UTC"))).getYears();
                    if(ValueRange.of(Long.valueOf(range[0]), Long.valueOf(range[1])).isValidIntValue(ageInYears)) {
                        AGE_GROUPS.put(String.format(BIO_KEY, fieldId, RegistrationConstants.AGE_GROUP), group);
                        AGE_GROUPS.put(String.format(BIO_KEY, fieldId, RegistrationConstants.AGE), ageInYears);

                        if(APPLICANT_DOB_SUBTYPE.equals(subType)) {
                            AGE_GROUPS.put(RegistrationConstants.AGE_GROUP, group);
                            AGE_GROUPS.put(RegistrationConstants.AGE, ageInYears);
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

    public Map<String, Object> getAgeGroup(){
        return AGE_GROUPS;
    }

    public void addDemographicField(String fieldId, String value) {
        if(isValidValue(value))
            this.demographics.put(fieldId, value);
        else
            this.demographics.remove(fieldId);

        clearAndNotifyAllObservers();
    }

    public void addDemographicField(String fieldId, Object value) {
        this.demographics.put(fieldId, value);
        clearAndNotifyAllObservers();
    }
    public void clearBiometricsHashmap(){
        this.biometrics.clear();
        clearAttemptsHashmap();
        clearExceptionsHashmap();
    }
    public void clearDocumentsHashmap(){
        this.documents.clear();
    }
    public void clearAttemptsHashmap(){
        this.ATTEMPTS.clear();
    }
    public void clearExceptionsHashmap(){
        this.EXCEPTIONS.clear();
    }
    public void addDemographicField(String fieldId, String value, String language) {
        this.demographics.compute(fieldId, (k, v) -> {
            v = v != null ? v : new ArrayList<SimpleType>();
            ((List<SimpleType>)v).removeIf( e -> e.getLanguage().equalsIgnoreCase(language));
            if(isValidValue(value))
                ((List<SimpleType>)v).add(new SimpleType(language, value));
            return v;
        });
        clearAndNotifyAllObservers();
    }

    public void removeDemographicField(String fieldId) {
        this.demographics.remove(fieldId);
    }

    public void addUpdatableFields(List<String> fieldIds) {
        this.updatableFields.addAll(fieldIds);
        clearAndNotifyAllObservers();
    }

    public void addUpdatableFieldGroup(String fieldGroup) {
        this.updatableFieldGroups.add(fieldGroup);
        clearAndNotifyAllObservers();
    }

    public void removeUpdatableFields(List<String> fieldIds) {
        this.updatableFields.removeAll(fieldIds);
        clearAndNotifyAllObservers();
    }

    public void removeUpdatableFieldGroup(String fieldGroup) {
        this.updatableFieldGroups.remove(fieldGroup);
        clearAndNotifyAllObservers();
    }

    public void setConsent(String consentText) {
        this.consentDto = new ConsentDto(consentText, LocalDateTime.now(ZoneOffset.UTC));
    }

    public void addDocument(String fieldId, String docType, String format,String reference, byte[] bytes) {
        if( docType != null && bytes != null ) {
            DocumentDto documentDto = this.documents.getOrDefault(fieldId, new DocumentDto());
            documentDto.setType(docType);
            if(format != null) {
                documentDto.setFormat(format);
            }else{
                documentDto.setFormat("pdf");
            }
            documentDto.setRefNumber(reference);
            documentDto.getContent().add(bytes);
            this.documents.put(fieldId, documentDto);
        }
    }

    public void removeDocument(String fieldId, int pageIndex) {
        DocumentDto documentDto = this.documents.get(fieldId);
        if( documentDto != null ) {
            this.documents.get(fieldId).getContent().remove(pageIndex);
        }
    }

    public List<DocumentDto> getScannedPages(String fieldId) {
        DocumentDto documentDto = this.documents.get(fieldId);
        if( documentDto != null ) {
            return Collections.singletonList(documentDto);
        }
        return Collections.EMPTY_LIST;
    }

    public boolean hasDocument(String fieldId) {
        return this.documents.containsKey(fieldId);
    }

    public void removeDocumentField(String fieldId) {
        this.documents.remove(fieldId);
    }

    public void addBiometric(String fieldId, String attribute, int attempt, BiometricsDto biometricsDto) {
        biometricsDto.setNumOfRetries(attempt);
        this.biometrics.put(String.format(BIO_KEY_ATTEMPT, fieldId, attribute, attempt), biometricsDto);
        this.CAPTURED_BIO_FIELDS.add(fieldId);
    }

    public List<BiometricsDto> getBiometrics(String fieldId, Modality modality, int attempt) {

        List<BiometricsDto> list = new ArrayList<>();
        for(String attribute : modality.getAttributes()) {
            String key = String.format(BIO_KEY_ATTEMPT, fieldId, attribute, attempt);

            if(this.biometrics.containsKey(key))
                list.add(this.biometrics.get(key));
        }

        return list;
    }

    public List<BiometricsDto> getBestBiometrics(String fieldId, Modality modality) {
        List<BiometricsDto> list = new ArrayList<>();
        for(String attribute : modality.getAttributes()) {
            Optional<BiometricsDto> highestScoredAttribute = this.biometrics
                    .entrySet()
                    .stream()
                    .filter(e -> e.getKey().startsWith(String.format(BIO_KEY_PATTERN, fieldId, attribute)))
                    .map( v -> v.getValue())
                    .max(Comparator.comparingDouble(BiometricsDto::getQualityScore));

            if(highestScoredAttribute.isPresent()) {
                BiometricsDto dto = highestScoredAttribute.get();
                dto.setNumOfRetries(this.getBioAttempt(fieldId, modality));
                list.add(dto);
            }
        }
        return list;
    }

    public void removeBiometricField(String fieldId) {
        this.biometrics.remove(fieldId);
    }

    public int incrementBioAttempt(String fieldId, Modality modality) {
        String key = String.format(BIO_KEY, fieldId, modality.name());
        ATTEMPTS.putIfAbsent(key, new AtomicInteger(0));
        return ATTEMPTS.get(key).incrementAndGet();
    }

    public int getBioAttempt(String fieldId, Modality modality) {
        String key = String.format(BIO_KEY, fieldId, modality.name());
        return ATTEMPTS.getOrDefault(key, new AtomicInteger(0)).get();
    }

    public void addBioException(String fieldId, Modality modality, String attribute) {
        String key = String.format(BIO_KEY, fieldId, modality.name());
        EXCEPTIONS.putIfAbsent(key, new HashSet<>());
        EXCEPTIONS.get(key).add(attribute);
        resetBioCapture(fieldId, modality);
    }

    public void removeBioException(String fieldId, Modality modality, String attribute) {
        String key = String.format(BIO_KEY, fieldId, modality.name());
        EXCEPTIONS.putIfAbsent(key, new HashSet<>());
        if(!EXCEPTIONS.get(key).isEmpty() && EXCEPTIONS.get(key).size()>1) {
            EXCEPTIONS.get(key).remove(attribute);
        } else {
            EXCEPTIONS.remove(key);
        }
        resetBioCapture(fieldId, modality);
    }

    public boolean isBioException(String fieldId, Modality modality, String attribute) {
        String key = String.format(BIO_KEY, fieldId, modality.name());
        return EXCEPTIONS.getOrDefault(key, Collections.EMPTY_SET).contains(attribute);
    }

    private void resetBioCapture(String fieldId, Modality modality) {
        String key = String.format(BIO_KEY, fieldId, modality.name());
        ATTEMPTS.put(key, new AtomicInteger(0));
        for(String attribute : modality.getAttributes()) {
            this.biometrics.keySet()
                    .removeIf(k -> k.startsWith(String.format(BIO_KEY_PATTERN, fieldId, attribute)));
        }
    }


    public void addBioDevice(Modality modality, String deviceCode, DigitalId digitalId) {
        Map<String, Object> registeredDevice = new LinkedHashMap<>();
        Map<String, String> digitalIdMap = new HashMap<>();
        digitalIdMap.put("serialNo", digitalId.getSerialNo());
        digitalIdMap.put("make", digitalId.getMake());
        digitalIdMap.put("model", digitalId.getModel());
        digitalIdMap.put("type", digitalId.getType());
        digitalIdMap.put("deviceProviderId", digitalId.getDeviceProviderId());
        digitalIdMap.put("deviceProvider", digitalId.getDeviceProvider());
        digitalIdMap.put("dateTime", digitalId.getDateTime());
        registeredDevice.put("deviceCode", deviceCode);
        registeredDevice.put("deviceServiceVersion", "0.9.5");
        registeredDevice.put("digitalId", digitalIdMap);
        BIO_DEVICES.put(modality, registeredDevice);
    }

    public Set<Map.Entry<String, Object>> getAllDemographicFields() {
        return this.demographics.entrySet();
    }

    public Map<String, Object> getDemographics() {
        return this.demographics;
    }

    public Map<String, BiometricsDto> getBiometrics() { return this.biometrics; }

    public Set<Map.Entry<String, DocumentDto>> getAllDocumentFields() {
        return this.documents.entrySet();
    }

    public Map<String, DocumentDto> getDocuments() { return this.documents; }

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

    public OperatorDto getMaker() { return maker; }

    public void cleanup() {
        this.demographics.clear();
        this.documents.clear();
        this.biometrics.clear();
        this.AGE_GROUPS.clear();
        this.EXCEPTIONS.clear();
        this.SELECTED_CODES.clear();
        this.selectedLanguages.removeIf(o->true);

        deleteObservers();
    }

    public void changeUpdatableFieldGroups() {
        this.demographics.clear();
        this.AGE_GROUPS.clear();
        this.SELECTED_CODES.clear();
        clearAndNotifyAllObservers();
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
        allIdentityDetails.put(RegistrationConstants.ID_SCHEMA_VERSION, this.schemaVersion);
        allIdentityDetails.put(RegistrationConstants.FLOW_KEY, this.flowType);
        allIdentityDetails.put(RegistrationConstants.PROCESS_KEY, this.process);
        allIdentityDetails.put("langCodes", this.selectedLanguages);
        allIdentityDetails.put("isNew", Objects.equals(this.flowType, "NEW"));
        allIdentityDetails.put("isUpdate", Objects.equals(this.flowType, "Update"));
        allIdentityDetails.put("isLost", Objects.equals(this.flowType, "Lost"));
        allIdentityDetails.put("updatableFields",
                this.updatableFields == null ? Collections.EMPTY_LIST : this.updatableFields);
        allIdentityDetails.put("updatableFieldGroups",
                this.updatableFieldGroups == null ? Collections.EMPTY_LIST : this.updatableFieldGroups);
        allIdentityDetails.putAll(this.demographics);
        allIdentityDetails.putAll(this.documents);
        allIdentityDetails.putAll(this.biometrics);
        allIdentityDetails.putAll(this.AGE_GROUPS);
        allIdentityDetails.putAll(this.SELECTED_CODES);
        allIdentityDetails.put("isBioException", this.EXCEPTIONS.size() > 0);
        if(!allIdentityDetails.containsKey(RegistrationConstants.AGE))
            allIdentityDetails.put(RegistrationConstants.AGE, 0);
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

    public void addWithoutDocument(String fieldId, String docType, String format,String value, String reference) {
        if( docType != null) {
            DocumentDto documentDto = this.documents.getOrDefault(fieldId, new DocumentDto());
            documentDto.setType(docType);
            documentDto.setFormat(format);
            documentDto.setValue(value);
            documentDto.setRefNumber(reference);
            this.documents.put(fieldId, documentDto);
        }
    }
}
