package com.shaw.dolores.interceptor;

import com.shaw.dolores.bo.User;
import com.shaw.dolores.utils.Constants;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class SecurityFilter implements Filter {
    private static final Set<String> NO_LOGIN_PAGE = new HashSet<>();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        NO_LOGIN_PAGE.add("/login");
        NO_LOGIN_PAGE.add("/logout");
        NO_LOGIN_PAGE.add("/index");
        NO_LOGIN_PAGE.add("/fromGithub");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest servletRequest = (HttpServletRequest) request;
        HttpServletResponse servletResponse = (HttpServletResponse) response;
        String uri = servletRequest.getRequestURI();
        if (!NO_LOGIN_PAGE.contains(uri) && !isResourceFile(uri)) {
            User user = (User) servletRequest.getSession().getAttribute(Constants.HTTP_SESSION_USER);
            if (user == null) {
                servletResponse.sendRedirect("/login");
                return;
            }
        }
        chain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {
        //do nothing
    }

    private boolean isResourceFile(String url) {
        return url.endsWith(".js")
                || url.endsWith(".css")
                || url.endsWith(".jpg")
                || url.endsWith(".gif")
                || url.endsWith(".png")
                || url.endsWith(".html")
                || url.endsWith(".eot")
                || url.endsWith(".svg")
                || url.endsWith(".ttf")
                || url.endsWith(".woff")
                || url.endsWith(".ico")
                || url.endsWith(".txt")
                || url.endsWith(".woff2");
    }
}
