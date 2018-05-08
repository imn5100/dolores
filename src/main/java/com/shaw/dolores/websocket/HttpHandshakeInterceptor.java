package com.shaw.dolores.websocket;

import com.alibaba.fastjson.JSONObject;
import com.shaw.dolores.bo.Meta;
import com.shaw.dolores.dao.MetaRepository;
import com.shaw.dolores.utils.Constants;
import com.shaw.dolores.utils.Utils;
import com.shaw.dolores.vo.SessionData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Date;
import java.util.Map;

@Component
public class HttpHandshakeInterceptor implements HandshakeInterceptor {
    @Autowired
    private MetaRepository metaRepository;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
                                   Map<String, Object> attributes) {
        if (request instanceof ServletServerHttpRequest) {
            ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;
            String token = servletRequest.getServletRequest().getParameter("token");
            Meta meta = metaRepository.findOneMetaByNameAndValid(token, System.currentTimeMillis());
            if (meta == null || Utils.isEmpty(meta.getValue())) {
                response.setStatusCode(HttpStatus.BAD_REQUEST);
                return false;
            }
            SessionData sessionData = JSONObject.parseObject(meta.getValue(), SessionData.class);
            if (sessionData == null) {
                response.setStatusCode(HttpStatus.BAD_REQUEST);
                return false;
            } else {
                sessionData.setExpireTime(System.currentTimeMillis() + sessionData.getExpireTime());
                sessionData.setConnectTime(new Date());
                attributes.put(Constants.SESSION_ID, sessionData.getSessionId());
                attributes.put(Constants.EXPIRE_TIME, sessionData.getExpireTime());
                attributes.put(Constants.SESSION_DATA, sessionData);
            }
        }
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
                               Exception ex) {
        ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;
        String token = servletRequest.getServletRequest().getParameter("token");
        metaRepository.updateMetaStatusByName(token, Meta.STATUS_USED);
    }
}
