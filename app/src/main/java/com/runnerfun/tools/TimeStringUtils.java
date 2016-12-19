package com.runnerfun.tools;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * TimeStringUtils
 * Created by andrie on 09/12/2016.
 */

public class TimeStringUtils {
    private static SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());

    public static String getTime(long ms) {
        format.setTimeZone(TimeZone.getTimeZone(TimeZone.getDefault().getDisplayName()));
        return format.format(new Date(ms));
    }
}
