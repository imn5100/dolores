package com.shaw.dolores.bo;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;

@Table(name = "task")
@Entity
@Data
public class Task implements Serializable {
    public static final String TOPIC_DOWNLOAD = "download";
    public static final String TOPIC_PYTHON_SCRIPT = "python";
    public static final int STATUS_INIT = 0;
    public static final int STATUS_SEND = 1;
    public static final int STATUS_FAIL = 2;

    public static final HashSet<String> TOPIC_SET = new HashSet<>(Arrays.asList(TOPIC_DOWNLOAD, TOPIC_PYTHON_SCRIPT));
    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    private Integer id;
    private String topic;
    private String contents;
    private int owner;
    private String remark = "";
    private int status;
    @Column(name = "session_id")
    private String sessionId;
    @Column(name = "create_time")
    private long createTime;
    @Column(name = "update_time")
    private long updateTime;

}
