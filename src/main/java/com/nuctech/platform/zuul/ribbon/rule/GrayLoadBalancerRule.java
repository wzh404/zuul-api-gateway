package com.nuctech.platform.zuul.ribbon.rule;


import com.netflix.loadbalancer.DynamicServerListLoadBalancer;
import com.netflix.loadbalancer.Server;
import com.netflix.loadbalancer.ZoneAvoidanceRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 灰度发布负载均衡算法
 *
 * Created by @author wangzunhui on 2018/5/16.
 */
public class GrayLoadBalancerRule extends ZoneAvoidanceRule {
    private final Logger logger = LoggerFactory.getLogger(GrayLoadBalancerRule.class);

    @Override
    public Server choose(Object key) {
        DynamicServerListLoadBalancer loadBalancer = (DynamicServerListLoadBalancer)getLoadBalancer();
        if (loadBalancer == null) {
            logger.warn("Could not be load balancer");
            return super.choose(key);
        }
        List<Server> serverList = loadBalancer.getReachableServers();
        String client = loadBalancer.getClientConfig().getClientName();
        logger.info("----------{} : {}---------", client, serverList.toString());
        //TODO 待完善
        for (Server server : getPredicate().getEligibleServers(serverList)){
            logger.info("Server-----------------{}", server.getHostPort());
        }
        return super.choose(key);
    }
}
