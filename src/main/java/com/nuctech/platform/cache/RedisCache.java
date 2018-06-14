package com.nuctech.platform.cache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.core.script.RedisScript;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by @author wangzunhui on 2018/4/12.
 */
public class RedisCache implements Cache<String, String> {
    //private final Logger logger = LoggerFactory.getLogger(RedisCache.class);
    private static RedisScript luaScript;

    /*
     * Create redis lua script. atomic operation
     *
     * KEYS[1] key : list key
     * KEYS[2] token : list key value
     * KEYS[3] value : token value
     * KEYS[4] timeout : token expired time.
     * KEYS[5] max_length: List max size.
     */
    static {
        StringBuilder lua = new StringBuilder();
        lua.append("local key = KEYS[1]\n");
        lua.append("local token = KEYS[2]\n");
        lua.append("local value = KEYS[3]\n");
        lua.append("local timeout = tonumber(KEYS[4])\n");
        lua.append("local max_length = tonumber(KEYS[5])\n\n");
        lua.append("local len = redis.call('llen', key)\n");

        lua.append("if (len >= max_length) then\n");
        lua.append("local t = redis.call('lpop', key)\n");
        lua.append("redis.call('del', t)\n");
        lua.append("end\n\n");

        lua.append("redis.call('rpush', key, token)\n");

        /* lua.append("redis.log(redis.LOG_WARNING, 'timeout = ' .. KEYS[2])\n"); */
        lua.append("redis.call('setex', token, timeout * 60, value)\n");
        lua.append("return\n");

        luaScript = RedisScript.of(lua.toString());
    }

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public void set(String key, String value) {
        ValueOperations vo = redisTemplate.opsForValue();
        vo.set(key, value);
    }

    @Override
    public void setnx(String key, String value) {
        ValueOperations vo = redisTemplate.opsForValue();
        vo.setIfAbsent(key, value);
    }

    @Override
    public void set(String key, String value, long timeout, TimeUnit unit) {
        ValueOperations vo = redisTemplate.opsForValue();
        vo.set(key, value, timeout, unit);
    }

    @Override
    public String get(String key) {
        ValueOperations vo = redisTemplate.opsForValue();
        return (String) vo.get(key);
    }

    /**
     * Store up to maxLength tokens for each key, use redis list。
     *
     * @param key       list key
     * @param token     string key.
     * @param value
     * @param timeout   time out
     * @param maxLength list max length
     */
    @Override
    public void set(final String key, final String token, final String value, long timeout, int maxLength) {
        List<String> keys = new ArrayList<>();
        keys.add(key);
        keys.add(token);
        keys.add(value);
        keys.add(Long.toString(timeout));
        keys.add(Integer.toString(maxLength));

        redisTemplate.execute(luaScript, keys);
    }
}
