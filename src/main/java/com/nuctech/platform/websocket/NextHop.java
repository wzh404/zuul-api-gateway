package com.nuctech.platform.websocket;

import com.nuctech.platform.exception.NuctechPlatformException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Created by @author wangzunhui on 2017/11/28.
 */
public class NextHop {
    private static final Logger logger = LoggerFactory.getLogger(NextHop.class);

    private final WebSocketSession remoteWebSocketSession;
    private final WebSocketSession clientWebSocketSession;
    private final String uri;

    public NextHop(WebSocketSession clientWebSocketSession, String uri) {
        this.uri = uri;
        this.clientWebSocketSession = clientWebSocketSession;
        remoteWebSocketSession = createWebSocketClientSession(clientWebSocketSession);
    }

    private WebSocketSession createWebSocketClientSession(WebSocketSession clientWebSocketSession) {
        try {
            return new StandardWebSocketClient()
                    .doHandshake(new WebSocketProxyRouteHandler(this, clientWebSocketSession), uri)
                    .get(5000, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            throw new NuctechPlatformException("api_create_websocket_failed", e);
        }
    }

    public void sendMessage(WebSocketMessage<?> webSocketMessage) throws IOException {
        logger.info("send message {} to remote {}", webSocketMessage.getPayload().toString(), remoteWebSocketSession.getId());
        remoteWebSocketSession.sendMessage(webSocketMessage);
    }

    public void closeRemote() throws IOException {
        if (remoteWebSocketSession.isOpen()) {
            logger.info("closing remote {} websocket server.", remoteWebSocketSession.getId());
            remoteWebSocketSession.close();
        }
    }

    public void closeClient() throws IOException {
        if (clientWebSocketSession.isOpen()) {
            logger.info("closing client {} websocket server.", clientWebSocketSession.getId());
            clientWebSocketSession.close();
        }
    }
}