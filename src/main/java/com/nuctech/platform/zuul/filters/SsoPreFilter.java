package com.nuctech.platform.zuul.filters;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.nuctech.platform.util.HttpRequestUtil;
import com.nuctech.platform.util.JSnowFlake;
import com.nuctech.platform.util.TokenUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.PRE_TYPE;

/**
 * Created by wangzunhui on 2017/10/9.
 */
@Component
public class SsoPreFilter extends ZuulFilter {
    private final Logger logger = LoggerFactory.getLogger(SsoPreFilter.class);
    public static final  JSnowFlake traceGen = new JSnowFlake(0);

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
        logger.info("1.------------sso filter---------" + ctx.getRequest().getRequestURI());
        Optional<String> token = HttpRequestUtil.getCookieValue(ctx.getRequest(), TokenUtil.TOKEN);
        return token.map(TokenUtil::checkAndGetUid)
                .map(u -> {
                    String traceNo = Long.toString(traceGen.nextId());
                    ctx.getRequest().setAttribute("user-name", u.get());
                    ctx.getRequest().setAttribute("trace-no", traceNo);
                    MDC.put("Trace-Id", traceNo);
                    return "";
                })
                .orElseGet(()->{
                    AuthPreFilter.rejectZuul(403, "invalid_token");
                    return null;
                });
    }
}
