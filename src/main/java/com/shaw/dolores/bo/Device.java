package com.shaw.dolores.bo;


import com.shaw.dolores.utils.TimeUtils;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Table(name = "device")
@Entity
@Data
public class Device implements Serializable {
    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    private String id;

    private String name;

    @Column(name = "user_id")
    private int userId;

    @Column(name = "create_time")
    private long createTime;
    @Column(name = "update_time")
    private long updateTime;


    public Map<String, Object> convertToVo(User user) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("name", name);
        map.put("user", user.getName());
        map.put("createTime", TimeUtils.formatDate(createTime));
        map.put("updateTime", TimeUtils.formatDate(updateTime));
        return map;
    }
}
