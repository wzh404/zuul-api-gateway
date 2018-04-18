package com.nuctech.platform.auth.config;

import com.nuctech.platform.auth.key.CacheKey;
import com.nuctech.platform.auth.key.StaticKey;
import com.nuctech.platform.auth.service.CacheUserService;
import com.nuctech.platform.auth.service.JwtUserService;
import com.nuctech.platform.cache.GuavaCache;
import com.nuctech.platform.cache.RedisCache;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by @author wangzunhui on 2018/4/13.
 */
@Configuration
public class AuthorizeConfigurer {
    @ConditionalOnProperty(name="nuctech.authenticator.type", havingValue = "jwt")
    @Bean("jwtUserService")
    public JwtUserService jwtUserService(){
        return new JwtUserService();
    }

    @ConditionalOnProperty(name="nuctech.authenticator.type", havingValue = "cache")
    @Bean("cacheUserService")
    public CacheUserService cacheUserService(){
        return new CacheUserService();
    }

    @ConditionalOnProperty(name="nuctech.cache.type", havingValue = "redis")
    @Bean("redisCache")
    public RedisCache redisCache(){
        return new RedisCache();
    }

    @ConditionalOnProperty(name="nuctech.cache.type", havingValue = "guava")
    @Bean("guavaCache")
    public GuavaCache guavaCache(){
        return new GuavaCache();
    }

    @ConditionalOnProperty(name="nuctech.key.storage", havingValue = "cache")
    @Bean("cacheKey")
    public CacheKey cacheKey(){
        return new CacheKey(1024);
    }

    @ConditionalOnProperty(name="nuctech.key.storage", havingValue = "static")
    @Bean("staticKey")
    public StaticKey staticKey(){
        return new StaticKey();
    }
}
