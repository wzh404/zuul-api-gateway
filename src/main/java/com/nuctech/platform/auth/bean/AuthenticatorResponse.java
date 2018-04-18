package com.nuctech.platform.auth.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.nuctech.platform.auth.bean.User;
import lombok.Data;

/**
 * Created by @author wangzunhui on 2018/4/12.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthenticatorResponse {
    @JsonProperty("flag")
    private Boolean flag;

    @JsonProperty("result")
    private Result result;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public class Result{
        private String id;
        private String name;
        private String orgId;
        private Integer timeout;
    }

    public User asUser(){
        User user = new User();
        user.setId(getResult().getId());
        user.setName(getResult().getName());
        user.setOrgId(getResult().getOrgId());
        user.setTimeout(getResult().getTimeout());
        user.setCreateTime(String.valueOf(System.currentTimeMillis()));

        return user;
    }
}
