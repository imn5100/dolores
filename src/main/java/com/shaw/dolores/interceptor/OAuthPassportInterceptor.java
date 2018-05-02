package com.shaw.dolores.interceptor;

import com.alibaba.fastjson.JSONObject;
import com.shaw.dolores.annotation.OAuthPassport;
import com.shaw.dolores.bo.User;
import com.shaw.dolores.service.UserService;
import com.shaw.dolores.utils.Constants;
import com.shaw.dolores.utils.Utils;
import com.shaw.dolores.vo.GithubUser;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@Log4j
public class OAuthPassportInterceptor extends HandlerInterceptorAdapter {
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private UserService userService;


    @Value("${dolores.gitHub.client_id}")
    private String GITHUB_CLIENT_ID;
    @Value("${dolores.gitHub.client_secret}")
    private String GITHUB_CLIENT_SECRET;
    private static final String GITHUB_TOKEN_URL = "https://github.com/login/oauth/access_token";
    private static final String GITHUB_USER_URL = "https://api.github.com/user";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        if (handler.getClass().isAssignableFrom(HandlerMethod.class)) {
            OAuthPassport oAuthPassport = ((HandlerMethod) handler).getMethodAnnotation(OAuthPassport.class);
            if (oAuthPassport == null || !oAuthPassport.validate()) {
                return true;
            } else {
                User visitor = (User) request.getSession().getAttribute(Constants.HTTP_SESSION_USER);
                if (visitor == null) {
                    String code = request.getParameter("code");
                    String state = request.getParameter("state");
                    if (Utils.isNotEmpty(code) && Utils.isNotEmpty(state)) {
                        try {
                            String token = getToken(code, state);
                            if (Utils.isNotEmpty(token)) {
                                GithubUser githubUser = getUser(token);
                                if (githubUser != null && githubUser.valid()) {
                                    visitor = userService.updateOrSave(githubUser.toUser());
                                    if (visitor != null) {
                                        request.getSession().setAttribute(Constants.HTTP_SESSION_USER, visitor);
                                        return true;
                                    }
                                }
                            }
                        } catch (Exception e) {
                            log.error("OAuth Login Fail:", e);
                        }
                        response.sendRedirect("/login");
                        return false;
                    }
                }
            }
        }
        return true;
    }

    //通过回调的code和state获取访问github的token
    private String getToken(String code, String state) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);
        MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();
        params.add("client_id", GITHUB_CLIENT_ID);
        params.add("client_secret", GITHUB_CLIENT_SECRET);
        params.add("code", code);
        if (Utils.isNotEmpty(state)) {
            params.add("state", state);
        }
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(params, headers);
        ResponseEntity<String> responseEntity = restTemplate.exchange(GITHUB_TOKEN_URL, HttpMethod.POST, requestEntity, String.class);
        if (responseEntity != null && responseEntity.getStatusCode() == HttpStatus.OK && responseEntity.getBody() != null) {
            JSONObject jsonObject = JSONObject.parseObject(responseEntity.getBody());
            return jsonObject.getString("access_token");
        } else {
            if (responseEntity != null)
                log.warn(responseEntity.toString());
            else
                log.warn("Get Access_Token Response is NULL");
            return null;
        }
    }

    //通过token获取github用户信息
    private GithubUser getUser(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);
        if (Utils.isNotEmpty(token)) {
            headers.add("Authorization", "token " + token);
            HttpEntity<?> userRequestEntity = new HttpEntity<>(headers);
            ResponseEntity<String> userResponse = restTemplate.exchange(GITHUB_USER_URL, HttpMethod.GET, userRequestEntity, String.class);
            if (userResponse != null && userResponse.getStatusCode() == HttpStatus.OK && userResponse.getBody() != null) {
                GithubUser githubUser = JSONObject.parseObject(userResponse.getBody(), GithubUser.class);
                if (githubUser != null && githubUser.valid()) {
                    githubUser.setMoreInfo(userResponse.getBody());
                    return githubUser;
                }
            }
        }
        return null;
    }
}