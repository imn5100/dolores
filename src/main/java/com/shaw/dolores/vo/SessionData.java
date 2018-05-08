package com.shaw.dolores.vo;

import com.shaw.dolores.utils.TimeUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@EqualsAndHashCode
public class SessionData implements Serializable {
    private String token;
    private List<String> topicList;
    private long expireTime = 12 * 60 * 60 * 1000;
    private String sessionId;
    private int userId;
    private String deviceId;
    private String deviceName;
    private Date connectTime;

    public void setExpireTime(long expireTime) {
        if (expireTime > 0) {
            this.expireTime = expireTime;
        }
    }

    public Map<String, Object> convert2Vo() {
        Map<String, Object> map = new HashMap<>();
        map.put("deviceName", deviceName);
        map.put("deviceId", deviceId);
        map.put("sessionId", sessionId);
        map.put("connectTime", TimeUtils.formatDate(connectTime));
        map.put("expireTime", TimeUtils.formatDate(expireTime));
        return map;
    }

}
