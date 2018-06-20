package com.nuctech.platform.websocket;

import com.nuctech.platform.auth.bean.User;
import com.nuctech.platform.exception.NuctechPlatformException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

import java.io.IOException;
import java.net.URI;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Created by @author wangzunhui on 2017/11/28.
 */
public class NextHop {
    private static final Logger logger = LoggerFactory.getLogger(NextHop.class);

    private final WebSocketSession remoteWebSocketSession;
    private final WebSocketSession clientWebSocketSession;
    private final String uri;
    private final Optional<User> user;

    public NextHop(WebSocketSession clientWebSocketSession, String uri, Optional<User> user) {
        this.uri = uri;
        this.user = user;
        this.clientWebSocketSession = clientWebSocketSession;
        remoteWebSocketSession = createWebSocketClientSession(clientWebSocketSession);
    }

    /**
     * Create websocket with remote server.
     *
     * @param clientWebSocketSession
     * @return
     */
    private WebSocketSession createWebSocketClientSession(WebSocketSession clientWebSocketSession) {
        try {
            WebSocketHttpHeaders headers = new WebSocketHttpHeaders();
            user.ifPresent(u ->{
                headers.add("X-APP-UID", u.getId());
                headers.add("X-APP-OID", u.getOrgId());
            });
            return new StandardWebSocketClient()
                    .doHandshake(new WebSocketProxyRouteHandler(this, clientWebSocketSession), headers, new URI(uri))
                    .get(5000, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            throw new NuctechPlatformException("Failed to create remote websocket", e);
        }
    }

    /**
     * Send message to the remote server.
     *
     * @param webSocketMessage
     * @throws IOException
     */
    public void sendMessage(WebSocketMessage<?> webSocketMessage) throws IOException {
        logger.info("send message {} to remote {}", webSocketMessage.getPayload().toString(), remoteWebSocketSession.getId());
        remoteWebSocketSession.sendMessage(webSocketMessage);
    }

    /**
     * Close remote websocket session.
     *
     * @throws IOException
     */
    public void closeRemote() throws IOException {
        if (remoteWebSocketSession.isOpen()) {
            logger.info("closing remote {} websocket server.", remoteWebSocketSession.getId());
            remoteWebSocketSession.close();
        }
    }

    /**
     * Close client websocket session.
     *
     * @throws IOException
     */
    public void closeClient() throws IOException {
        if (clientWebSocketSession.isOpen()) {
            logger.info("closing client {} websocket server.", clientWebSocketSession.getId());
            clientWebSocketSession.close();
        }
    }
}
