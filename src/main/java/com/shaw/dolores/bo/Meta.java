package com.shaw.dolores.bo;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.concurrent.TimeUnit;

@Table(name = "meta")
@Entity
@Data
public class Meta implements Serializable {
    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    private Integer id;

    private String name;

    private String value;

    private int owner;

    @Column(name = "create_time")
    private long createTime;
    @Column(name = "update_time")
    private long updateTime;
    @Column(name = "expire_time")
    private long expireTime;


    public static Meta of(String key, String value, int owner, long expireTime, TimeUnit timeUnit) {
        Meta meta = new Meta();
        meta.setName(key);
        meta.setOwner(owner);
        meta.setValue(value);
        long currentTimeMillis = System.currentTimeMillis();
        meta.setExpireTime(currentTimeMillis + timeUnit.toMillis(expireTime));
        meta.setUpdateTime(currentTimeMillis);
        meta.setCreateTime(currentTimeMillis);
        return meta;
    }
}
