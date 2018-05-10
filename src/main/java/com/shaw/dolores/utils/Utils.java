package com.shaw.dolores.utils;

import org.springframework.util.AntPathMatcher;
import org.springframework.util.CollectionUtils;
import org.springframework.util.PathMatcher;
import org.springframework.util.StringUtils;

import java.security.MessageDigest;
import java.util.Collection;

public final class Utils {
    public static final PathMatcher pathMatcher = new AntPathMatcher();

    public static String generateToken() {
        return md5(String.valueOf(GidUtil.next()));
    }

    public static String generateSessionId() {
        return String.valueOf(GidUtil.next());
    }


    public static long parseLongQuietly(Object value, long def) {
        if (value != null) {
            if (value instanceof Number) {
                return ((Number) value).longValue();
            }
            try {
                return Long.valueOf(value.toString());
            } catch (Throwable ignored) {
            }
        }

        return def;
    }

    public static String md5(String value) {
        if (isNotEmpty(value)) {
            try {
                MessageDigest md = MessageDigest.getInstance("MD5");
                md.reset();
                md.update(value.getBytes("UTF-8"));
                return DesUtils.byteArr2HexStr(md.digest());
            } catch (Throwable var2) {
                var2.printStackTrace();
            }
        }

        return value;
    }

    public static boolean isEmpty(String value) {
        return value == null || value.isEmpty();
    }

    public static boolean isNotEmpty(String value) {
        return !isEmpty(value);
    }

    public static boolean topicValidity(Collection<String> topicWhitelists, String topic) {
        return !StringUtils.isEmpty(topic) && !CollectionUtils.isEmpty(topicWhitelists) && topicWhitelists.stream().anyMatch(pattern -> pathMatcher.match(pattern, topic));
    }

    public static String buildSubscribeUrl(String subPrefix, String driverId, int userId) {
        return subPrefix + userId + "/" + driverId;
    }

}
