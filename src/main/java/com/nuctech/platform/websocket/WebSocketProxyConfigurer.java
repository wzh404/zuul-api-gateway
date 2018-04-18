package com.nuctech.platform.websocket;

import com.nuctech.platform.auth.service.UserService;
import com.nuctech.platform.auth.whitelist.Whitelists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.client.standard.WebSocketContainerFactoryBean;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * Created by @author wangzunhui on 2017/11/28.
 */
@Configuration
@EnableWebSocket
public class WebSocketProxyConfigurer implements WebSocketConfigurer {
    @Autowired
    private UserService userService;

    @Autowired
    private Whitelists whitelists;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new WebSocketProxyServerHandler(userService, whitelists, "ws://localhost:9000/ws"), "/ws");
    }

    @Bean
    public WebSocketContainerFactoryBean createWebSocketContainer() {
        WebSocketContainerFactoryBean container = new WebSocketContainerFactoryBean();
        container.setMaxTextMessageBufferSize(8192);
        container.setMaxBinaryMessageBufferSize(8192);
        return container;
    }
}