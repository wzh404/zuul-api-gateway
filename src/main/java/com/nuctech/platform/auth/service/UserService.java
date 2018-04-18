package com.nuctech.platform.auth.service;

import com.nuctech.platform.auth.bean.User;
import com.nuctech.platform.util.ErrorCodeEnum;

import java.util.Optional;

/**
 * Created by @author wangzunhui on 2018/4/11.
 */
public interface UserService {
    /**
     * Get user from cache or urpm based on token
     *
     * @param token
     * @return user
     */
    Optional<User> getUser(String token);

    /**
     * Check if the current requested uri is authorized
     *
     * @param uid user id
     * @param url the requested uri
     * @return
     */
    ErrorCodeEnum checkAuthorize(String uid, String url);

    /**
     * create user login token and save to cache.
     *
     * @param user
     * @return
     */
    String createToken(User user);
}
