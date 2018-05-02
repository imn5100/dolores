package com.shaw.dolores.utils;

import java.io.File;
import java.util.Calendar;
import java.util.concurrent.atomic.AtomicInteger;

public final class GidUtil {
    private static final AtomicInteger SERIAL = new AtomicInteger(2147483647);
    private static final int SHIFTS_FOR_TIMESTAMP = 31;
    private static final int MASK_FOR_PARTITION_AND_SERIAL = 2147483647;
    private static final int SHIFTS_FOR_PARTITION = 18;
    private static final int PARTITION;
    private static final int MASK_FOR_SERIAL = 262143;
    private static final int SERVER_ID = 1;

    public GidUtil() {
    }

    public static long next() {
        return next((System.currentTimeMillis() / 1000L));
    }

    public static long next(long timeInSeconds, AtomicInteger serial) {
        return min(timeInSeconds) | (long) PARTITION | (long) (serial.incrementAndGet() & 262143);
    }

    public static long next(long timeInSeconds) {
        return min(timeInSeconds) | (long) PARTITION | (long) (SERIAL.incrementAndGet() & 262143);
    }

    public static long getTimeInSeconds(long id) {
        return id >> 31;
    }

    public static int getServerId(long id) {
        return (int) ((id & (long) PARTITION) >> 18);
    }

    public static long min(long timeInSeconds) {
        return timeInSeconds << 31;
    }

    public static long max(long timeInSeconds) {
        return min(timeInSeconds) | 2147483647L;
    }

    public static String getFilePath(long id) {
        return getFilePath("", id);
    }

    public static String getFilePath(String base, long id) {
        StringBuilder buff = new StringBuilder();
        if (Utils.isNotEmpty(base)) {
            buff.append(base);
            if (base.charAt(base.length() - 1) != File.separatorChar) {
                buff.append(File.separatorChar);
            }
        }

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(getTimeInSeconds(id) * 1000L);
        buff.append(cal.get(1));
        appendFilePathSegment(buff, cal.get(2) + 1);
        appendFilePathSegment(buff, cal.get(5));
        appendFilePathSegment(buff, (int) (id % 100L));
        return buff.toString();
    }

    private static void appendFilePathSegment(StringBuilder buff, int value) {
        buff.append(File.separatorChar);
        if (value < 10) {
            buff.append('0');
        }

        buff.append(value);
    }

    static {
        PARTITION = (SERVER_ID & 9010) << 18;
    }
}