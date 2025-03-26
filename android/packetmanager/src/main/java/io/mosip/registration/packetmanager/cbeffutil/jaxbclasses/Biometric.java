package io.mosip.registration.packetmanager.cbeffutil.jaxbclasses;

import static io.mosip.registration.packetmanager.util.PacketManagerConstant.FACE_FULL_FACE;
import static io.mosip.registration.packetmanager.util.PacketManagerConstant.FINGERPRINT_SLAB_LEFT;
import static io.mosip.registration.packetmanager.util.PacketManagerConstant.FINGERPRINT_SLAB_RIGHT;
import static io.mosip.registration.packetmanager.util.PacketManagerConstant.FINGERPRINT_SLAB_THUMBS;
import static io.mosip.registration.packetmanager.util.PacketManagerConstant.IRIS;
import static io.mosip.registration.packetmanager.util.PacketManagerConstant.IRIS_DOUBLE;
import static io.mosip.registration.packetmanager.util.PacketManagerConstant.LEFT_SLAB;
import static io.mosip.registration.packetmanager.util.PacketManagerConstant.RIGHT_SLAB;
import static io.mosip.registration.packetmanager.util.PacketManagerConstant.THUMBS;

import java.util.ArrayList;
import java.util.List;

import io.mosip.registration.packetmanager.dto.PacketWriter.BiometricType;

public enum Biometric {

    LEFT_INDEX(FINGERPRINT_SLAB_LEFT, LEFT_SLAB, "leftIndex", BiometricType.FINGER, "LF_INDEX"),
    LEFT_MIDDLE(FINGERPRINT_SLAB_LEFT, LEFT_SLAB, "leftMiddle", BiometricType.FINGER, "LF_MIDDLE"),
    LEFT_RING(FINGERPRINT_SLAB_LEFT, LEFT_SLAB, "leftRing", BiometricType.FINGER, "LF_RING"),
    LEFT_LITTLE(FINGERPRINT_SLAB_LEFT, LEFT_SLAB, "leftLittle", BiometricType.FINGER, "LF_LITTLE"),
    RIGHT_INDEX(FINGERPRINT_SLAB_RIGHT, RIGHT_SLAB, "rightIndex", BiometricType.FINGER, "RF_INDEX"),
    RIGHT_MIDDLE(FINGERPRINT_SLAB_RIGHT, RIGHT_SLAB, "rightMiddle", BiometricType.FINGER, "RF_MIDDLE"),
    RIGHT_RING(FINGERPRINT_SLAB_RIGHT, RIGHT_SLAB, "rightRing", BiometricType.FINGER, "RF_RING"),
    RIGHT_LITTLE(FINGERPRINT_SLAB_RIGHT, RIGHT_SLAB, "rightLittle", BiometricType.FINGER, "RF_LITTLE"),
    LEFT_THUMB(FINGERPRINT_SLAB_THUMBS, THUMBS, "leftThumb", BiometricType.FINGER, "LF_THUMB"),
    RIGHT_THUMB(FINGERPRINT_SLAB_THUMBS, THUMBS, "rightThumb", BiometricType.FINGER, "RF_THUMB"),
    RIGHT_IRIS(IRIS_DOUBLE, IRIS, "rightEye", BiometricType.IRIS, "R_IRIS"),
    LEFT_IRIS(IRIS_DOUBLE, IRIS, "leftEye", BiometricType.IRIS, "L_IRIS"),
    FACE(FACE_FULL_FACE, "Face", "face", BiometricType.FACE, "FACE"),
    EXCEPTION(FACE_FULL_FACE, "ExceptionPhoto", "unknown", BiometricType.EXCEPTION_PHOTO, "EXCEPTION_PHOTO");

    Biometric(String modalityName, String modalityShortName, String attributeName, BiometricType biometricType,
              String mdmConstant) {
        this.modalityName = modalityName;
        this.setModalityShortName(modalityShortName);
        this.attributeName = attributeName;
        this.biometricType = biometricType;
        this.mdmConstant = mdmConstant;
    }

    private String modalityName;
    private String modalityShortName;
    private String attributeName;
    private BiometricType biometricType;
    private String mdmConstant;

    public String getModalityName() {
        return modalityName;
    }
    public void setModalityName(String modalityName) {
        this.modalityName = modalityName;
    }
    public String getAttributeName() {
        return attributeName;
    }
    public void setAttributes(String attributeName) {
        this.attributeName = attributeName;
    }

    public BiometricType getBiometricType() {
        return biometricType;
    }

    public void setBiometricType(BiometricType biometricType) {
        this.biometricType = biometricType;
    }

    public String getModalityShortName() {
        return modalityShortName;
    }
    public void setModalityShortName(String modalityShortName) {
        this.modalityShortName = modalityShortName;
    }
    public static List<String> getDefaultAttributes(String modalityName) {
        List<String> list = new ArrayList<>();
        for(Biometric biometric : Biometric.values()) {
            if(biometric.getModalityName().equalsIgnoreCase(modalityName) ||
                    biometric.getModalityShortName().equalsIgnoreCase(modalityName))
                list.add(biometric.getAttributeName());
        }
        return list;
    }

    public static String getModalityNameByAttribute(String attributeName) {
        String modalityName = null;
        for(Biometric biometric : Biometric.values()) {
            if(biometric.getAttributeName().equalsIgnoreCase(attributeName)) {
                modalityName = biometric.getModalityName();
                break;
            }
        }
        return modalityName;
    }

    public static BiometricType getSingleTypeByAttribute(String attributeName) {
        BiometricType modalityName = null;
        for(Biometric biometric : Biometric.values()) {
            if(biometric.getAttributeName().equalsIgnoreCase(attributeName)) {
                modalityName = biometric.getBiometricType();
                break;
            }
        }
        return modalityName;
    }

    public static Biometric getBiometricByAttribute(String attributeName) {
        Biometric constant = null;
        for(Biometric biometric : Biometric.values()) {
            if(biometric.getMdmConstant().equalsIgnoreCase(attributeName) ||
                    biometric.getAttributeName().equalsIgnoreCase(attributeName)) {
                constant = biometric;
                break;
            }
        }
        return constant;
    }

    public static Biometric getBiometricByMDMConstant(String mdmConstant) {
        Biometric constant = null;
        for(Biometric biometric : Biometric.values()) {
            if(biometric.getMdmConstant().equalsIgnoreCase(mdmConstant)) {
                constant = biometric;
                break;
            }
        }
        return constant;
    }

    public static long getFormatType(BiometricType biometricType) {
        long format = 0;
        switch (biometricType) {
            case FINGER:
                format = CbeffConstant.FORMAT_TYPE_FINGER;
                break;

            case EXCEPTION_PHOTO:
            case FACE:
                format = CbeffConstant.FORMAT_TYPE_FACE;
                break;
            case IRIS:
                format = CbeffConstant.FORMAT_TYPE_IRIS;
                break;
        }
        return format;
    }
    public String getMdmConstant() {
        return mdmConstant;
    }
    public void setMdsConstant(String mdmConstant) {
        this.mdmConstant = mdmConstant;
    }
}
