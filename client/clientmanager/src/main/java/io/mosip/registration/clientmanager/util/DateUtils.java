package io.mosip.registration.clientmanager.util;

import io.mosip.registration.clientmanager.constant.ClientManagerError;
import io.mosip.registration.clientmanager.exception.ClientCheckedException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.TimeZone;

public class DateUtils {

    private static final TimeZone UTC_TIME_ZONE = TimeZone.getTimeZone("UTC");

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
}
