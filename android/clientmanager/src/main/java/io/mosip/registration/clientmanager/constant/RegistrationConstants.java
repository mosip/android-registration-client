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
    public static final String SERVER_VERSION = "mosip.registration.server_version";
    public static final String PRIMARY_LANGUAGE = "mosip.primary-language";
    public static final String SECONDARY_LANGUAGE = "mosip.secondary-language";
    public static final String CONSENT_SCREEN_TEMPLATE_NAME = "mosip.registration.consent-screen-template-name";
    public static final String INDIVIDUAL_BIOMETRICS_ID = "mosip.registration.individual-biometrics-id";
    public static final String INTRODUCER_BIOMETRICS_ID = "mosip.registration.introducer-biometrics-id";
    public static final String INFANT_AGEGROUP_NAME = "mosip.registration.infant-agegroup-name";
    public static final String AGEGROUP_CONFIG = "mosip.registration.agegroup-config";
    public static final String ALLOWED_BIO_ATTRIBUTES = "mosip.registration.allowed-bioattributes";
    public static final String DEFAULT_APP_TYPE_CODE = "mosip.registration.default-app-type-code";

    public static final String RESPONSE = "response";
    public static final String ERRORS = "errors";
    public static final String ON_BOARD_AUTH_STATUS = "authStatus";
    public static final String USER_ON_BOARDING_THRESHOLD_NOT_MET_MSG = "USER_ON_BOARDING_THRESHOLD_NOT_MET_MSG";
    public static final String SUCCESS = "Success";
    public static final String FINGERPRINT_UPPERCASE = "FINGERPRINT";
    public static final String FACE = "FACE";
    public static final String IRIS = "IRIS";
    public static final String USER_ON_BOARDING_SUCCESS_MSG = "USER_ONBOARD_SUCCESS";
    public static final String USER_ON_BOARDING_ERROR_RESPONSE = "USER_ONBOARD_ERROR";

    //Audits
    public static final String REGISTRATION_SCREEN = "Registration: %s";
}
