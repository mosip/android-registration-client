package io.mosip.registration.clientmanager.util;

import android.content.Context;

import java.text.DateFormat;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MMM-dd");
    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm:ss");

    DateFormat dateFormat;
    DateFormat timeFormat;

    public DateUtil(Context context) {
        dateFormat = android.text.format.DateFormat.getMediumDateFormat(context);
        timeFormat = android.text.format.DateFormat.getTimeFormat(context);
    }

    public String getDateTime(long dateTimeMillis) {
        Date date = new Date(dateTimeMillis);

        String dateStr = DATE_FORMAT.format(date);
        String timeStr = TIME_FORMAT.format(date);

        return String.format("%s %s", dateStr, timeStr);
    }
}
