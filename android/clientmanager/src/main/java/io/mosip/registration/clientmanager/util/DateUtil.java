package io.mosip.registration.clientmanager.util;

import android.content.Context;

import java.text.DateFormat;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {

    DateFormat dateFormat;
    DateFormat timeFormat;

    public DateUtil(Context context) {
        dateFormat = android.text.format.DateFormat.getMediumDateFormat(context);
        timeFormat = android.text.format.DateFormat.getTimeFormat(context);
    }

    public String getDateTime(long dateTimeMillis) {
        Date date = new Date(dateTimeMillis);


        // Date format (yyyy-MM-dd) and time format (HH:mm:ss)
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MMM-dd");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");

        // These will automatically use the system's local timezone
        String dateStr = dateFormat.format(date);
        String timeStr = timeFormat.format(date);

        return String.format("%s %s", dateStr, timeStr);
    }
}
