package com.nuctech.platform.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Client -> Proxy
 *
 * Created by wangzunhui on 2017/11/28.
 */
public class WebSocketProxyServerHandler extends AbstractWebSocketHandler {
    private static final Logger logger = LoggerFactory.getLogger(WebSocketProxyServerHandler.class);

    private final Map<String, NextHop> nextHops = new ConcurrentHashMap<>();
    private final String remoteUri;

    public WebSocketProxyServerHandler(String remoteUri){
        this.remoteUri = remoteUri;
    }

    /**
     * Send message to remote server.
     *
     * @param webSocketSession client websocket session.
     * @param webSocketMessage client websocket message.
     * @throws Exception
     */
    @Override
    public void handleMessage(WebSocketSession webSocketSession, WebSocketMessage<?> webSocketMessage) throws Exception {
        logger.info("[c->p]", webSocketMessage.getPayload().toString());
        getNextHop(webSocketSession).sendMessage(webSocketMessage);
    }

    /**
     * Get remote websocket server.
     *
     * @param webSocketSession client websocket session.
     * @return
     */
    private NextHop getNextHop(WebSocketSession webSocketSession) {
        NextHop nextHop = nextHops.get(webSocketSession.getId());
        if (nextHop == null) {
            nextHop = new NextHop(webSocketSession, remoteUri);
            nextHops.put(webSocketSession.getId(), nextHop);
        }
        return nextHop;
    }

    /**
     * Client websocket connected.
     *
     * @param session
     * @throws Exception
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        logger.info("client {} websocket connected.", session.getId());
    }

    /**
     * Client websocket closed. start closing remote server.
     *
     * @param session
     * @param status
     * @throws Exception
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        logger.info("client {} websocket closed.", session.getId());
        getNextHop(session).closeRemote();
        nextHops.remove(session.getId());
        logger.info("nextHops caches size {}.", nextHops.size());
    }
}
