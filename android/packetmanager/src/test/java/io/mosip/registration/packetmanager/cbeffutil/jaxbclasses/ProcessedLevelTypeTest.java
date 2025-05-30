package io.mosip.registration.packetmanager.cbeffutil.jaxbclasses;

import junit.framework.TestCase;

public class ProcessedLevelTypeTest extends TestCase {

    public void testEnumValuesAndValueMethod() {
        assertEquals("Raw", ProcessedLevelType.RAW.value());
        assertEquals("Intermediate", ProcessedLevelType.INTERMEDIATE.value());
        assertEquals("Processed", ProcessedLevelType.PROCESSED.value());
    }

    public void testFromValueValid() {
        assertEquals(ProcessedLevelType.RAW, ProcessedLevelType.fromValue("Raw"));
        assertEquals(ProcessedLevelType.RAW, ProcessedLevelType.fromValue("raw"));
        assertEquals(ProcessedLevelType.INTERMEDIATE, ProcessedLevelType.fromValue("Intermediate"));
        assertEquals(ProcessedLevelType.INTERMEDIATE, ProcessedLevelType.fromValue("INTERMEDIATE"));
        assertEquals(ProcessedLevelType.PROCESSED, ProcessedLevelType.fromValue("Processed"));
        assertEquals(ProcessedLevelType.PROCESSED, ProcessedLevelType.fromValue("processed"));
    }

    public void testFromValueInvalid() {
        try {
            ProcessedLevelType.fromValue("Unknown");
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertEquals("Unknown", e.getMessage());
        }
    }
}
