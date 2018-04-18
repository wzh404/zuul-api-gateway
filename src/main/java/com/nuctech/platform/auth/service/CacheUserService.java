package com.nuctech.platform.auth.service;

import com.nuctech.platform.auth.bean.User;
import com.nuctech.platform.cache.Cache;
import com.nuctech.platform.util.TokenUtil;
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

        // Ensure that one user is up to five online.
        cache.set(user.getId(), token, u2j(user), timeout <= 0 ? 30 : timeout, 5);

        return token;
    }

    @Override
    public List<String> getAuthorize(String uid) {
        String join = cache.get(uid);
        if (join == null) {
            return new ArrayList<>();
        }

        return Arrays.asList(join.split(","));
    }

    @Override
    public void setAuthorize(String uid, List<String> uris) {
        cache.set(uid, String.join(",", uris));
    }
}
