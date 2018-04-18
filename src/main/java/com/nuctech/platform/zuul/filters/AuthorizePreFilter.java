package com.nuctech.platform.zuul.filters;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.nuctech.platform.auth.bean.User;
import com.nuctech.platform.auth.service.UserService;
import com.nuctech.platform.auth.whitelist.Whitelists;
import com.nuctech.platform.util.ErrorCodeEnum;
import com.nuctech.platform.util.HttpRequestUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import static com.nuctech.platform.util.ErrorCodeEnum.*;
import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.PRE_TYPE;

/**
 * 用户鉴权过滤器
 *
 * Created by @author wangzunhui on 2017/9/21.
 */
@Component
public class AuthorizePreFilter extends ZuulFilter {
    private final Logger logger = LoggerFactory.getLogger(AuthorizePreFilter.class);

    @Autowired
    private UserService userService;

    @Autowired
    private Whitelists whitelists;

    @Override
    public String filterType() {
        return PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return 4;
    }

    @Override
    public boolean shouldFilter() {
        RequestContext ctx = RequestContext.getCurrentContext();
        String uri = ctx.getRequest().getRequestURI();

        // Check if uri is in the white list
        if (whitelists.inAuthorize(uri)){
            return false;
        }
        return ctx.sendZuulResponse();
    }

    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        User user = (User) ctx.getRequest().getAttribute(AuthenticatorPreFilter.REQUEST_ATTRIBUTE_USER);

        // Check uri for access.
        String uri = ctx.getRequest().getRequestURI();
        ErrorCodeEnum errorCode = userService.checkAuthorize(user.getId(), uri);
        if (errorCode != API_SUCCESS){
            HttpRequestUtil.rejectZuul(HttpStatus.OK.value(), errorCode);
        }

        return null;
    }
}
