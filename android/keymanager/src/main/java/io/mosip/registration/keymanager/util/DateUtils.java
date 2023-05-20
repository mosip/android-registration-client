package io.mosip.registration.keymanager.util;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;

public class DateUtils {

    public static LocalDateTime parseDateToLocalDateTime(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    public static LocalDateTime getUTCCurrentDateTime() {
        return ZonedDateTime.now(ZoneOffset.UTC).toLocalDateTime();
    }
}
