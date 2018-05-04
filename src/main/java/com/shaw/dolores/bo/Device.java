package com.shaw.dolores.bo;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

@Table(name = "device")
@Entity
@Data
public class Device implements Serializable {
    private String id;

    private String name;

    @Column(name = "user_id")
    private int userId;

    @Column(name = "create_time")
    private long createTime;
    @Column(name = "update_time")
    private long updateTime;

}
