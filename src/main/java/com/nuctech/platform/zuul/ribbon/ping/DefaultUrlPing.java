package com.nuctech.platform.zuul.ribbon.ping;

import com.netflix.loadbalancer.PingUrl;

/**
 * Created by wangzunhui on 2018/6/9.
 */
public class DefaultUrlPing extends PingUrl {
    public DefaultUrlPing(){
        super(false,"/user/ping");
    }
}
