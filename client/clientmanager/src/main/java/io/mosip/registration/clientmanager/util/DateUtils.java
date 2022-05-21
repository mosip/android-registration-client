package io.mosip.registration.clientmanager.util;

import io.mosip.registration.clientmanager.constant.ClientManagerError;
import io.mosip.registration.clientmanager.exception.ClientCheckedException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.TimeZone;

public class DateUtils {

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
            throws ClientCheckedException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        simpleDateFormat.setTimeZone(UTC_TIME_ZONE);
        try {
            return simpleDateFormat.parse(utcDateTime).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        } catch (ParseException e) {
            throw new ClientCheckedException(ClientManagerError.DATE_PARSE_ERROR.getErrorCode(),
                    ClientManagerError.DATE_PARSE_ERROR.getErrorMessage(), e);
        }
    }

    public static String formatToISOString(LocalDateTime localDateTime) {
        return localDateTime.format(DateTimeFormatter.ofPattern(UTC_DATETIME_PATTERN));
    }

    public static String formatToISOStringWithoutMillis(LocalDateTime localDateTime) {
        return localDateTime.format(DateTimeFormatter.ofPattern(UTC_DATETIME_PATTERN_WITHOUT_MILLIS));
    }

    public static LocalDateTime getLocalDateTime() {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss");
        // set UTC time zone by using SimpleDateFormat class
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        //create another instance of the SimpleDateFormat class for local date format
        SimpleDateFormat ldf = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss");
        // declare and initialize a date variable which we return to the main method
        Date date = null;
        // use try catch block to parse date in UTC time zone
        try {
            // parsing date using SimpleDateFormat class
            date = ldf.parse( sdf.format(new Date()) );
        }
        // catch block for handling ParseException
        catch (java.text.ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
        // pass UTC date to main method.

        return date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

}
