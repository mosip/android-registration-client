package io.mosip.registration.packetmanager.util;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

public class DateUtils {

    private static final String TAG = DateUtils.class.getSimpleName();
    private static final TimeZone UTC_TIME_ZONE = TimeZone.getTimeZone("UTC");
    /**
     * Default UTC ZoneId.
     */
    private static final ZoneId UTC_ZONE_ID = ZoneId.of("UTC");
    /**
     * Default UTC pattern.
     */
    private static final String UTC_DATETIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    private static final String UTC_DATETIME_PATTERN_WITHOUT_MILLIS = "yyyy-MM-dd'T'HH:mm:ss'Z'";

    public static LocalDateTime parseUTCToLocalDateTime(String utcDateTime, String pattern)
            throws Exception {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        simpleDateFormat.setTimeZone(UTC_TIME_ZONE);
        try {
            return simpleDateFormat.parse(utcDateTime).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        } catch (ParseException e) {
            Log.e(TAG, "DATE_PARSE_ERROR", e);
            throw new Exception("DATE_PARSE_ERROR");
        }
    }

    public static String formatToISOString(LocalDateTime localDateTime) {
        return localDateTime.format(DateTimeFormatter.ofPattern(UTC_DATETIME_PATTERN));
    }

    public static String formatToISOStringWithoutMillis(LocalDateTime localDateTime) {
        return localDateTime.format(DateTimeFormatter.ofPattern(UTC_DATETIME_PATTERN_WITHOUT_MILLIS));
    }

    public static String parseEpochToISOString(long epochInMillis) {
        LocalDateTime localDateTime = LocalDateTime.ofEpochSecond(epochInMillis/1000, 0, ZoneOffset.UTC);
        return localDateTime.format(DateTimeFormatter.ofPattern(UTC_DATETIME_PATTERN));
    }

    public static LocalDateTime getUTCCurrentDateTime() {
        return ZonedDateTime.now(ZoneOffset.UTC).toLocalDateTime();
    }

}
