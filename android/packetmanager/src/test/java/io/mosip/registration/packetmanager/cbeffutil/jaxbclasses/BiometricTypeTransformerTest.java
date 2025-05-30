package io.mosip.registration.packetmanager.cbeffutil.jaxbclasses;

import io.mosip.registration.packetmanager.dto.PacketWriter.BiometricType;
import junit.framework.TestCase;

public class BiometricTypeTransformerTest extends TestCase {

    private BiometricTypeTransformer transformer;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        transformer = new BiometricTypeTransformer();
    }

    public void testReadValidValue() throws Exception {
        // Assuming BiometricType has a value "Finger"
        BiometricType type = transformer.read("Finger");
        assertEquals(BiometricType.FINGER, type);
    }

    public void testReadInvalidValue() {
        try {
            transformer.read("INVALID_TYPE");
            fail("Expected IllegalArgumentException for invalid value");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    public void testWrite() throws Exception {
        String value = transformer.write(BiometricType.FINGER);
        assertEquals("Finger", value);
    }
}
