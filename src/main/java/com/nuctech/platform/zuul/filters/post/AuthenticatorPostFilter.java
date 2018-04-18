package com.nuctech.platform.zuul.filters.post;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import com.nuctech.platform.auth.bean.AuthenticatorResponse;
import com.nuctech.platform.auth.service.UserService;
import com.nuctech.platform.util.TokenUtil;
import com.nuctech.platform.zuul.filters.support.FilterConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import javax.servlet.http.Cookie;
import java.io.InputStream;
import java.nio.charset.Charset;

import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.POST_TYPE;

/**
 * 认证成功后处理过滤器
 * <p>
 * Created by @author wangzunhui on 2018/4/12.
 */
@Component
public class AuthenticatorPostFilter extends ZuulFilter {
    private final Logger logger = LoggerFactory.getLogger(AuthenticatorPostFilter.class);

    @Value(value = "${nuctech.login.uri}")
    private String loginUri;

    @Autowired
    private UserService userService;

    @Override
    public String filterType() {
        return POST_TYPE;
    }

    @Override
    public int filterOrder() {
        return FilterConstants.POST_AUTHENTICATOR_FILTER_ORDER;
    }

    @Override
    public boolean shouldFilter() {
        RequestContext ctx = RequestContext.getCurrentContext();
        String uri = ctx.getRequest().getRequestURI();

        return uri.equalsIgnoreCase(loginUri);
    }

    @Override
    public Object run() throws ZuulException {
        RequestContext ctx = RequestContext.getCurrentContext();
        InputStream stream = ctx.getResponseDataStream();

        try {
            String body = StreamUtils.copyToString(stream, Charset.forName("UTF-8"));
            AuthenticatorResponse login = (new ObjectMapper()).readValue(body, AuthenticatorResponse.class);
            handleLogged(login);
            ctx.setResponseBody(body);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("convert login response to json failed.");
        }

        return null;
    }

    /**
     * add cookie to response header.
     *
     * @param key
     * @param value
     */
    private void addCookie(String key, String value) {
        RequestContext ctx = RequestContext.getCurrentContext();

        Cookie c = new Cookie(key, value);
        //c.setSecure(true);
        c.setHttpOnly(true);
        c.setPath("/");
        ctx.getResponse().addCookie(c);
    }

    /**
     * Handle login success. add cookie and response headers.
     *
     * @param login
     */
    private void handleLogged(AuthenticatorResponse login) {
        if (!login.getFlag()) {
            return;
        }

        RequestContext ctx = RequestContext.getCurrentContext();

        // create token and save to cache.
        String token = userService.createToken(login.asUser());
        logger.info("token is {}", token);

        String xCsrfToken = TokenUtil.generateXCSRFToken();
        String sCsrfToken = TokenUtil.generateSCSRFToken(xCsrfToken);

        addCookie(TokenUtil.TOKEN, token);
        addCookie(TokenUtil.S_CSRF_TOKEN, sCsrfToken);
        ctx.addZuulResponseHeader(TokenUtil.X_CSRF_TOKEN, xCsrfToken);
    }
}
