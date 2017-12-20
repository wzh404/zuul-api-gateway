package com.nuctech.platform.zuul.filters;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.nuctech.platform.util.TokenUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import static com.nuctech.platform.util.ErrorCodeEnum.API_CSRF_OR_TOKEN_NOT_FOUND;
import static com.nuctech.platform.util.ErrorCodeEnum.API_INVALID_CSRF_TOKEN;
import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.PRE_TYPE;

/**
 * Created by @author wangzunhui on 2017/7/31.
 */
@Component
public class CsrfPreFilter extends ZuulFilter {
    //private final Logger logger = LoggerFactory.getLogger(CsrfPreFilter.class);

    @Override
    public String filterType() {
        return PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return 5;
    }

    @Override
    public boolean shouldFilter() {
        RequestContext ctx = RequestContext.getCurrentContext();
        return ctx.get("sendZuulResponse") == null;
    }

    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        //logger.info("3------------csrf filter---------" + ctx.getRequest().getRequestURI());
        if (!"POST".equalsIgnoreCase(ctx.getRequest().getMethod())){
            return null;
        }

        String csrfToken = ctx.getRequest().getHeader(TokenUtil.X_CSRF_TOKEN);
        if (StringUtils.isEmpty(csrfToken)){
            AuthPreFilter.rejectZuul(403, API_CSRF_OR_TOKEN_NOT_FOUND);
            return null;
        }

        String uid = (String)ctx.getRequest().getAttribute(SsoPreFilter.REQUEST_ATTRIBUTE_UID);
        if (!TokenUtil.checkCSRFToken(uid, csrfToken)){
            AuthPreFilter.rejectZuul(403, API_INVALID_CSRF_TOKEN);
        }
/*
        boolean find = Arrays.stream(cookies)
                .anyMatch(c -> (
                        TokenUtil.TOKEN.equalsIgnoreCase(c.getName()) &&
                        TokenUtil.checkCSRFToken(c.getValue(), csrfToken)
                ));
        if (!find){
            AuthPreFilter.rejectZuul(403, API_INVALID_CSRF_TOKEN);
        }
*/
        return null;
    }
}
