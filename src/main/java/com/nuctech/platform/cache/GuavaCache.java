package com.nuctech.platform.cache;

import com.google.common.cache.CacheBuilder;
import java.util.concurrent.TimeUnit;

/**
 * Created by @author wangzunhui on 2018/4/13.
 */
public class GuavaCache implements Cache<String, Object> {
    private com.google.common.cache.Cache<String, Object> cache = CacheBuilder.newBuilder().build();

    @Override
    public void set(String key, Object value) {
        cache.put(key, value);
    }

    @Override
    public void setnx(String key, Object value) {
        synchronized (cache){
            if (cache.getIfPresent(key) == null){
                cache.put(key, value);
            }
        }
    }

    @Override
    public void set(String key, Object value, long timeout, TimeUnit unit) {
        // Cache expiration time on key is not supported
        cache.put(key, value);
    }

    @Override
    public Object get(String key) {
        return cache.getIfPresent(key);
    }

    @Override
    public void set(String key, String token, Object value, long seconds, int maxLength) {
        // Does not support storing multiple tokens on one key
        cache.put(token, value);
    }
}
