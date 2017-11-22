package com.nuctech.platform.util.limit;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by wangzunhui on 2017/9/26.
 */
public class IPRateLimiter {
    private final Cache<String, Integer> cache;
    private int threshold;

    public IPRateLimiter(int threshold){
        cache = CacheBuilder
                .newBuilder()
                .maximumSize(1024)
                .expireAfterWrite(2, TimeUnit.MINUTES)
                .build();
        this.threshold = threshold;
    }

    public boolean tryRequire(String ip){
        String minuteKey = DateFormatUtils.format(new Date(), "yyyyMMddHHmm") + ip;

        synchronized(this.cache) {
            Integer count = cache.getIfPresent(minuteKey);
            count = count == null ? 1 : count + 1;
            if (count > this.threshold) {
                return false;
            }

            cache.put(minuteKey, count);
        }

        return true;
    }
}
