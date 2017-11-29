package com.nuctech.platform.websocket;

import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

/**
 * Proxy -> Backend.
 *
 * Created by wangzunhui on 2017/11/28.
 */
public class WebSocketProxyClientHandler extends AbstractWebSocketHandler {
    private final WebSocketSession webSocketServerSession;

    public WebSocketProxyClientHandler(WebSocketSession webSocketServerSession) {
        this.webSocketServerSession = webSocketServerSession;
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> webSocketMessage) throws Exception {
        webSocketServerSession.sendMessage(webSocketMessage);
    }
}
