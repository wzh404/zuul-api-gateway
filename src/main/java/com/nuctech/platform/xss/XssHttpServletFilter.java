package com.nuctech.platform.xss;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Created by @author wangzunhui on 2017/8/15.
 */
public class XssHttpServletFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    // not used
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        chain.doFilter(new XssHttpServletRequestWrapper((HttpServletRequest) request), response);
    }

    @Override
    public void destroy() {
    // not used
    }
}
