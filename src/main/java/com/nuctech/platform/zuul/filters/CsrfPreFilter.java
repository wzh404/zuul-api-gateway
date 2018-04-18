package com.nuctech.platform.zuul.filters;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.nuctech.platform.auth.whitelist.Whitelists;
import com.nuctech.platform.util.HttpRequestUtil;
import com.nuctech.platform.util.TokenUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;


import javax.ws.rs.HttpMethod;

import static com.nuctech.platform.util.ErrorCodeEnum.API_CSRF_TOKEN_NOT_FOUND;
import static com.nuctech.platform.util.ErrorCodeEnum.API_INVALID_CSRF_TOKEN;
import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.PRE_TYPE;

/**
 * 跨站请求伪造防御(Cross-site request forgery)
 *
 * Created by @author wangzunhui on 2017/7/31.
 */
@Component
public class CsrfPreFilter extends ZuulFilter {
    private final Logger logger = LoggerFactory.getLogger(CsrfPreFilter.class);

    @Autowired
    private Whitelists whitelists;

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
        String uri = ctx.getRequest().getRequestURI();
        if (whitelists.inAuthenticator(uri)){
            logger.warn("{} in authenticator white list!", uri);
            return false;
        }

        if (!HttpMethod.POST.equalsIgnoreCase(ctx.getRequest().getMethod()) &&
            !HttpMethod.PUT.equalsIgnoreCase(ctx.getRequest().getMethod()) &&
            !HttpMethod.DELETE.equalsIgnoreCase(ctx.getRequest().getMethod())){
            return false;
        }

        return ctx.sendZuulResponse();
    }


    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();

        String xCsrfToken = ctx.getRequest().getHeader(TokenUtil.X_CSRF_TOKEN);
        String sCsrfToken = ctx.getRequest().getHeader(TokenUtil.S_CSRF_TOKEN);
        if (StringUtils.isEmpty(xCsrfToken) || StringUtils.isEmpty(sCsrfToken)){
            HttpRequestUtil.rejectZuul(HttpStatus.OK.value(), API_CSRF_TOKEN_NOT_FOUND);
            return null;
        }

        if (xCsrfToken.length() != TokenUtil.X_CSRF_TOKEN_SIZE ||
            !TokenUtil.checkSCSRFToken(xCsrfToken, sCsrfToken)){
            HttpRequestUtil.rejectZuul(HttpStatus.OK.value(), API_INVALID_CSRF_TOKEN);
        }

        return null;
    }
}
