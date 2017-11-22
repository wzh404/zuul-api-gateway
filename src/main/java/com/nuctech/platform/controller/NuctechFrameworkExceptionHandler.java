package com.nuctech.platform.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by wangzunhui on 2017/8/8.
 */
@ControllerAdvice
public class NuctechFrameworkExceptionHandler {
    private final static Logger logger = LoggerFactory.getLogger(NuctechFrameworkExceptionHandler.class);

    @ExceptionHandler(RuntimeException.class)
    @ResponseBody
    public Map<String, Object> exceptionHandler(RuntimeException e, HttpServletResponse response) {
        Map<String, Object> map = new HashMap<>(16);
        logger.error("frame exception:", e);
        map.put("code", "error");
        map.put("msg", e.getMessage());

        return map;
    }
}
