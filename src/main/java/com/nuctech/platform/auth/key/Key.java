package com.nuctech.platform.auth.key;

/**
 * Interface for get the hash key.
 *
 * Created by @author wangzunhui on 2018/4/15.
 */
public interface Key {
    /**
     * get the hash key form the key pool by index.
     *
     * @param index key index
     * @return
     */
    String get(int index);
}
