package com.shaw.dolores.controller;

import com.alibaba.fastjson.JSON;
import com.shaw.dolores.annotation.OAuthPassport;
import com.shaw.dolores.bo.Meta;
import com.shaw.dolores.bo.User;
import com.shaw.dolores.dao.MetaRepository;
import com.shaw.dolores.utils.*;
import com.shaw.dolores.vo.SessionData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

/**
 * @author imn5100
 */
@Controller
@RequestMapping("/user")
public class UserController {
    @Value("${dolores.gitHub.client_id}")
    private String GITHUB_CLIENT_ID;
    @Value("${dolores.subscribe.prefix}")
    private String subPrefix;
    private static final String DEFAULT_SCOPE = "";
    private static final String GITHUB_AUTHORIZE_URL = "http://github.com/login/oauth/authorize";
    @Autowired
    private MetaRepository metaRepository;


    @RequestMapping("/fromGithub")
    public ModelAndView loginByGithub(@RequestParam(name = "redirect") String redirect, HttpSession session) throws Exception {
        ModelAndView mav = new ModelAndView();
        User user = (User) session.getAttribute(Constants.HTTP_SESSION_USER);
        if (user != null) {
            mav.setViewName("redirect:/");
        } else {
            String redirectUri = GITHUB_AUTHORIZE_URL + "?" +
                    "client_id=" + GITHUB_CLIENT_ID + "&" +
                    "scope=" + DEFAULT_SCOPE + "&" +
                    "redirect_uri=" + redirect + "&" +
                    "state=" + DesUtils.getDefaultInstance().encrypt(redirect) + "&";
            mav.setViewName("redirect:" + redirectUri);
        }
        return mav;
    }

    @RequestMapping(value = "/token", method = RequestMethod.POST)
    @ResponseBody
    @OAuthPassport
    public ResponseDataholder getConnectToken(HttpSession session, String driverId) {
        User user = (User) session.getAttribute(Constants.HTTP_SESSION_USER);
        if (user != null && user.getId() != null && Utils.isNotEmpty(driverId)) {
            if (metaRepository.countByOwnerAndExpireTimeAfter(user.getId(), System.currentTimeMillis()) > Constants.MAX_TOKEN) {
                return ResponseDataholder.fail(ResponseCode.TRY_LATER);
            }
            SessionData sessionData = new SessionData();
            String sessionId = Utils.generateSessionId();
            String token = Utils.generateToken();
            sessionData.setToken(token);
            sessionData.setTopicList(Collections.singletonList(subPrefix + driverId));
            sessionData.setSessionId(sessionId);
            sessionData.setUserName(user.getName());
            Meta meta = Meta.of(Utils.generateToken(), JSON.toJSONString(session), user.getId(), 5, TimeUnit.MINUTES);
            metaRepository.save(meta);
            return ResponseDataholder.success(session);
        }
        return ResponseDataholder.fail(ResponseCode.NOT_LOGIN);
    }
}
