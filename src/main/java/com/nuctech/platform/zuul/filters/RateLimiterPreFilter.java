package com.nuctech.platform.zuul.filters;

import com.google.common.util.concurrent.RateLimiter;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.nuctech.platform.util.HttpRequestUtil;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

import static com.nuctech.platform.util.ErrorCodeEnum.API_RATE_LIMITER;
import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.PRE_TYPE;

/**
 * 根据Guava实现的接口限流过滤器
 *
 * Created by @author wangzunhui on 2017/11/22.
 */
@Component
public class RateLimiterPreFilter extends ZuulFilter {
    private static ConcurrentHashMap<String, RateLimiter> rateLimiterMap = new ConcurrentHashMap<>(64);

    @Override
    public String filterType() {
        return PRE_TYPE;
    }

    /**
     * 过滤器顺序
     * @return
     */
    @Override
    public int filterOrder() {
        return 2;
    }

    @Override
    public boolean shouldFilter() {
        RequestContext ctx = RequestContext.getCurrentContext();
        ctx.getRequest().setAttribute(AuthenticatorPreFilter.REQUEST_ATTRIBUTE_START_TIME, System.currentTimeMillis());
        return false;
    }

    /**
     * 根据配置限制接口的访问速率
     *
     * <p>10.0f表示接口访问速率受限每秒10次</p>
     * @return nothing
     */
    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        String uri = ctx.getRequest().getRequestURI();
        rateLimiterMap.putIfAbsent(uri, RateLimiter.create(10.0f));
        RateLimiter limiter = rateLimiterMap.get(uri);
        if (!limiter.tryAcquire()){
            HttpRequestUtil.rejectZuul(HttpStatus.OK.value(), API_RATE_LIMITER);
            return null;
        }

        return null;
    }
}
