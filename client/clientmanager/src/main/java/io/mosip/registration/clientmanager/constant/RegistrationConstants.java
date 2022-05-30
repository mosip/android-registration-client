package io.mosip.registration.clientmanager.constant;

import java.util.Arrays;
import java.util.List;

public class RegistrationConstants {
    public static final String EMPTY_STRING = "";
    public static final String AGE_GROUP = "ageGroup";
    public static final String AGE = "age";
    public static final String DEFAULT_AGE_GROUP = "adult";
    public static final String PROCESS_KEY = "_process";
    public static final String FLOW_KEY = "_flow";
    public static final String ID_SCHEMA_VERSION = "IDSchemaVersion";
    public static final List<String> RIGHT_SLAB_ATTR = Arrays.asList("rightIndex","rightMiddle","rightRing","rightLittle");
    public static final List<String> LEFT_SLAB_ATTR = Arrays.asList("leftIndex","leftMiddle","leftRing","leftLittle");
    public static final List<String> THUMBS_ATTR = Arrays.asList("leftThumb", "rightThumb");
    public static final List<String> DOUBLE_IRIS_ATTR = Arrays.asList("leftEye", "rightEye");
    public static final List<String> FACE_ATTR = Arrays.asList("face");
    public static final List<String> EXCEPTION_PHOTO_ATTR = Arrays.asList("unknown");
}
