package com.shaw.dolores.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;
import org.springframework.web.socket.handler.WebSocketHandlerDecorator;
import org.springframework.web.socket.handler.WebSocketHandlerDecoratorFactory;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig extends AbstractWebSocketMessageBrokerConfigurer {
    @Autowired
    private HttpHandshakeInterceptor httpHandshakeInterceptor;
    @Autowired
    private SubscribeChannelInterceptor subscribeChannelInterceptor;

    @Bean
    public SessionHandler sessionHandler() {
        return SessionHandler.getInstance();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/dolores");
        config.setApplicationDestinationPrefixes("/dolores");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/dolores").addInterceptors(httpHandshakeInterceptor)
                .setAllowedOrigins("*")
                .withSockJS()
                .setStreamBytesLimit(512 * 1024)
                .setHttpMessageCacheSize(1000)
                .setDisconnectDelay(30 * 1000);
        registry.setErrorHandler(new PayloadStompSubProtocolErrorHandler());
    }


    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(subscribeChannelInterceptor);
    }

    @Override
    public void configureWebSocketTransport(final WebSocketTransportRegistration registration) {
        registration.addDecoratorFactory(new WebSocketHandlerDecoratorFactory() {
            @Override
            public WebSocketHandler decorate(final WebSocketHandler handler) {
                SessionHandler sessionHandler = sessionHandler();
                return new WebSocketHandlerDecorator(handler) {
                    @Override
                    public void afterConnectionEstablished(final WebSocketSession session) throws Exception {
                        sessionHandler.register(session);
                        super.afterConnectionEstablished(session);
                    }

                    @Override
                    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
                        sessionHandler.removeSessionData(session);
                        super.afterConnectionClosed(session, closeStatus);
                    }
                };
            }
        });
        super.configureWebSocketTransport(registration);
    }
}
