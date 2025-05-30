package io.mosip.registration.packetmanager.cbeffutil.jaxbclasses;

import junit.framework.TestCase;

public class SingleAnySubtypeTypeTest extends TestCase {

    public void testEnumValuesAndValueMethod() {
        assertEquals("Left", SingleAnySubtypeType.LEFT.value());
        assertEquals("Right", SingleAnySubtypeType.RIGHT.value());
        assertEquals("Thumb", SingleAnySubtypeType.THUMB.value());
        assertEquals("IndexFinger", SingleAnySubtypeType.INDEX_FINGER.value());
        assertEquals("MiddleFinger", SingleAnySubtypeType.MIDDLE_FINGER.value());
        assertEquals("RingFinger", SingleAnySubtypeType.RING_FINGER.value());
        assertEquals("LittleFinger", SingleAnySubtypeType.LITTLE_FINGER.value());
    }

    public void testFromValueValid() {
        assertEquals(SingleAnySubtypeType.LEFT, SingleAnySubtypeType.fromValue("Left"));
        assertEquals(SingleAnySubtypeType.RIGHT, SingleAnySubtypeType.fromValue("right"));
        assertEquals(SingleAnySubtypeType.THUMB, SingleAnySubtypeType.fromValue("Thumb"));
        assertEquals(SingleAnySubtypeType.INDEX_FINGER, SingleAnySubtypeType.fromValue("IndexFinger"));
        assertEquals(SingleAnySubtypeType.MIDDLE_FINGER, SingleAnySubtypeType.fromValue("MiddleFinger"));
        assertEquals(SingleAnySubtypeType.RING_FINGER, SingleAnySubtypeType.fromValue("RingFinger"));
        assertEquals(SingleAnySubtypeType.LITTLE_FINGER, SingleAnySubtypeType.fromValue("LittleFinger"));
    }

    public void testFromValueInvalid() {
        try {
            SingleAnySubtypeType.fromValue("Unknown");
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertEquals("Unknown", e.getMessage());
        }
    }
}
