package com.shaw.dolores.controller;


import com.shaw.dolores.bo.User;
import com.shaw.dolores.utils.Constants;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;

@Controller
public class IndexController {
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public ModelAndView loginPage(HttpSession session) {
        User user = (User) session.getAttribute(Constants.HTTP_SESSION_USER);
        if (user != null && user.getId() != null) {
            return new ModelAndView("redirect:" + "/home");
        }
        return new ModelAndView("login");
    }
}
