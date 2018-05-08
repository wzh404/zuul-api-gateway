package com.nuctech.platform.auth.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nuctech.platform.auth.bean.User;
import com.nuctech.platform.util.ErrorCodeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.AntPathMatcher;

import java.io.IOException;
import java.util.List;

import static com.nuctech.platform.util.ErrorCodeEnum.API_EMPTY_USER_PERMISSION;
import static com.nuctech.platform.util.ErrorCodeEnum.API_NOT_PERMIT;
import static com.nuctech.platform.util.ErrorCodeEnum.API_SUCCESS;

/**
 *
 * Created by @author wangzunhui on 2018/4/12.
 */
public abstract  class AbstractUserService implements UserService{
    private final Logger logger = LoggerFactory.getLogger(AbstractUserService.class);
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Autowired
    private UrpmService urpmService;

    /**
     * Get user authority from the urpm.
     *
     * @param uid user id
     * @return the list of user authority
     */
    private List<String> getUrpmAuthorize(String uid) {
        return urpmService.authorize(uid).getUserAuthority();
    }

    /**
     * Get user authority from the cache
     *
     * @param uid user id
     * @return  the list of user authority
     */
    public abstract List<String> getAuthorize(String uid);

    /**
     * Save user authority to the cache.
     *
     * @param uid user id
     * @param uris the list of user authorize
     */
    public abstract void setAuthorize(String uid, List<String> uris);

     /*
      * <p>The mapping matches URLs using the following rules:<br>
      * <ul>
      * <li>{@code ?} matches one character</li>
      * <li>{@code *} matches zero or more characters</li>
      * <li>{@code **} matches zero or more <em>directories</em> in a path</li>
      * <li>{@code {spring:[a-z]+}} matches the regexp {@code [a-z]+} as a path variable named "spring"</li>
      * </ul>
      */
    @Override
    public ErrorCodeEnum checkAuthorize(String uid, String uri) {
        // Get user Authorize from cache.
        List<String> uris = getAuthorize(uid);
        if (uris == null || uris.isEmpty()){
            logger.warn("User authorize cache misses");
            uris = getUrpmAuthorize(uid);
            if (uris.isEmpty()) {
                return API_EMPTY_USER_PERMISSION;
            }

            // Cache user authorize.
            setAuthorize(uid, uris);
        }

        logger.info("user authorize uri is {}", uris);
        // match uri
        if (!uris.stream().anyMatch(m -> pathMatcher.match(m, uri))) {
            return API_NOT_PERMIT;
        }

        return API_SUCCESS;
    }

    /**
     * convert json string to the user instance.
     *
     * @param json String
     * @return
     */
    public User j2u(String json){
        if (json == null){
            logger.warn("json is null");
            return null;
        }

        try {
            return (new ObjectMapper()).readValue(json, User.class);
        } catch (IOException e) {
            logger.error("j2u", e);
            return null;
        }
    }

    /**
     * convert user to json string.
     *
     * @param user
     * @return
     */
    public String u2j(User user){
        if (user == null){
            logger.warn("user is null");
            return null;
        }
        try {
            return (new ObjectMapper()).writeValueAsString(user);
        } catch (IOException e) {
            logger.error("u2j", e);
            return null;
        }
    }
}
