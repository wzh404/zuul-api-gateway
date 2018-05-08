package com.nuctech.platform.cache;

import org.springframework.lang.Nullable;

import java.util.concurrent.TimeUnit;

/**
 * Created by @author wangzunhui on 2018/4/11.
 */
public interface Cache<K, V> {
    /**
     * Set {@code value} for {@code key}.
     *
     * @param key must not be {@literal null}.
     * @param value
     *
     */
    void set(K key, V value);

    /**
     * SET key if not exists
     *
     * @param key
     * @param value
     */
    void setnx(K key, V value);

    /**
     * Set the {@code value} and expiration {@code timeout} for {@code key}.
     *
     * @param key must not be {@literal null}.
     * @param value
     * @param timeout
     * @param unit must not be {@literal null}.
     *
     */
    void set(K key, V value, long timeout, TimeUnit unit);


    /**
     * Get the value of {@code key}.
     *
     * @param key must not be {@literal null}.
     * @return
     *
     */
    @Nullable
    V get(K key);

    /**
     * Store up to maxLength tokens for each {@code key}, use lru list。
     *
     * @param key must not be {@literal null}.
     * @param token must not be {@literal null}.
     * @param value
     * @param seconds time out
     * @param maxLength list max length
     */
    void set(final K key, final K token, final V value, long seconds, int maxLength);
}
