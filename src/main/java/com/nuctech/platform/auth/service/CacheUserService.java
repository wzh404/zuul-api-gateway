package com.nuctech.platform.auth.service;

import com.nuctech.platform.auth.bean.User;
import com.nuctech.platform.cache.Cache;
import com.nuctech.platform.util.TokenUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Created by @author wangzunhui on 2018/4/11.
 */
public class CacheUserService extends AbstractUserService {
    private final Logger logger = LoggerFactory.getLogger(CacheUserService.class);

    @Autowired
    private Cache<String, String> cache;

    @Override
    public Optional<User> getUser(String token) {
        return Optional.ofNullable(j2u(cache.get(token)));
    }

    @Override
    public String createToken(User user) {
        String token = TokenUtil.createSessionId();
        long timeout = user.getTimeout();
        if (timeout <= 0){
            timeout = defaultTimeout;
        }

        int online = user.getNumPerUser();
        logger.debug("default user online number is {}", online);
        if (online > 0) {
            // 限制用户在线人数.
            cache.set(user.getId(), token, u2j(user), timeout, online);
        } else {
            cache.set(token, u2j(user), timeout, TimeUnit.MINUTES);
        }
        return token;
    }

    @Override
    public List<String> getAuthorize(String uid) {
        String join = cache.get(prefixUserPermission + uid);
        if (join == null) {
            return new ArrayList<>();
        }

        return Arrays.asList(join.split(","));
    }

    @Override
    public void setAuthorize(String uid, List<String> uris) {
        cache.set(prefixUserPermission + uid, String.join(",", uris));
    }
}
