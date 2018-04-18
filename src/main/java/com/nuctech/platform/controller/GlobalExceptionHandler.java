package com.nuctech.platform.controller;

import com.netflix.zuul.exception.ZuulException;
import com.nuctech.platform.util.ErrorCodeEnum;
import com.nuctech.platform.util.HttpRequestUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.netflix.zuul.util.ZuulRuntimeException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by @author wangzunhui on 2017/8/8.
 */
@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler({RuntimeException.class, ZuulException.class, ZuulRuntimeException.class})
    @ResponseBody
    public Map<String, Object> exceptionHandler(RuntimeException e, HttpServletResponse response) {
        logger.error("global exception:", e);
        return HttpRequestUtil.getErrorResultMap(ErrorCodeEnum.API_INTERNAL_EXCEPTION.getCode(), e.getMessage());
    }
}
