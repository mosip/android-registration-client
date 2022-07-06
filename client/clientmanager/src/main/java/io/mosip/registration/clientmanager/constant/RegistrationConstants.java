package io.mosip.registration.clientmanager.constant;

import java.util.Arrays;
import java.util.List;

public class RegistrationConstants {

    public static final String COMMA = ",";
    public static final String EMPTY_STRING = "";
    public static final String AGE_GROUP = "ageGroup";
    public static final String AGE = "age";
    public static final String DEFAULT_AGE_GROUP = "adult";
    public static final String PROCESS_KEY = "_process";
    public static final String FLOW_KEY = "_flow";
    public static final String ID_SCHEMA_VERSION = "IDSchemaVersion";

    public static final String AUDIT_EXPORTED_TILL = "AuditExportedTill";
    public static final List<String> RIGHT_SLAB_ATTR = Arrays.asList("rightIndex","rightMiddle","rightRing","rightLittle");
    public static final List<String> LEFT_SLAB_ATTR = Arrays.asList("leftIndex","leftMiddle","leftRing","leftLittle");
    public static final List<String> THUMBS_ATTR = Arrays.asList("leftThumb", "rightThumb");
    public static final List<String> DOUBLE_IRIS_ATTR = Arrays.asList("leftEye", "rightEye");
    public static final List<String> FACE_ATTR = Arrays.asList("");
    public static final List<String> EXCEPTION_PHOTO_ATTR = Arrays.asList("unknown");


    //SBI intents
    public static final String DISCOVERY_INTENT_ACTION = "io.sbi.device";
    public static final String D_INFO_INTENT_ACTION = ".Info";
    public static final String R_CAPTURE_INTENT_ACTION = ".rCapture";
    public static final String SBI_INTENT_REQUEST_KEY = "input";
    public static final String SBI_INTENT_RESPONSE_KEY = "response";

    //Global param keys
    public static final String MANDATORY_LANGUAGES_KEY = "mosip.mandatory-languages";
    public static final String OPTIONAL_LANGUAGES_KEY = "mosip.optional-languages";
    public static final String MAX_LANGUAGES_COUNT_KEY = "mosip.max-languages.count";
    public static final String MIN_LANGUAGES_COUNT_KEY = "mosip.min-languages.count";
    public static final String LEFT_SLAP_THRESHOLD_KEY = "mosip.registration.leftslap_fingerprint_threshold";
    public static final String RIGHT_SLAP_THRESHOLD_KEY = "mosip.registration.rightslap_fingerprint_threshold";
    public static final String THUMBS_THRESHOLD_KEY = "mosip.registration.thumbs_fingerprint_threshold";
    public static final String IRIS_THRESHOLD_KEY = "mosip.registration.iris_threshold";
    public static final String FACE_THRESHOLD_KEY = "mosip.registration.face_threshold";
    public static final String LEFT_SLAP_ATTEMPTS_KEY = "mosip.registration.num_of_fingerprint_retries";
    public static final String RIGHT_SLAP_ATTEMPTS_KEY = "mosip.registration.num_of_fingerprint_retries";
    public static final String THUMBS_ATTEMPTS_KEY = "mosip.registration.num_of_fingerprint_retries";
    public static final String IRIS_ATTEMPTS_KEY = "mosip.registration.num_of_iris_retries";
    public static final String FACE_ATTEMPTS_KEY = "mosip.registration.num_of_face_retries";

    //Audits
    public static final String REGISTRATION_SCREEN = "Registration: %s";
}
