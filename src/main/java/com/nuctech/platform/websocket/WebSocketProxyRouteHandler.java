package com.nuctech.platform.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

/**
 * Proxy -> Backend.
 *
 * Created by wangzunhui on 2017/11/28.
 */
public class WebSocketProxyRouteHandler extends AbstractWebSocketHandler {
    private static final Logger logger = LoggerFactory.getLogger(WebSocketProxyRouteHandler.class);

    private final WebSocketSession clientWebSocketSession;
    private final NextHop nextHop;

    public WebSocketProxyRouteHandler(NextHop nextHop, WebSocketSession clientWebSocketSession) {
        this.nextHop = nextHop;
        this.clientWebSocketSession = clientWebSocketSession;
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> webSocketMessage) throws Exception {
        logger.info("[p->c] {}", webSocketMessage.getPayload().toString());
        clientWebSocketSession.sendMessage(webSocketMessage);
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        logger.info("Remote websocket server {} connected.", session.getId());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        logger.info("Remote websocket server {} closed. start closing client websocket.", session.getId());
        this.nextHop.closeClient();
    }
}
