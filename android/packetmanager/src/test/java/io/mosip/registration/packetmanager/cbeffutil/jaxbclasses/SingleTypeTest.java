package io.mosip.registration.packetmanager.cbeffutil.jaxbclasses;

import junit.framework.TestCase;

public class SingleTypeTest extends TestCase {

    public void testEnumValuesAndValueMethod() {
        assertEquals("Scent", SingleType.SCENT.value());
        assertEquals("DNA", SingleType.DNA.value());
        assertEquals("Ear ", SingleType.EAR.value());
        assertEquals("Face", SingleType.FACE.value());
        assertEquals("Finger", SingleType.FINGER.value());
        assertEquals("Foot", SingleType.FOOT.value());
        assertEquals("Vein", SingleType.VEIN.value());
        assertEquals("HandGeometry", SingleType.HAND_GEOMETRY.value());
        assertEquals("Iris", SingleType.IRIS.value());
        assertEquals("Retina", SingleType.RETINA.value());
        assertEquals("Voice", SingleType.VOICE.value());
        assertEquals("Gait", SingleType.GAIT.value());
        assertEquals("Keystroke", SingleType.KEYSTROKE.value());
        assertEquals("LipMovement", SingleType.LIP_MOVEMENT.value());
        assertEquals("SignatureSign", SingleType.SIGNATURE_SIGN.value());
        assertEquals("ExceptionPhoto", SingleType.EXCEPTION_PHOTO.value());
    }

    public void testFromValueValid() {
        assertEquals(SingleType.SCENT, SingleType.fromValue("Scent"));
        assertEquals(SingleType.DNA, SingleType.fromValue("dna"));
        assertEquals(SingleType.EAR, SingleType.fromValue("Ear "));
        assertEquals(SingleType.FACE, SingleType.fromValue("Face"));
        assertEquals(SingleType.FINGER, SingleType.fromValue("Finger"));
        assertEquals(SingleType.FOOT, SingleType.fromValue("Foot"));
        assertEquals(SingleType.VEIN, SingleType.fromValue("Vein"));
        assertEquals(SingleType.HAND_GEOMETRY, SingleType.fromValue("HandGeometry"));
        assertEquals(SingleType.IRIS, SingleType.fromValue("Iris"));
        assertEquals(SingleType.RETINA, SingleType.fromValue("Retina"));
        assertEquals(SingleType.VOICE, SingleType.fromValue("Voice"));
        assertEquals(SingleType.GAIT, SingleType.fromValue("Gait"));
        assertEquals(SingleType.KEYSTROKE, SingleType.fromValue("Keystroke"));
        assertEquals(SingleType.LIP_MOVEMENT, SingleType.fromValue("LipMovement"));
        assertEquals(SingleType.SIGNATURE_SIGN, SingleType.fromValue("SignatureSign"));
        assertEquals(SingleType.EXCEPTION_PHOTO, SingleType.fromValue("ExceptionPhoto"));
    }

    public void testFromValueInvalid() {
        try {
            SingleType.fromValue("UnknownType");
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertEquals("UnknownType", e.getMessage());
        }
    }
}
