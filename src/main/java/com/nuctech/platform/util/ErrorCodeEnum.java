package com.nuctech.platform.util;

/**
 * Created by @author wangzunhui on 2017/11/28.
 */

public enum ErrorCodeEnum {
    API_RATE_LIMITER("api.rate.limiter"),
    API_TOKEN_NOT_FOUND("api_token_not_found"),
    API_EMPTY_USER_PERMISSION("api_empty_user_permission"),
    API_NOT_PERMIT("api_not_permit"),
    API_INVALID_TOKEN("api_invalid_token"),
    API_CSRF_OR_TOKEN_NOT_FOUND("api_csrf_or_token_not_found"),
    API_INVALID_CSRF_TOKEN("api_invalid_csrf_token");
    private String code;

    public String getCode() {
        return code;
    }

    private ErrorCodeEnum(String code){
        this.code = code;
    }
}
