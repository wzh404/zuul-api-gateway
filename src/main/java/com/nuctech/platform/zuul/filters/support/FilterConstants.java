package com.nuctech.platform.zuul.filters.support;

/**
 * Created by @author wangzunhui on 2018/4/18.
 */
public interface FilterConstants {
    /**
     * Filter Order for {@link com.nuctech.platform.zuul.filters.pre.RateLimiterPreFilter#filterOrder()}
     */
    int PRE_RATELIMITER_FILTER_ORDER= 2;

    /**
     * Filter Order for {@link com.nuctech.platform.zuul.filters.pre.AuthenticatorPreFilter#filterOrder()}
     */
    int PRE_AUTHENTICATOR_FILTER_ORDER = 3;

    /**
     * Filter Order for {@link com.nuctech.platform.zuul.filters.pre.AuthorizePreFilter#filterOrder()}
     */
    int PRE_AUTHORIZE_FILTER_ORDER = 4;

    /**
     * Filter Order for {@link com.nuctech.platform.zuul.filters.pre.CsrfPreFilter#filterOrder()}
     */
    int PRE_CSRF_FILTER_ORDER = 5;

    /**
     * Filter Order for {@link com.nuctech.platform.zuul.filters.post.AuthenticatorPostFilter#filterOrder()}
     */
    int POST_AUTHENTICATOR_FILTER_ORDER = 101;

    /**
     * Filter Order for {@link com.nuctech.platform.zuul.filters.post.AccessLoggerPostFilter#filterOrder()}
     */
    int POST_ACCESS_LOGGER_FILTER_ORDER = 109;

    /**
     * Filter Order for {@link com.nuctech.platform.zuul.filters.route.SignatureRouteFilter#filterOrder()}
     */
    int ROUTING_SIGNATURE_FILTER_ORDER = 8;
}
