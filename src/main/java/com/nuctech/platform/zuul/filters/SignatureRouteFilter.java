package com.nuctech.platform.zuul.filters;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.nuctech.platform.util.CryptoUtil;
import com.nuctech.platform.util.KeyPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.ROUTE_TYPE;

/**
 * Created by wangzunhui on 2017/8/13.
 */
@Component
public class SignatureRouteFilter extends ZuulFilter {
    private final Logger logger = LoggerFactory.getLogger(SignatureRouteFilter.class);

    @Override
    public String filterType() {
        return ROUTE_TYPE;
    }

    @Override
    public int filterOrder() {
        return 9;
    }

    @Override
    public boolean shouldFilter() {
        RequestContext ctx = RequestContext.getCurrentContext();
        return ctx.get("sendZuulResponse") == null ? true : false;
    }

    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        //logger.info("4.------------sign route filter---------" + ctx.getRequest().getRequestURI());

        String uid = (String)ctx.getRequest().getAttribute("user-name");
        String tid = (String)ctx.getRequest().getAttribute("trace-no");
        String signature = CryptoUtil.signature(KeyPool.DEFAULT_KEY, uid, tid);

        ctx.addZuulRequestHeader("X-USER-ID", uid);
        ctx.addZuulRequestHeader("X-TRACE-ID", tid);
        ctx.addZuulRequestHeader("X-AUTH-CODE", signature);

        return null;
    }
}
