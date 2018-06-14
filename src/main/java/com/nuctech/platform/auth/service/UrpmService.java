package com.nuctech.platform.auth.service;

import com.nuctech.platform.auth.bean.AuthorizeResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Created by wangzunhui on 2017/10/10.
 */
@FeignClient(value = "user")
@FunctionalInterface
public interface UrpmService {
    /**
     * Get user authority from urpm.
     *
     * @param account user account.
     * @return
     */
    @RequestMapping(value = "${nuctech.user.prms}", method =RequestMethod.POST)
    AuthorizeResponse authorize(@RequestParam(value = "account") String account);
}
