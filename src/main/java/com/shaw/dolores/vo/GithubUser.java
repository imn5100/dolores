package com.shaw.dolores.vo;

import com.shaw.dolores.bo.User;
import com.shaw.dolores.utils.Utils;

import java.io.Serializable;

public class GithubUser implements Serializable {
    /**
     * {
     * "login": "imn5100",
     * "id": 13531995,
     * "avatar_url": "https://avatars2.githubusercontent.com/u/13531995?v=4",
     * "gravatar_id": "",
     * "url": "https://api.github.com/users/imn5100",
     * "html_url": "https://github.com/imn5100",
     * "followers_url": "https://api.github.com/users/imn5100/followers",
     * "following_url": "https://api.github.com/users/imn5100/following{/other_user}",
     * "gists_url": "https://api.github.com/users/imn5100/gists{/gist_id}",
     * "starred_url": "https://api.github.com/users/imn5100/starred{/owner}{/repo}",
     * "subscriptions_url": "https://api.github.com/users/imn5100/subscriptions",
     * "organizations_url": "https://api.github.com/users/imn5100/orgs",
     * "repos_url": "https://api.github.com/users/imn5100/repos",
     * "events_url": "https://api.github.com/users/imn5100/events{/privacy}",
     * "received_events_url": "https://api.github.com/users/imn5100/received_events",
     * "type": "User",
     * "site_admin": false,
     * "name": "Shaw",
     * "company": null,
     * "blog": "https://shawblog.me",
     * "location": "China",
     * "email": null,
     * "hireable": null,
     * "bio": null,
     * "public_repos": 13,
     * "public_gists": 0,
     * "followers": 2,
     * "following": 1,
     * "created_at": "2015-07-28T03:44:01Z",
     * "updated_at": "2017-08-17T02:52:49Z"
     * }
     */
    private Integer id;
    private String login;
    private String name;
    private String email;
    private String avatar_url;
    private String location;
    private String moreInfo;

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId(Integer id) {
        return this.id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAvatar_url() {
        return avatar_url;
    }

    public void setAvatar_url(String avatar_url) {
        this.avatar_url = avatar_url;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getMoreInfo() {
        return moreInfo;
    }

    public void setMoreInfo(String moreInfo) {
        this.moreInfo = moreInfo;
    }

    public boolean valid() {
        return id != null && Utils.isNotEmpty(login);
    }

    public User toUser() {
        User visitor = new User();
        visitor.setAccount(login);
        visitor.setName(name);
        visitor.setAvatarUrl(avatar_url);
        visitor.setEmail(email);
        visitor.setOauthFrom(User.OAUTH_FROM_GITHUB);
        visitor.setThirdId(String.valueOf(id));
        visitor.setMoreInfo(moreInfo);
        return visitor;
    }
}
