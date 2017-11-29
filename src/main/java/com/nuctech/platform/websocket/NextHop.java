package com.nuctech.platform.websocket;

import com.nuctech.platform.exception.NuctechPlatformException;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Created by wangzunhui on 2017/11/28.
 */
public class NextHop {
    private final WebSocketSession webSocketSession;

    public NextHop(WebSocketSession webSocketServerSession) {
        webSocketSession = createWebSocketClientSession(webSocketServerSession);
    }

    private WebSocketSession createWebSocketClientSession(WebSocketSession webSocketServerSession) {
        try {
            return new StandardWebSocketClient()
                    .doHandshake(new WebSocketProxyClientHandler(webSocketServerSession), "ws://localhost:9999")
                    .get(1000, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            throw new NuctechPlatformException("api_create_websocket_failed", e);
        }
    }

    public void sendMessageToNextHop(WebSocketMessage<?> webSocketMessage) throws IOException {
        webSocketSession.sendMessage(webSocketMessage);
    }
}
