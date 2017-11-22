package com.nuctech.platform.xss;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by wangzunhui on 2017/8/15.
 */
@Configuration
public class XssFilterConfiguration {
    @Bean
    public XssHttpServletFilter xssHttpServletFilter() {
        return new XssHttpServletFilter();
    }

    @Bean
    public FilterRegistrationBean filterRegistrationBean() {
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        registrationBean.addUrlPatterns("/*");
        registrationBean.setFilter(xssHttpServletFilter());
        registrationBean.setName("xssFilter");
        registrationBean.setOrder(1);

        return registrationBean;
    }
}
