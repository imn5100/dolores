package com.shaw.dolores.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MessageController {
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @MessageMapping({"/hello"})
    public void receiver(@Payload String message, SimpMessageHeaderAccessor headerAccessor) {
        String sessionId = headerAccessor.getSessionAttributes().get("sessionId").toString();
        System.out.println(sessionId);
        headerAccessor.getSessionId();
        System.out.println(headerAccessor.getSessionId());
//        messagingTemplate.convertAndSend("/dolores/driver/*", message);
    }

}
