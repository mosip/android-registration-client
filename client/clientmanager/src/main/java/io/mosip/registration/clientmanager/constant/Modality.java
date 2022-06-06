package io.mosip.registration.clientmanager.constant;

import io.mosip.registration.packetmanager.cbeffutil.jaxbclasses.SingleType;

import java.util.ArrayList;
import java.util.List;

public enum Modality {

    FINGERPRINT_SLAB_LEFT(RegistrationConstants.LEFT_SLAB_ATTR, SingleType.FINGER, 1),
    FINGERPRINT_SLAB_RIGHT(RegistrationConstants.RIGHT_SLAB_ATTR, SingleType.FINGER, 2),
    FINGERPRINT_SLAB_THUMBS(RegistrationConstants.THUMBS_ATTR, SingleType.FINGER, 3),
    IRIS_DOUBLE(RegistrationConstants.DOUBLE_IRIS_ATTR, SingleType.IRIS, 3),
    FACE(RegistrationConstants.FACE_ATTR, SingleType.FACE, 0);

    public List<String> getAttributes() {
        return attributes;
    }
    public SingleType getSingleType() {
        return singleType;
    }
    public int getDeviceSubId() {
        return deviceSubId;
    }
    List<String> attributes;

    SingleType singleType;

    private int deviceSubId;

    Modality(List<String> attributes, SingleType singleType, int deviceSubId) {
        this.attributes = attributes;
        this.singleType = singleType;
        this.deviceSubId = deviceSubId;
    }

    public static List<String> getSpecBioSubType(List<String> bioAttributes) {
        List<String> bioSubTypes = new ArrayList<>();
        for(String attribute : bioAttributes) {
            switch (attribute.toLowerCase()) {
                case "leftlittle": bioSubTypes.add("Left LittleFinger"); break;
                case "leftring": bioSubTypes.add("Left RingFinger"); break;
                case "leftmiddle": bioSubTypes.add("Left MiddleFinger"); break;
                case "leftindex": bioSubTypes.add("Left IndexFinger"); break;
                case "rightlittle": bioSubTypes.add("Right LittleFinger"); break;
                case "rightring": bioSubTypes.add("Right RingFinger");  break;
                case "rightmiddle": bioSubTypes.add("Right MiddleFinger"); break;
                case "rightindex": bioSubTypes.add("Right IndexFinger"); break;
                case "leftthumb": bioSubTypes.add("Left Thumb"); break;
                case "rightthumb": bioSubTypes.add("Right Thumb"); break;
                case "lefteye": case "leftiris": bioSubTypes.add("Left"); break;
                case "righteye": case "rightiris": bioSubTypes.add("Right"); break;
                case "face": bioSubTypes.add("Face"); break;
            }
        }
        return bioSubTypes;
    }

    public static String getBioAttribute(String bioSubType) {
        switch (bioSubType) {
            case "Left LittleFinger": return "leftLittle";
            case "Left RingFinger" : return "leftRing";
            case "Left MiddleFinger": return "leftMiddle";
            case "Left IndexFinger": return "leftIndex";
            case "Right LittleFinger": return "rightLittle";
            case "Right RingFinger": return "rightRing";
            case "Right MiddleFinger": return "rightMiddle";
            case "Right IndexFinger": return "rightIndex";
            case "Left Thumb": return "leftThumb";
            case "Right Thumb": return "rightThumb";
            case "Left": return "leftEye";
            case "Right": return "rightEye";
            case "Face":return "";
        }
        return bioSubType;
    }

}
