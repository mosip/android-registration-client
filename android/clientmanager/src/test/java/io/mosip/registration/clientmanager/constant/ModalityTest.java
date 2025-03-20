package io.mosip.registration.clientmanager.constant;

import io.mosip.registration.packetmanager.cbeffutil.jaxbclasses.SingleType;
import io.mosip.registration.packetmanager.util.PacketManagerConstant;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class ModalityTest {

    @Test
    public void testFingerprintSlabLeft() {
        Modality modality = Modality.FINGERPRINT_SLAB_LEFT;
        assertEquals(RegistrationConstants.LEFT_SLAB_ATTR, modality.getAttributes());
        assertEquals(SingleType.FINGER, modality.getSingleType());
        assertEquals(1, modality.getDeviceSubId());
    }

    @Test
    public void testFingerprintSlabRight() {
        Modality modality = Modality.FINGERPRINT_SLAB_RIGHT;
        assertEquals(RegistrationConstants.RIGHT_SLAB_ATTR, modality.getAttributes());
        assertEquals(SingleType.FINGER, modality.getSingleType());
        assertEquals(2, modality.getDeviceSubId());
    }

    @Test
    public void testFingerprintSlabThumbs() {
        Modality modality = Modality.FINGERPRINT_SLAB_THUMBS;
        assertEquals(RegistrationConstants.THUMBS_ATTR, modality.getAttributes());
        assertEquals(SingleType.FINGER, modality.getSingleType());
        assertEquals(3, modality.getDeviceSubId());
    }

    @Test
    public void testIrisDouble() {
        Modality modality = Modality.IRIS_DOUBLE;
        assertEquals(RegistrationConstants.DOUBLE_IRIS_ATTR, modality.getAttributes());
        assertEquals(SingleType.IRIS, modality.getSingleType());
        assertEquals(3, modality.getDeviceSubId());
    }

    @Test
    public void testFace() {
        Modality modality = Modality.FACE;
        assertEquals(RegistrationConstants.FACE_ATTR, modality.getAttributes());
        assertEquals(SingleType.FACE, modality.getSingleType());
        assertEquals(0, modality.getDeviceSubId());
    }

    @Test
    public void testExceptionPhoto() {
        Modality modality = Modality.EXCEPTION_PHOTO;
        assertEquals(RegistrationConstants.EXCEPTION_PHOTO_ATTR, modality.getAttributes());
        assertEquals(SingleType.EXCEPTION_PHOTO, modality.getSingleType());
        assertEquals(0, modality.getDeviceSubId());
    }

    @Test
    public void testGetModality_LeftSlabAttribute() {
        assertEquals(Modality.FINGERPRINT_SLAB_LEFT, Modality.getModality("leftIndex"));
    }

    @Test
    public void testGetModality_RightSlabAttribute() {
        assertEquals(Modality.FINGERPRINT_SLAB_RIGHT, Modality.getModality("rightIndex"));
    }

    @Test
    public void testGetModality_ThumbsAttribute() {
        assertEquals(Modality.FINGERPRINT_SLAB_THUMBS, Modality.getModality("leftThumb"));
    }

    @Test
    public void testGetModality_IrisAttribute() {
        assertEquals(Modality.IRIS_DOUBLE, Modality.getModality("leftEye"));
    }

    @Test
    public void testGetModality_FaceAttribute() {
        assertEquals(Modality.FACE, Modality.getModality("face"));
    }

    @Test
    public void testGetModality_ExceptionPhotoAttribute() {
        assertEquals(Modality.EXCEPTION_PHOTO, Modality.getModality("unknown"));
    }

    @Test
    public void testGetModality_NullForUnknownAttribute() {
        assertNull(Modality.getModality("unknownAttribute"));
    }

    @Test
    public void testGetSpecBioSubType_SingleAttribute() {
        List<String> attributes = Collections.singletonList("leftIndex");
        List<String> result = Modality.getSpecBioSubType(attributes);
        assertEquals(Collections.singletonList(RegistrationConstants.LEFT_INDEX_FINGER), result);
    }

    @Test
    public void testGetSpecBioSubType_MultipleAttributes() {
        List<String> attributes = Arrays.asList("leftIndex", "rightThumb", "leftEye");
        List<String> result = Modality.getSpecBioSubType(attributes);
        assertEquals(Arrays.asList(
                RegistrationConstants.LEFT_INDEX_FINGER,
                RegistrationConstants.RIGHT_THUMB,
                RegistrationConstants.LEFT
        ), result);
    }

    @Test
    public void testGetSpecBioSubType_EmptyList() {
        List<String> attributes = Collections.emptyList();
        List<String> result = Modality.getSpecBioSubType(attributes);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetSpecBioSubType_SingleAttributeValue() {
        assertEquals(RegistrationConstants.LEFT_INDEX_FINGER, Modality.getSpecBioSubType("leftIndex"));
        assertEquals(RegistrationConstants.RIGHT_THUMB, Modality.getSpecBioSubType("rightThumb"));
        assertEquals(RegistrationConstants.LEFT, Modality.getSpecBioSubType("leftEye"));
        assertEquals("Face", Modality.getSpecBioSubType("face"));
    }

    @Test
    public void testGetSpecBioSubType_EmptyForUnknown() {
        assertEquals("", Modality.getSpecBioSubType("unknown"));
    }

    @Test
    public void testGetBioAttribute() {
        assertEquals("leftIndex", Modality.getBioAttribute(RegistrationConstants.LEFT_INDEX_FINGER));
        assertEquals("rightThumb", Modality.getBioAttribute(RegistrationConstants.RIGHT_THUMB));
        assertEquals("leftEye", Modality.getBioAttribute(RegistrationConstants.LEFT));
        assertEquals("", Modality.getBioAttribute("Face"));
    }

    @Test
    public void testGetBioAttribute_NullInput() {
        assertEquals("", Modality.getBioAttribute(null));
    }

    @Test
    public void testGetBioAttribute_UnknownInput() {
        assertEquals("unknown", Modality.getBioAttribute("unknown"));
    }

    @Test
    public void testGetFormatType_Finger() {
        // Assuming FORMAT_TYPE_FINGER = 1L (adjust if different)
        assertEquals(PacketManagerConstant.FORMAT_TYPE_FINGER, Modality.getFormatType(SingleType.FINGER));
    }

    @Test
    public void testGetFormatType_Face() {
        // Assuming FORMAT_TYPE_FACE = 2L (adjust if different)
        assertEquals(PacketManagerConstant.FORMAT_TYPE_FACE, Modality.getFormatType(SingleType.FACE));
        assertEquals(PacketManagerConstant.FORMAT_TYPE_FACE, Modality.getFormatType(SingleType.EXCEPTION_PHOTO));
    }

    @Test
    public void testGetFormatType_Iris() {
        // Assuming FORMAT_TYPE_IRIS = 3L (adjust if different)
        assertEquals(PacketManagerConstant.FORMAT_TYPE_IRIS, Modality.getFormatType(SingleType.IRIS));
    }
}