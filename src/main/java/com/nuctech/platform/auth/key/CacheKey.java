package com.nuctech.platform.auth.key;

import com.nuctech.platform.cache.Cache;
import com.nuctech.platform.util.TokenUtil;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by @author wangzunhui on 2018/4/15.
 */
public class CacheKey implements Key {
    private final String keyPrefix = "key:";
    private int keyPoolSize = 1024;

    @Autowired
    private Cache<String, String> cache;

    public CacheKey(int keyPoolSize){
        this.keyPoolSize = keyPoolSize;
    }

    @Override
    public String get(int index) {
        String key = keyPrefix + Math.abs(index) % keyPoolSize;
        String value = cache.get(key);
        if (value != null){
            return value;
        }

        value = Long.toString(System.currentTimeMillis()) + TokenUtil.fixedRandom(3);
        cache.setnx(key, value);
        return value;
    }
}
