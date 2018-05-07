package com.shaw.dolores.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public final class Constants {
    public static final String SUBSCRIBE_SET_REDIS_KEY = "web-push:subscribe_hash";
    public static final int TOKEN_EXPIRE_TIME_MIN = 5;
    public static final int MAX_TOKEN = 10;

    public static final String HTTP_SESSION_USER = "http_session_user";
    public static final long DEFAULT_EXPIRE_TIME = 1000 * 60 * 60 * 4;
    public static final String TOKEN_DATA = "tokenData";
    public static final String SESSION_ID = "sessionId";
    public static final String EXPIRE_TIME = "expireTime";
    public static final String SUBSCRIBE_TOPIC = "subscribe_topic";

    public static String COMMON_FORMAT_STR = "yyyy-MM-dd HH:mm:ss";
    public static String DAY_FORMAT_STR = "yyyy-MM-dd";

}
