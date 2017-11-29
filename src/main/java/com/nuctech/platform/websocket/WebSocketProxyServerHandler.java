package com.nuctech.platform.websocket;

import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Client -> Proxy -> Backend
 * Created by wangzunhui on 2017/11/28.
 */
public class WebSocketProxyServerHandler extends AbstractWebSocketHandler {
    private final Map<String, NextHop> nextHops = new ConcurrentHashMap<>();

    @Override
    public void handleMessage(WebSocketSession webSocketSession, WebSocketMessage<?> webSocketMessage) throws Exception {
        getNextHop(webSocketSession).sendMessageToNextHop(webSocketMessage);
    }

    private NextHop getNextHop(WebSocketSession webSocketSession) {
        NextHop nextHop = nextHops.get(webSocketSession.getId());
        if (nextHop == null) {
            nextHop = new NextHop(webSocketSession);
            nextHops.put(webSocketSession.getId(), nextHop);
        }
        return nextHop;
    }
}
