package com.nuctech.platform.zuul.filters;

import com.google.common.util.concurrent.RateLimiter;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;

import java.util.concurrent.ConcurrentHashMap;

import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.PRE_TYPE;

/**
 * Created by wangzunhui on 2017/11/22.
 */
public class RateLimiterPreFilter extends ZuulFilter {
    private static ConcurrentHashMap<String, RateLimiter> rateLimiterMap = new ConcurrentHashMap<>(64);

    @Override
    public String filterType() {
        return PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return 2;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        String uri = ctx.getRequest().getRequestURI();
        rateLimiterMap.putIfAbsent(uri, RateLimiter.create(10.0f));
        RateLimiter limiter = rateLimiterMap.get(uri);
        if (!limiter.tryAcquire()){
            AuthPreFilter.rejectZuul( 403, "api_rate_limiter");
            return null;
        }

        return null;
    }
}
