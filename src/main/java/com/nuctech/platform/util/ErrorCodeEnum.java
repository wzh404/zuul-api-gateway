package com.nuctech.platform.util;

/**
 * Created by @author wangzunhui on 2017/11/28.
 */

public enum ErrorCodeEnum {
    API_SUCCESS("OK"),
    API_RATE_LIMITER("020010"),
    API_EMPTY_USER_PERMISSION("020003"),
    API_NOT_PERMIT("020002"),
    API_INVALID_TOKEN("020001"),
    API_CSRF_TOKEN_NOT_FOUND("020005"),
    API_INVALID_CSRF_TOKEN("020006"),
    API_INTERNAL_EXCEPTION("029999");
    private String code;

    ErrorCodeEnum(String code){
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
