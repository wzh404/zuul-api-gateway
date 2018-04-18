package com.nuctech.platform.exception;

/**
 * Created by @author wangzunhui on 2017/8/7.
 */
public class NuctechPlatformException extends RuntimeException {
    protected final String errorMsg;

    public NuctechPlatformException(String message) {
        super(message);
        this.errorMsg = message;
    }

    public NuctechPlatformException(String message, Throwable cause) {
        super(message, cause);
        this.errorMsg = message;
    }

    @Override
    public Throwable fillInStackTrace() {
        return this;
    }
}
