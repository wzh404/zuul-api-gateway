package com.nuctech.platform.auth.whitelist;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import java.util.Arrays;

/**
 * Authentication and authorization whitelist.
 *
 * Created by @author wangzunhui on 2018/4/13.
 */
@Component
public class Whitelists {
    private final Logger logger = LoggerFactory.getLogger(Whitelists.class);
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    // 授权
    @Value(value = "${nuctech.whitelist.authorize}")
    private String[] authorize;

    // 认证
    @Value(value = "${nuctech.whitelist.authenticator}")
    private String[] authenticator;

    /**
     * authorize include authenticator whitelist
     *
     * @param uri request uri.
     * @return
     */
    public boolean inAuthorize(String uri){
        return inAuthenticator(uri) || Arrays.stream(authorize).anyMatch(m -> pathMatcher.match(m, uri));
    }

    /**
     * check authenticator white list.
     *
     * @param uri request uri.
     * @return
     */
    public boolean inAuthenticator(String uri){
        return Arrays.stream(authenticator).anyMatch(m -> pathMatcher.match(m, uri));
    }
}
