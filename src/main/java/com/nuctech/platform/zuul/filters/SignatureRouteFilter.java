package com.nuctech.platform.zuul.filters;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.nuctech.platform.auth.bean.User;
import com.nuctech.platform.auth.whitelist.Whitelists;
import com.nuctech.platform.util.CryptoUtil;
import com.nuctech.platform.util.KeyPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.ROUTE_TYPE;

/**
 * 路由到后端业务服务过滤器
 * <p>
 * Created by @author wangzunhui on 2017/8/13.
 */
@Component
public class SignatureRouteFilter extends ZuulFilter {
    private final Logger logger = LoggerFactory.getLogger(SignatureRouteFilter.class);

    @Autowired
    private Whitelists whitelists;

    /**
     * 返回过滤器类型
     *
     * @return
     */
    @Override
    public String filterType() {
        return ROUTE_TYPE;
    }

    @Override
    public int filterOrder() {
        return 9;
    }

    /**
     * 是否执行此过滤器
     *
     * @return
     */
    @Override
    public boolean shouldFilter() {
        RequestContext ctx = RequestContext.getCurrentContext();
        return ctx.sendZuulResponse();
    }

    /**
     * 路由当前客户ID、跟踪号及签名授权信息到后端业务服务
     *
     * @return
     */
    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        String uri = ctx.getRequest().getRequestURI();

        if (whitelists.inAuthenticator(uri)) {
            return null;
        }

        User user = (User) ctx.getRequest().getAttribute(AuthenticatorPreFilter.REQUEST_ATTRIBUTE_USER);
        String tid = (String) ctx.getRequest().getAttribute(AuthenticatorPreFilter.REQUEST_ATTRIBUTE_TID);
        assert user != null : "The user information cannot be empty";
        assert tid != null : "Request tracking number cannot be empty";

        logger.info("route to " + uri);
        String signed = CryptoUtil.signature(KeyPool.getKey(tid), user.getId(), tid);

        // user id
        ctx.addZuulRequestHeader("X-APP-UID", user.getId());
        // user organization id
        ctx.addZuulRequestHeader("X-APP-OID", user.getOrgId());
        // trace number
        ctx.addZuulRequestHeader("X-APP-TID", tid);
        // signature data
        ctx.addZuulRequestHeader("X-APP-TOKEN", signed);

        return null;
    }
}
