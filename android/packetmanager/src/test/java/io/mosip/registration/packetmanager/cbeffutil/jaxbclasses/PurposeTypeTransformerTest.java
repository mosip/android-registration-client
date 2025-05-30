package io.mosip.registration.packetmanager.cbeffutil.jaxbclasses;

import junit.framework.TestCase;

public class PurposeTypeTransformerTest extends TestCase {

    public void testReadValid() throws Exception {
        PurposeTypeTransformer transformer = new PurposeTypeTransformer();
        assertEquals(PurposeType.VERIFY, transformer.read("Verify"));
        assertEquals(PurposeType.IDENTIFY, transformer.read("Identify"));
        assertEquals(PurposeType.ENROLL, transformer.read("Enroll"));
        assertEquals(PurposeType.ENROLL_VERIFY, transformer.read("EnrollVerify"));
        assertEquals(PurposeType.ENROLL_IDENTIFY, transformer.read("EnrollIdentify"));
        assertEquals(PurposeType.AUDIT, transformer.read("Audit"));
    }

    public void testReadInvalid() {
        PurposeTypeTransformer transformer = new PurposeTypeTransformer();
        try {
            transformer.read("Unknown");
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertEquals("Unknown", e.getMessage());
        } catch (Exception e) {
            fail("Expected IllegalArgumentException");
        }
    }

    public void testWrite() throws Exception {
        PurposeTypeTransformer transformer = new PurposeTypeTransformer();
        assertEquals("Verify", transformer.write(PurposeType.VERIFY));
        assertEquals("Identify", transformer.write(PurposeType.IDENTIFY));
        assertEquals("Enroll", transformer.write(PurposeType.ENROLL));
        assertEquals("EnrollVerify", transformer.write(PurposeType.ENROLL_VERIFY));
        assertEquals("EnrollIdentify", transformer.write(PurposeType.ENROLL_IDENTIFY));
        assertEquals("Audit", transformer.write(PurposeType.AUDIT));
    }
}
