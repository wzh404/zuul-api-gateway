package com.nuctech.platform.zuul.filters.support;

import org.springframework.cloud.netflix.zuul.filters.route.RibbonRoutingFilter;

/**
 * Hystrix策略为Thread时，RequestContext使用ThreadLocal会丢失本地线程数据。
 *
 * @see RibbonRoutingFilter
 * Created by  @author wangzunhui on 2018/5/29.
 */
public class HystrixThreadContext {
    private static ThreadLocal<String> threadLocal = new InheritableThreadLocal<>();

    public static void set(String val){
        threadLocal.set(val);
    }

    public static String get(){
        return threadLocal.get();
    }

    public static void remove(){
        threadLocal.remove();
    }
}
