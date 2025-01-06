package io.mosip.registration.packetmanager.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.time.LocalDateTime;
import java.time.ZoneId;


public class DateUtilsTest {

    @Test
    public void testParseUTCToLocalDateTime() throws Exception {
        String utcDateTime = "2024-12-09T10:15:30.000Z";
        String pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
        LocalDateTime result = DateUtils.parseUTCToLocalDateTime(utcDateTime, pattern);
        assertEquals(LocalDateTime.parse("2024-12-09T15:45:30"), result);
    }

    @Test
    public void testParseUTCToLocalDateTime_InvalidFormat() {
        String utcDateTime = "Invalid-Date";
        String pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
        assertThrows(Exception.class, () -> DateUtils.parseUTCToLocalDateTime(utcDateTime, pattern));
    }

    @Test
    public void testFormatToISOString() {
        LocalDateTime dateTime = LocalDateTime.of(2024, 12, 9, 10, 15, 30);
        String expected = "2024-12-09T10:15:30.000Z";
        String result = DateUtils.formatToISOString(dateTime);
        assertEquals(expected, result);
    }

    @Test
    public void testFormatToISOStringWithoutMillis() {
        LocalDateTime dateTime = LocalDateTime.of(2024, 12, 9, 10, 15, 30);
        String expected = "2024-12-09T10:15:30Z";
        String result = DateUtils.formatToISOStringWithoutMillis(dateTime);
        assertEquals(expected, result);
    }

    @Test
    public void testParseEpochToISOString() {
        long epochInMillis = 1733741730000L;
        String expected = "2024-12-09T10:55:30.000Z";
        String result = DateUtils.parseEpochToISOString(epochInMillis);
        assertEquals(expected, result);
    }

    @Test
    public void testGetUTCCurrentDateTime() {
        LocalDateTime utcNow = DateUtils.getUTCCurrentDateTime();
        assertNotNull(utcNow);

        LocalDateTime now = LocalDateTime.now(ZoneId.of("UTC"));
        assertTrue(utcNow.isBefore(now.plusSeconds(1)) && utcNow.isAfter(now.minusSeconds(1)));
    }
}
