package io.mosip.registration.clientmanager.util;

import android.content.Context;

import java.text.DateFormat;
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
        String dateStr = dateFormat.format(date);
        String time = timeFormat.format(date);
        return String.format("%s %s", dateStr, time);
    }
}
