package com.shaw.dolores.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtils {
    public static String formatDate(long milliseconds, String format) {
        return formatDate(new Date(milliseconds), format);
    }

    public static String formatDate(long milliseconds) {
        return formatDate(new Date(milliseconds), null);
    }

    public static String formatDate(Date date) {
        return formatDate(date, null);
    }

    public static String formatDate(Date date, String format) {
        if (Utils.isEmpty(format)) {
            return Constants.COMMON_FORMAT.format(date);
        }
        return new SimpleDateFormat(format).format(date);
    }
}
