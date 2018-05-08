package com.nuctech.platform.zuul.filters.support;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Websocket configuration properties
 *
 * Created by @author wangzunhui on 2018/4/19.
 */
@ConfigurationProperties(prefix="nuctech")
@Data
public class NuctechProperties {
    private Map<String, WebsocketRoute> websockets = new LinkedHashMap<>();

    @Data
    public static class WebsocketRoute{
        private String path;
        private String remoteUri;
    }
}
