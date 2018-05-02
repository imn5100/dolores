package com.shaw.dolores.websocket;

import com.alibaba.fastjson.JSONObject;
import com.shaw.dolores.utils.Constants;
import com.shaw.dolores.utils.Utils;
import com.shaw.dolores.vo.SessionData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptorAdapter;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

@Component
public class SubscribeChannelInterceptor extends ChannelInterceptorAdapter {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public Message<?> preSend(Message message, MessageChannel channel) throws IllegalArgumentException {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(message);
        if (StompCommand.SUBSCRIBE.equals(headerAccessor.getCommand())) {
            String sessionId = (String) headerAccessor.getSessionAttributes().get(Constants.SESSION_ID);
            SessionData sessionData = JSONObject.parseObject(stringRedisTemplate.opsForValue().get(sessionId), SessionData.class);
            if (sessionData == null
                    || CollectionUtils.isEmpty(sessionData.getTopicList())
                    || !Utils.topicValidity(sessionData.getTopicList(), headerAccessor.getDestination())) {
                throw new MessagingException("No permission for this topic");
            }
        }
        return message;
    }
}
