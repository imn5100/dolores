package com.shaw.dolores.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class SessionData implements Serializable {
    private String token;
    private List<String> topicList;
    private long expireTime = 12 * 60 * 60 * 1000;
    private String sessionId;
    private String userName;

    public void setExpireTime(long expireTime) {
        if (expireTime > 0) {
            this.expireTime = expireTime;
        }
    }

}
