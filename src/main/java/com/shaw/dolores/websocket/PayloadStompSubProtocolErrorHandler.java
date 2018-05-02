package com.shaw.dolores.websocket;

import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.messaging.StompSubProtocolErrorHandler;

public class PayloadStompSubProtocolErrorHandler extends StompSubProtocolErrorHandler {

    @Override
    public Message<byte[]> handleClientMessageProcessingError(Message<byte[]> clientMessage, Throwable ex) {
        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.ERROR);
        accessor.setLeaveMutable(true);

        StompHeaderAccessor clientHeaderAccessor = null;
        if (clientMessage != null) {
            clientHeaderAccessor = MessageHeaderAccessor.getAccessor(clientMessage, StompHeaderAccessor.class);
            String receiptId = clientHeaderAccessor.getReceipt();
            if (receiptId != null) {
                accessor.setReceiptId(receiptId);
            }
        }
        return handleInternal(accessor, StringUtils.isEmpty(ex.getMessage()) ? new byte[0] : ex.getMessage().getBytes(), ex, clientHeaderAccessor);
    }
}
