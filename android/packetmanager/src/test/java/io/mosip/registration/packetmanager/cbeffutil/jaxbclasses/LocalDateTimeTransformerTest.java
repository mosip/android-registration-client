package io.mosip.registration.packetmanager.cbeffutil.jaxbclasses;

import junit.framework.TestCase;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class LocalDateTimeTransformerTest extends TestCase {

    public void testReadIsoDateTime() throws Exception {
        LocalDateTimeTransformer transformer = new LocalDateTimeTransformer();
        String isoString = "2024-06-01T12:34:56Z[UTC]";
        LocalDateTime result = transformer.read(isoString);
        assertEquals(LocalDateTime.of(2024, 6, 1, 12, 34, 56), result);
    }

    public void testWriteNonNull() throws Exception {
        LocalDateTimeTransformer transformer = new LocalDateTimeTransformer();
        LocalDateTime dateTime = LocalDateTime.of(2024, 6, 1, 12, 34, 56);
        String result = transformer.write(dateTime);
        // The output should be the ISO_INSTANT string
        assertEquals(dateTime.toInstant(ZoneOffset.UTC).toString(), result);
    }

    public void testWriteNull() throws Exception {
        LocalDateTimeTransformer transformer = new LocalDateTimeTransformer();
        String result = transformer.write(null);
        assertEquals("", result);
    }
}
