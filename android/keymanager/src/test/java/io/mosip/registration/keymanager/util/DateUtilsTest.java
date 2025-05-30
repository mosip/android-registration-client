package io.mosip.registration.keymanager.util;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertThrows;

import org.junit.Test;

public class DateUtilsTest {

    @Test
    public void testParseDateToLocalDateTime() {
        Date now = new Date();
        LocalDateTime expected = now.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        LocalDateTime actual = DateUtils.parseDateToLocalDateTime(now);
        assertEquals(expected, actual);
    }

    @Test
    public void testParseDateToLocalDateTimeWithNull() {
        assertThrows(NullPointerException.class, () -> {
            DateUtils.parseDateToLocalDateTime(null);
        });
    }

    @Test
    public void testGetUTCCurrentDateTime() {
        LocalDateTime utcNow = DateUtils.getUTCCurrentDateTime();
        LocalDateTime systemNow = LocalDateTime.now(ZoneOffset.UTC);
        // Allow a small difference due to execution time
        assertTrue(!utcNow.isBefore(systemNow.minusSeconds(1)) && !utcNow.isAfter(systemNow.plusSeconds(1)));
    }

    @Test
    public void testDateUtilsConstructor() {
        // This covers the implicit default constructor
        new DateUtils();
    }
}
