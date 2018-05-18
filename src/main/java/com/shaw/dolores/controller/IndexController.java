package com.shaw.dolores.controller;


import com.shaw.dolores.annotation.OAuthPassport;
import com.shaw.dolores.bo.User;
import com.shaw.dolores.dao.UserRepository;
import com.shaw.dolores.utils.Constants;
import com.shaw.dolores.utils.DesUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;

@Controller
public class IndexController {
    private static final String DEFAULT_SCOPE = "";
    private static final String GITHUB_AUTHORIZE_URL = "https://github.com/login/oauth/authorize";
    @Value("${dolores.gitHub.client_id}")
    private String GITHUB_CLIENT_ID;

    @RequestMapping(value = "/login")
    @OAuthPassport
    public ModelAndView loginPage(HttpSession session) {
        User user = (User) session.getAttribute(Constants.HTTP_SESSION_USER);
        if (user != null && user.getId() != null) {
            return new ModelAndView("redirect:" + "/user/home");
        }
        return new ModelAndView("login");
    }

    @RequestMapping(value = "/logout")
    public ModelAndView logout(HttpSession session) {
        session.removeAttribute(Constants.HTTP_SESSION_USER);
        return new ModelAndView("login");
    }

    @RequestMapping(value = "/fromGithub", method = RequestMethod.GET)
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

    @RequestMapping(value = "/ws_test", method = RequestMethod.GET)
    public ModelAndView webSocketPage() {
        return new ModelAndView("ws_test");
    }
}
