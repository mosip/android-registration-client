package io.mosip.registration.clientmanager.constant;

import io.mosip.registration.clientmanager.R;
import io.mosip.registration.packetmanager.cbeffutil.jaxbclasses.SingleType;
import io.mosip.registration.packetmanager.util.PacketManagerConstant;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public enum Modality implements Serializable {

    FINGERPRINT_SLAB_LEFT(RegistrationConstants.LEFT_SLAB_ATTR, SingleType.FINGER, 1),
    FINGERPRINT_SLAB_RIGHT(RegistrationConstants.RIGHT_SLAB_ATTR, SingleType.FINGER, 2),
    FINGERPRINT_SLAB_THUMBS(RegistrationConstants.THUMBS_ATTR, SingleType.FINGER, 3),
    IRIS_DOUBLE(RegistrationConstants.DOUBLE_IRIS_ATTR, SingleType.IRIS, 3),
    FACE(RegistrationConstants.FACE_ATTR, SingleType.FACE, 0),
    EXCEPTION_PHOTO(RegistrationConstants.EXCEPTION_PHOTO_ATTR, SingleType.EXCEPTION_PHOTO, 0);

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

    public static Modality getModality(String attribute) {
        if(RegistrationConstants.LEFT_SLAB_ATTR.stream().anyMatch(a -> a.equalsIgnoreCase(attribute)))
            return Modality.FINGERPRINT_SLAB_LEFT;

        if(RegistrationConstants.RIGHT_SLAB_ATTR.stream().anyMatch(a -> a.equalsIgnoreCase(attribute)))
            return Modality.FINGERPRINT_SLAB_RIGHT;

        if(RegistrationConstants.THUMBS_ATTR.stream().anyMatch(a -> a.equalsIgnoreCase(attribute)))
            return Modality.FINGERPRINT_SLAB_THUMBS;

        if(RegistrationConstants.DOUBLE_IRIS_ATTR.stream().anyMatch(a -> a.equalsIgnoreCase(attribute)))
            return Modality.IRIS_DOUBLE;

        if(RegistrationConstants.FACE_ATTR.stream().anyMatch(a -> a.equalsIgnoreCase(attribute) ||
                "face".equalsIgnoreCase(attribute)))
            return Modality.FACE;

        if(RegistrationConstants.EXCEPTION_PHOTO_ATTR.stream().anyMatch(a -> a.equalsIgnoreCase(attribute)))
            return Modality.EXCEPTION_PHOTO;

        return null;
    }

    public static List<String> getSpecBioSubType(List<String> bioAttributes) {
        List<String> bioSubTypes = new ArrayList<>();
        for(String attribute : bioAttributes) {
            switch (attribute.toLowerCase()) {
                case "leftlittle": bioSubTypes.add(RegistrationConstants.LEFT_LITTLE_FINGER); break;
                case "leftring": bioSubTypes.add(RegistrationConstants.LEFT_RING_FINGER); break;
                case "leftmiddle": bioSubTypes.add(RegistrationConstants.LEFT_MIDDLE_FINGER); break;
                case "leftindex": bioSubTypes.add(RegistrationConstants.LEFT_INDEX_FINGER); break;
                case "rightlittle": bioSubTypes.add(RegistrationConstants.RIGHT_LITTLE_FINGER); break;
                case "rightring": bioSubTypes.add(RegistrationConstants.RIGHT_RING_FINGER);  break;
                case "rightmiddle": bioSubTypes.add(RegistrationConstants.RIGHT_MIDDLE_FINGER); break;
                case "rightindex": bioSubTypes.add(RegistrationConstants.RIGHT_INDEX_FINGER); break;
                case "leftthumb": bioSubTypes.add(RegistrationConstants.LEFT_THUMB); break;
                case "rightthumb": bioSubTypes.add(RegistrationConstants.RIGHT_THUMB); break;
                case "lefteye": case "leftiris": bioSubTypes.add(RegistrationConstants.LEFT); break;
                case "righteye": case "rightiris": bioSubTypes.add(RegistrationConstants.RIGHT); break;
                case "face": bioSubTypes.add("Face"); break;
            }
        }
        return bioSubTypes;
    }

    public static String getSpecBioSubType(String bioAttribute) {
            switch (bioAttribute.toLowerCase()) {
                case "leftlittle": return RegistrationConstants.LEFT_LITTLE_FINGER;
                case "leftring": return RegistrationConstants.LEFT_RING_FINGER;
                case "leftmiddle": return RegistrationConstants.LEFT_MIDDLE_FINGER;
                case "leftindex": return RegistrationConstants.LEFT_INDEX_FINGER;
                case "rightlittle": return RegistrationConstants.RIGHT_LITTLE_FINGER;
                case "rightring": return RegistrationConstants.RIGHT_RING_FINGER;
                case "rightmiddle": return RegistrationConstants.RIGHT_MIDDLE_FINGER;
                case "rightindex": return RegistrationConstants.RIGHT_INDEX_FINGER;
                case "leftthumb": return RegistrationConstants.LEFT_THUMB;
                case "rightthumb": return RegistrationConstants.RIGHT_THUMB;
                case "lefteye": case "leftiris": return RegistrationConstants.LEFT;
                case "righteye": case "rightiris": return RegistrationConstants.RIGHT;
                case "face": return "Face";
            }
        return "";
    }

    public static String getBioAttribute(String bioSubType) {
        if(bioSubType==null){
            return "";
        }
        switch (bioSubType) {
            case RegistrationConstants.LEFT_LITTLE_FINGER : return "leftLittle";
            case RegistrationConstants.LEFT_RING_FINGER : return "leftRing";
            case RegistrationConstants.LEFT_MIDDLE_FINGER : return "leftMiddle";
            case RegistrationConstants.LEFT_INDEX_FINGER : return "leftIndex";
            case RegistrationConstants.RIGHT_LITTLE_FINGER : return "rightLittle";
            case RegistrationConstants.RIGHT_RING_FINGER : return "rightRing";
            case RegistrationConstants.RIGHT_MIDDLE_FINGER : return "rightMiddle";
            case RegistrationConstants.RIGHT_INDEX_FINGER : return "rightIndex";
            case RegistrationConstants.LEFT_THUMB : return "leftThumb";
            case RegistrationConstants.RIGHT_THUMB : return "rightThumb";
            case RegistrationConstants.LEFT: return "leftEye";
            case RegistrationConstants.RIGHT : return "rightEye";
            case "Face":return "";
        }
        return bioSubType;
    }

    public static long getFormatType(SingleType singleType) {
        long format = 0;
        switch (singleType) {
            case FINGER:
                format = PacketManagerConstant.FORMAT_TYPE_FINGER;
                break;
            case EXCEPTION_PHOTO:
            case FACE:
                format = PacketManagerConstant.FORMAT_TYPE_FACE;
                break;
            case IRIS:
                format = PacketManagerConstant.FORMAT_TYPE_IRIS;
                break;
        }
        return format;
    }

}
