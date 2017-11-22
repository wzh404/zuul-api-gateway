package com.nuctech.platform.auth;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Created by wangzunhui on 2017/10/10.
 */
@FeignClient(value = "user")
@FunctionalInterface
public interface UserService {
    @RequestMapping(value = "/permission.json", method =RequestMethod.GET)
    public UserPermission permits(@RequestParam(value = "uid") String uid);
}
