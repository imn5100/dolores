package com.shaw.dolores.websocket;

import com.shaw.dolores.utils.Constants;
import com.shaw.dolores.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.*;

@Service
public class SessionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(SessionHandler.class);
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final Map<String, WebSocketSession> sessionMap = new ConcurrentHashMap<>();
    private final Queue<WebSocketSession> queue = new PriorityBlockingQueue<>(10, (o1, o2) -> (int) (Utils.parseLongQuietly(o1.getAttributes().get(Constants.EXPIRE_TIME), 0) - Utils.parseLongQuietly(o2.getAttributes().get(Constants.EXPIRE_TIME), 0)));

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    public SessionHandler() {
        scheduler.scheduleAtFixedRate(() -> {
            try {
                while (true) {
                    WebSocketSession session = queue.poll();
                    if (session != null) {
                        Long expireTime = Utils.parseLongQuietly(session.getAttributes().get(Constants.EXPIRE_TIME), 0);
                        if (System.currentTimeMillis() >= expireTime) {
                            if (session.isOpen()) {
                                session.close();
                            }
                        } else {
                            //当前会话未超时重新放入队列，停止遍历
                            queue.add(session);
                            break;
                        }
                    } else {
                        //队列无元素
                        break;
                    }
                }
                LOGGER.info("sessionMap size:" + sessionMap.size() + "||queue size:" + queue.size());
            } catch (IOException e) {
                LOGGER.error("Error while closing websocket session: {}", e);
            }
        }, 1, 5, TimeUnit.MINUTES);
    }

    public synchronized void disconnect(String sessionId) {
        if (!StringUtils.isEmpty(sessionId) && sessionMap.containsKey(sessionId)) {
            WebSocketSession session = sessionMap.get(sessionId);
            if (session != null) {
                try {
                    if (session.isOpen())
                        session.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void register(WebSocketSession session) {
        sessionMap.put((String) session.getAttributes().get(Constants.SESSION_ID), session);
        queue.add(session);
    }


    public void removeSessionData(WebSocketSession session) {
        if (session != null && session.getAttributes().containsKey(Constants.SESSION_ID)) {
            String sessionId = (String) session.getAttributes().get(Constants.SESSION_ID);
            sessionMap.remove(sessionId);
            queue.remove(session);
            stringRedisTemplate.delete(sessionId);
//            removeSubscribeCount(session);
        }
    }

    private void removeSubscribeCount(WebSocketSession session) {
        String subscribeTopic = (String) session.getAttributes().get(Constants.SUBSCRIBE_TOPIC);
        if (Utils.isNotEmpty(subscribeTopic)) {
            long count = stringRedisTemplate.opsForHash().increment(Constants.SUBSCRIBE_SET_REDIS_KEY, subscribeTopic, -1);
            if (count <= 0) {
                stringRedisTemplate.opsForHash().delete(Constants.SUBSCRIBE_SET_REDIS_KEY, subscribeTopic);
            }
        }
    }

}