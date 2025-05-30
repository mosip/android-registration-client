package io.mosip.registration.packetmanager.cbeffutil.jaxbclasses;

import junit.framework.TestCase;

public class ProcessedLevelTypeTransformerTest extends TestCase {

    public void testReadValid() throws Exception {
        ProcessedLevelTypeTransformer transformer = new ProcessedLevelTypeTransformer();
        assertEquals(ProcessedLevelType.RAW, transformer.read("Raw"));
        assertEquals(ProcessedLevelType.INTERMEDIATE, transformer.read("Intermediate"));
        assertEquals(ProcessedLevelType.PROCESSED, transformer.read("Processed"));
    }

    public void testReadInvalid() {
        ProcessedLevelTypeTransformer transformer = new ProcessedLevelTypeTransformer();
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
        ProcessedLevelTypeTransformer transformer = new ProcessedLevelTypeTransformer();
        assertEquals("Raw", transformer.write(ProcessedLevelType.RAW));
        assertEquals("Intermediate", transformer.write(ProcessedLevelType.INTERMEDIATE));
        assertEquals("Processed", transformer.write(ProcessedLevelType.PROCESSED));
    }
}
