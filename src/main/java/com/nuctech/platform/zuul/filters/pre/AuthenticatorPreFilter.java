package com.nuctech.platform.zuul.filters.pre;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.nuctech.platform.auth.bean.User;
import com.nuctech.platform.auth.service.UserService;
import com.nuctech.platform.auth.whitelist.Whitelists;
import com.nuctech.platform.util.HttpRequestUtil;
import com.nuctech.platform.util.JSnowFlake;
import com.nuctech.platform.util.TokenUtil;
import com.nuctech.platform.zuul.filters.support.FilterConstants;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static com.nuctech.platform.util.ErrorCodeEnum.API_INVALID_TOKEN;
import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.PRE_TYPE;

/**
 * 用户认证过滤器
 *
 * <p>
 * Created by @author wangzunhui on 2017/10/9.
 */
@Component
public class AuthenticatorPreFilter extends ZuulFilter {
    private final Logger logger = LoggerFactory.getLogger(AuthenticatorPreFilter.class);
    private static final String SLF4J_MDC_TRACKING_NUMBER = "tracking";

    public static final String REQUEST_ATTRIBUTE_USER = "_user";
    public static final String REQUEST_ATTRIBUTE_TID = "_tid";
    public static final String REQUEST_ATTRIBUTE_START_TIME = "_start_time";
    public static final JSnowFlake traceGen = new JSnowFlake(0);

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
        return FilterConstants.PRE_AUTHENTICATOR_FILTER_ORDER;
    }

    @Override
    public boolean shouldFilter() {
        RequestContext ctx = RequestContext.getCurrentContext();

        // generate a unique tracking number.
        String trackingNumber = Long.toString(traceGen.nextId());
        ctx.getRequest().setAttribute(REQUEST_ATTRIBUTE_TID, trackingNumber);
        MDC.put(SLF4J_MDC_TRACKING_NUMBER, trackingNumber);

        String uri = ctx.getRequest().getRequestURI();
        if (whitelists.inAuthenticator(uri)){
            return false;
        }
        return ctx.sendZuulResponse();
    }

    /**
     * handle authenticator previous filter.
     *
     * @return
     */
    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        Optional<String> token = HttpRequestUtil.getCookieValue(ctx.getRequest(), TokenUtil.TOKEN);
        logger.info("token is {}", token);

        return token.map(userService::getUser)
                .map(this::setAttribute)
                .orElseGet(() -> {
                    HttpRequestUtil.rejectZuul(HttpStatus.OK.value(), API_INVALID_TOKEN);
                    return null;
                });
    }

    /**
     * set the request attribute value after the user has logged in successfully.
     *
     * @param user User entity
     * @return
     */
    private String setAttribute(Optional<User> user) {
        if (!user.isPresent()) {
            logger.warn("get user failed.");
            return null;
        }

        RequestContext ctx = RequestContext.getCurrentContext();
        ctx.getRequest().setAttribute(REQUEST_ATTRIBUTE_USER, user.get());

        return StringUtils.EMPTY;
    }
}
