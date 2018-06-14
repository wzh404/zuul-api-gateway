package com.nuctech.platform.websocket;

import com.nuctech.platform.auth.service.UserService;
import com.nuctech.platform.auth.whitelist.Whitelists;
import com.nuctech.platform.zuul.filters.support.NuctechProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
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
@EnableConfigurationProperties({ NuctechProperties.class })
@EnableWebSocket
public class WebSocketProxyConfigurer implements WebSocketConfigurer {
    private final Logger logger = LoggerFactory.getLogger(WebSocketProxyConfigurer.class);

    @Autowired
    private UserService userService;

    @Autowired
    private Whitelists whitelists;

    @Autowired
    private NuctechProperties nuctechProperties;

    @Autowired
    private LoadBalancerClient loadBalancerClient;

    /**
     * 加载websocket代理配置，并注册。
     *
     * @param registry
     */
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        nuctechProperties.getWebsockets().forEach((k, v) ->{
            logger.info("------------{} mapping websocket: {}------", k, v.getPath());
            WebSocketProxyServerHandler handler = new WebSocketProxyServerHandler(userService, whitelists, v.getPath(), loadBalancerClient, k);
            registry.addHandler(handler, v.getPath());
        });
    }

    @Bean
    public WebSocketContainerFactoryBean createWebSocketContainer() {
        WebSocketContainerFactoryBean container = new WebSocketContainerFactoryBean();
        container.setMaxTextMessageBufferSize(8192);
        container.setMaxBinaryMessageBufferSize(8192);
        return container;
    }
}