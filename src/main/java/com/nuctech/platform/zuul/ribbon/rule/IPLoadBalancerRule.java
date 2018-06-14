package com.nuctech.platform.zuul.ribbon.rule;

import com.google.common.hash.Hashing;
import com.netflix.loadbalancer.BaseLoadBalancer;
import com.netflix.loadbalancer.DynamicServerListLoadBalancer;
import com.netflix.loadbalancer.Server;
import com.netflix.loadbalancer.ZoneAvoidanceRule;
import com.nuctech.platform.zuul.filters.support.HystrixThreadContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.util.List;

/**
 * 根据client IP选择服务器负载均衡算法
 *
 * Created by @author wangzunhui on 2018/5/16.
 */
public class IPLoadBalancerRule extends ZoneAvoidanceRule {
    private final Logger logger = LoggerFactory.getLogger(IPLoadBalancerRule.class);

    @Override
    public Server choose(Object key) {
        String clientIP = HystrixThreadContext.get();
        if (clientIP == null){
            clientIP = "localhost";
        }

        HystrixThreadContext.remove();

        DynamicServerListLoadBalancer loadBalancer = (DynamicServerListLoadBalancer)getLoadBalancer();
        if (loadBalancer == null) {
            logger.warn("Could not be load balancer");
            return super.choose(key);
        }
        List<Server> serverList = getPredicate().getEligibleServers(loadBalancer.getReachableServers());
        if (serverList.isEmpty()){
            return super.choose(key);
        }

        logger.info("server list is {}.", serverList.toString());
        int hashValue = Hashing.murmur3_32().hashString(clientIP, Charset.forName("utf-8")).asInt();
        logger.info("{}.",Math.abs(hashValue) % serverList.size());
        return (serverList.toArray(new Server[0]))[Math.abs(hashValue) % serverList.size()];
    }
}
