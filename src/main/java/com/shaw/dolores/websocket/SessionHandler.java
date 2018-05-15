package com.shaw.dolores.websocket;

import com.shaw.dolores.utils.Constants;
import com.shaw.dolores.utils.Utils;
import com.shaw.dolores.vo.SessionData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.*;

public class SessionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(SessionHandler.class);
    private final Map<String, WebSocketSession> sessionMap = new ConcurrentHashMap<>();
    private final Queue<WebSocketSession> queue = new PriorityBlockingQueue<>(10, (o1, o2) -> (int) (Utils.parseLongQuietly(o1.getAttributes().get(Constants.EXPIRE_TIME), 0) - Utils.parseLongQuietly(o2.getAttributes().get(Constants.EXPIRE_TIME), 0)));
    private final Map<Integer, List<SessionData>> useSessionMap = new ConcurrentHashMap<>();
    private static final SessionHandler INSTANCE = new SessionHandler();
    public static SessionHandler getInstance() {
        return INSTANCE;
    }

    public void register(WebSocketSession session) {
        SessionData sessionData = (SessionData) session.getAttributes().get(Constants.SESSION_DATA);
        if (sessionData == null) {
            return;
        }
        useSessionMap.computeIfAbsent(sessionData.getUserId(), k -> new ArrayList<>()).add(sessionData);
        sessionMap.put(sessionData.getSessionId(), session);
        queue.add(session);
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

    public SessionData getSessionData(String sessionId) {
        WebSocketSession webSocketSession = sessionMap.get(sessionId);
        if (webSocketSession != null) {
            return (SessionData) webSocketSession.getAttributes().get(Constants.SESSION_DATA);
        }
        return null;
    }

    public List<SessionData> getSessionDataByUser(int userId) {
        return useSessionMap.getOrDefault(userId, new ArrayList<>());
    }


    //private
    private SessionHandler() {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
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

    void removeSessionData(WebSocketSession session) {
        if (session != null && session.getAttributes().containsKey(Constants.SESSION_ID)) {
            String sessionId = (String) session.getAttributes().get(Constants.SESSION_ID);
            if(sessionMap.remove(sessionId)!=null){
                LOGGER.info(sessionId + " remove from sessionMap");
            }
            if(queue.remove(session)){
                LOGGER.info(sessionId + " remove from sessionQueue");
            }
            removeUseSessionData(session);
        }
    }

    private void removeUseSessionData(WebSocketSession session) {
        SessionData sessionData = (SessionData) session.getAttributes().get(Constants.SESSION_DATA);
        if (sessionData != null) {
            if (useSessionMap.containsKey(sessionData.getUserId())) {
                useSessionMap.get(sessionData.getUserId()).remove(sessionData);
            }
        }
    }
}