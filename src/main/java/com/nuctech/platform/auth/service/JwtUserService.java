package com.nuctech.platform.auth.service;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.nuctech.platform.auth.bean.User;
import com.nuctech.platform.util.TokenUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

/**
 * Created by @author wangzunhui on 2018/4/11.
 */
public class JwtUserService extends AbstractUserService {
    private final Logger logger = LoggerFactory.getLogger(JwtUserService.class);
    private final Cache<String, List<String>> cache = CacheBuilder.newBuilder().build();

    @Override
    public Optional<User> getUser(String token) {
        return TokenUtil.decodeJwt(token).map(this::j2u);
    }

    @Override
    public String createToken(User user) {
        return TokenUtil.createJwtToken(u2j(user)).get();
    }

    @Override
    public List<String> getAuthorize(String uid) {
        return cache.getIfPresent(uid);
    }

    @Override
    public void setAuthorize(String uid, List<String> uris) {
        cache.put(uid, uris);
    }
}
