package com.nuctech.platform.zuul.filters;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.nuctech.platform.util.HttpRequestUtil;
import com.nuctech.platform.util.JSnowFlake;
import com.nuctech.platform.util.TokenUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import java.util.Optional;

import static com.nuctech.platform.util.ErrorCodeEnum.API_INVALID_TOKEN;
import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.PRE_TYPE;

/**
 * Created by @author wangzunhui on 2017/10/9.
 */
@Component
public class SsoPreFilter extends ZuulFilter {
    // private final Logger logger = LoggerFactory.getLogger(SsoPreFilter.class);
    private static final String SLF4J_MDC_TRACE = "Trace-Id";
    public static final String REQUEST_ATTRIBUTE_UID = "attr_uid";
    public static final String REQUEST_ATTRIBUTE_TRA = "attr_tra";
    public static final JSnowFlake traceGen = new JSnowFlake(0);

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
        return true;
    }

    private static String handleToken(TokenUtil.TokenResult r) {
        if (r.getCode() < 0) {
            return null;
        }

        RequestContext ctx = RequestContext.getCurrentContext();
        String traceNo = Long.toString(traceGen.nextId());
        ctx.getRequest().setAttribute(REQUEST_ATTRIBUTE_UID, r.getValue());
        ctx.getRequest().setAttribute(REQUEST_ATTRIBUTE_TRA, traceNo);
        MDC.put(SLF4J_MDC_TRACE, traceNo);

        // reset token.
        if (r.getCode() == 1) {
            String token = TokenUtil.generatorToken(r.getValue());
            Cookie c = new Cookie(TokenUtil.TOKEN, token);
            c.setSecure(true);
            c.setHttpOnly(true);
            c.setPath("/");
            ctx.getResponse().addCookie(c);
        }
        return StringUtils.EMPTY;
    }

    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        Optional<String> token = HttpRequestUtil.getCookieValue(ctx.getRequest(), TokenUtil.TOKEN);
        return token.map(TokenUtil::checkAndGetUid)
                .map(SsoPreFilter::handleToken)
                .orElseGet(() -> {
                    AuthPreFilter.rejectZuul(403, API_INVALID_TOKEN);
                    return null;
                });
    }
}
