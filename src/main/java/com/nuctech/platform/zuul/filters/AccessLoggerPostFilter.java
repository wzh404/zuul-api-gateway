package com.nuctech.platform.zuul.filters;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.POST_TYPE;

/**
 * Record access log.
 *
 * Created by @author wangzunhui on 2018/4/17.
 */
@Component
public class AccessLoggerPostFilter extends ZuulFilter {
    private final Logger logger = LoggerFactory.getLogger(AccessLoggerPostFilter.class.getName());

    @Override
    public String filterType() {
        return POST_TYPE;
    }

    @Override
    public int filterOrder() {
        return 9;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() throws ZuulException {
        RequestContext ctx = RequestContext.getCurrentContext();
        String uri = ctx.getRequest().getRequestURI();
        String referer = ctx.getRequest().getHeader("Referer");
        long startTime = (long)ctx.getRequest().getAttribute(AuthenticatorPreFilter.REQUEST_ATTRIBUTE_START_TIME);
        long elapsedTime = System.currentTimeMillis() - startTime;

        logger.info("{} [{}] {}ms - {}", uri, referer == null ? "" : referer, elapsedTime, ctx.getResponseStatusCode());
        return null;
    }
}
