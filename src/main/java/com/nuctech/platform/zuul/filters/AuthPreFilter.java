package com.nuctech.platform.zuul.filters;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.nuctech.platform.auth.UserPermission;
import com.nuctech.platform.auth.UserService;
import com.nuctech.platform.util.HttpRequestUtil;
import com.nuctech.platform.util.TokenUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.PRE_TYPE;

/**
 * Created by wangzunhui on 2017/9/21.
 */
@Component
public class AuthPreFilter extends ZuulFilter {
    private final Logger logger = LoggerFactory.getLogger(AuthPreFilter.class);

    public static final Cache<String, List<String>> cache = CacheBuilder.newBuilder().build();

    private final AntPathMatcher pathMatcher = new AntPathMatcher();


    @Autowired
    private UserService userService;

    @Override
    public String filterType() {
        return PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return 3;
    }

    @Override
    public boolean shouldFilter() {
        RequestContext ctx = RequestContext.getCurrentContext();
        return ctx.get("sendZuulResponse") == null? true : false;
    }

    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        logger.info("2.------------auth filter---------" + ctx.getRequest().getRequestURI());
        // check token
        Optional<String> token = HttpRequestUtil.getCookieValue(ctx.getRequest(), TokenUtil.TOKEN);
        if (!token.isPresent()){
            rejectZuul( 403, "api_token_not_found");
            return null;
        }

        // check uid
        Optional<String> optional = TokenUtil.checkAndGetUid(token.get());
        if (!optional.isPresent()){
            rejectZuul( 403, "api_invalid_token");
            return null;
        }

        // check user permission
        String uid = optional.get();
        List<String> uris = getUserPermissions(uid);
        if (uris.isEmpty()){
            rejectZuul(403, "api_empty_user_permission");
            return null;
        }

        // match uri
        String uri = ctx.getRequest().getRequestURI();
        if (!uris.stream().anyMatch(m -> pathMatcher.match(m, uri))){
            rejectZuul( 403, "api_not_permit");
            return null;
        }

        return null;
    }

    public static void rejectZuul(int status, String body){
        RequestContext ctx = RequestContext.getCurrentContext();
        ctx.setSendZuulResponse(false);
        ctx.setResponseStatusCode(status);
        ctx.setResponseBody(body);
    }

    /**
     * 获取用户可访问的uri列表。
     *
     * @param uid
     * @return
     */
    private List<String> getUserPermissions(String uid){
        List<java.lang.String> uris = cache.getIfPresent(uid);
        if (uris == null) {
            UserPermission permission = userService.permits(uid);
            if (permission == null) {
                return new ArrayList<>(0);
            }

            uris = permission.getPermissions();
            cache.put(uid, uris);
        }

        return uris;
    }
}
