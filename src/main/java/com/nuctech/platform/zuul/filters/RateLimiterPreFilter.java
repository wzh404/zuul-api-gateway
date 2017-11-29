package com.nuctech.platform.zuul.filters;

import com.google.common.util.concurrent.RateLimiter;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

import static com.nuctech.platform.util.ErrorCodeEnum.API_RATE_LIMITER;
import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.PRE_TYPE;

/**
 * Created by @author wangzunhui on 2017/11/22.
 */
@Component
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
            AuthPreFilter.rejectZuul( 403, API_RATE_LIMITER);
            return null;
        }

        return null;
    }
}
