package com.shaw.dolores.bo;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Table(name = "user")
@Entity
@Data
public class User implements Serializable {
    public static final int OAUTH_FROM_GITHUB = 1;
    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    private Integer id;

    private String account;

    private String name;

    @Column(name = "third_id")
    private String thirdId;

    private String email;

    @Column(name = "oauth_from")
    private Integer oauthFrom;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @Column(name = "create_time")
    private Long createTime;

    @Column(name = "update_time")
    private Long updateTime;

    @Column(name = "more_info")
    private String moreInfo;
}