package com.nuctech.platform.websocket;

import com.nuctech.platform.auth.bean.User;
import com.nuctech.platform.auth.service.UserService;
import com.nuctech.platform.auth.whitelist.Whitelists;
import com.nuctech.platform.util.ErrorCodeEnum;
import com.nuctech.platform.util.TokenUtil;
import com.nuctech.platform.zuul.filters.AuthenticatorPreFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Client(web browser) -> Proxy(api gateway)
 *
 * Created by @author wangzunhui on 2017/11/28.
 */
public class WebSocketProxyServerHandler extends AbstractWebSocketHandler {
    private static final Logger logger = LoggerFactory.getLogger(WebSocketProxyServerHandler.class);

    private final Map<String, NextHop> nextHops = new ConcurrentHashMap<>();
    private final String remoteUri;

    @Autowired
    private UserService userService;

    @Autowired
    private Whitelists whitelists;

    public WebSocketProxyServerHandler(String remoteUri){
        this.remoteUri = remoteUri;
    }

    /**
     * Send message to remote application server.
     *
     * @param webSocketSession client websocket session.
     * @param webSocketMessage client websocket message.
     * @throws Exception
     */
    @Override
    public void handleMessage(WebSocketSession webSocketSession, WebSocketMessage<?> webSocketMessage) throws Exception {
        getNextHop(webSocketSession).sendMessage(webSocketMessage);
    }

    /**
     * Check whether the user and the token are valid
     *
     * @param uri websocket request uri.
     * @param token request cookie
     * @return
     */
    private boolean auth(String uri, String token){
        // The uri is in the authenticator's white list.
        if (whitelists.inAuthenticator(uri)){
            return true;
        }

        if (token == null){
            return false;
        }

        Optional<User> user = userService.getUser(token);
        if (!user.isPresent()){
            logger.warn("The user is not logged in");
            return false;
        }

        // The uri is in the authorize's white list.
        if (whitelists.inAuthorize(uri)){
            return true;
        }

        ErrorCodeEnum errorCode = userService.checkAuthorize(user.get().getId(), uri);
        return errorCode == ErrorCodeEnum.API_SUCCESS;
    }

    /**
     * Get the cookie value by the name from the websocket handshake session header.
     *
     * @param webSocketSession websocket session
     * @param name cookie name
     * @return cookie value
     */
    private Optional<String> getCookie(WebSocketSession webSocketSession, String name){
        String cookieValue = webSocketSession.getHandshakeHeaders().getFirst("cookie");
        if (cookieValue == null){
            logger.warn("not cookie found");
            return Optional.empty();
        }

        String[] cookies = cookieValue.split(";");
        for (String c : cookies){
            c = c.trim();
            // Base64 encrypted data contains equal sign
            if (c.startsWith(name + "=")){
                String v = c.substring(name.length() + 1);
                return Optional.of(v);
            }
        }

        logger.warn("cookie {} not found ", name);
        return Optional.empty();
    }

    /**
     * Get remote application server.
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
        Optional<String> token = getCookie(session, TokenUtil.TOKEN);
        String uri = session.getUri().toString();
        logger.info("[c -> p] {} {}", uri, token.get());
        if (!auth(uri, token.get())){
            if (session.isOpen()){
                session.close(CloseStatus.NOT_ACCEPTABLE);
            }
            return;
        }
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
