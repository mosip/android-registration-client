package io.mosip.registration.packetmanager.cbeffutil.jaxbclasses;

import junit.framework.TestCase;
import java.util.Base64;

public class ByteArrayTransformerTest extends TestCase {

    public void testWriteNonNull() throws Exception {
        ByteArrayTransformer transformer = new ByteArrayTransformer();
        byte[] data = new byte[] {1, 2, 3, 4};
        // Simulate expected Base64 string without padding
        String expected = Base64.getEncoder().encodeToString(data).replaceAll("=+$", "");
        String result = transformer.write(data);
        assertEquals(expected, result);
    }

    public void testWriteNull() throws Exception {
        ByteArrayTransformer transformer = new ByteArrayTransformer();
        assertEquals("", transformer.write(null));
    }

    public void testRead() throws Exception {
        ByteArrayTransformer transformer = new ByteArrayTransformer();
        byte[] data = new byte[] {10, 20, 30, 40};
        String encoded = Base64.getEncoder().encodeToString(data);
        byte[] result = transformer.read(encoded);
        // Compare arrays
        assertEquals(data.length, result.length);
        for (int i = 0; i < data.length; i++) {
            assertEquals(data[i], result[i]);
        }
    }
}
