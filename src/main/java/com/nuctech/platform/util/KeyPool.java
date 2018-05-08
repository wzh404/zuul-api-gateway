package com.nuctech.platform.util;

import com.google.common.hash.Hashing;
import com.nuctech.platform.auth.key.Key;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * Created by @author wangzunhui on 2017/8/12.
 */
@Component
public class KeyPool {
    public static final String DEFAULT_KEY = "nuctech.com@706B";

    private static Key key;

    /**
     * Inject static or dynamic keys according to the configuration.
     *
     * @param k Key
     */
    @Autowired
    public void setKey(Key k){
        synchronized(KeyPool.class){
            if (KeyPool.key == null) {
                KeyPool.key = k;
            }
        }
    }

    /**
     * get a key from the key pool according to string type.
     *
     * @param source
     * @return
     */
    public static String getKey(String... source) {
        if (source == null) {
            return KeyPool.DEFAULT_KEY;
        }

        String s = Arrays.stream(source).reduce("", (s1, s2) -> s1 + s2);
        int index = Hashing.murmur3_32()
                .hashBytes(s.getBytes())
                .hashCode();
        return key.get(index);
    }

    /**
     * get a key from the key pool according to long type.
     *
     * @param source
     * @return
     */
    public static String getKey(long source) {
        int hash = Long.valueOf(source).hashCode();
        if (hash < 0) {
            return DEFAULT_KEY;
        }
        return key.get(hash);
    }
}
