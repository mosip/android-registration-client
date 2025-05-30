package io.mosip.registration.packetmanager.cbeffutil.jaxbclasses;

import junit.framework.TestCase;

public class PurposeTypeTest extends TestCase {

    public void testEnumValuesAndValueMethod() {
        assertEquals("Verify", PurposeType.VERIFY.value());
        assertEquals("Identify", PurposeType.IDENTIFY.value());
        assertEquals("Enroll", PurposeType.ENROLL.value());
        assertEquals("EnrollVerify", PurposeType.ENROLL_VERIFY.value());
        assertEquals("EnrollIdentify", PurposeType.ENROLL_IDENTIFY.value());
        assertEquals("Audit", PurposeType.AUDIT.value());
    }

    public void testFromValueValid() {
        assertEquals(PurposeType.VERIFY, PurposeType.fromValue("Verify"));
        assertEquals(PurposeType.VERIFY, PurposeType.fromValue("verify"));
        assertEquals(PurposeType.IDENTIFY, PurposeType.fromValue("Identify"));
        assertEquals(PurposeType.ENROLL, PurposeType.fromValue("Enroll"));
        assertEquals(PurposeType.ENROLL_VERIFY, PurposeType.fromValue("EnrollVerify"));
        assertEquals(PurposeType.ENROLL_IDENTIFY, PurposeType.fromValue("EnrollIdentify"));
        assertEquals(PurposeType.AUDIT, PurposeType.fromValue("Audit"));
    }

    public void testFromValueInvalid() {
        try {
            PurposeType.fromValue("Unknown");
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertEquals("Unknown", e.getMessage());
        }
    }
}
