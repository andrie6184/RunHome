package com.runnerfun.tools;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * TimeStringUtils
 * Created by andrie on 09/12/2016.
 */

public class TimeStringUtils {
    private static SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");

    public static String getTime(long ms) {
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        return format.format(new Date(ms));
    }
}
