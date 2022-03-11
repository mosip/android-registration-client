package io.mosip.registration.packetmanager.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class RegistrationDto {

    private String registrationId;
    private String schemaVersion;
    private LocalDateTime registrationDate;
    private Map<String, List<SimpleType>> demographics;
    private Map<String, Document> documents;
    private Map<String, Biometrics> biometrics;
    private Map<String, BiometricsException> biometricsExceptions;
    private MakerReviewerDetails makerReviewerDetails;
    private List<String> selectedLanguages;
}
