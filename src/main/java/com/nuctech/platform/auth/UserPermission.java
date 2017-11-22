package com.nuctech.platform.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangzunhui on 2017/10/17.
 */
@Data
public class UserPermission {
    @JsonProperty("flag")
    private Boolean flag;

    @JsonProperty("result")
    private Result result;

    public List<String> getPermissions(){
        List<String> list = new ArrayList<>();
        if (!getFlag()){
            return list;
        }

        getResult().getPermissions().stream()
                .filter(p -> StringUtils.isNotEmpty(p.getUrl()))
                .forEach(p -> list.add(p.getUrl()));

        return list;
    }

    @Data
    static class Result{
        @JsonProperty("userPermissions")
        List<Permission> permissions;
    }

    @Data
    static class Permission{
        @JsonProperty("prmUrl")
        String url;

        @JsonProperty("childPerms")
        List<Permission> childPerms;
    }
}
