package com.nuctech.platform.controller;

import com.nuctech.platform.util.ErrorCodeEnum;
import com.nuctech.platform.util.HttpRequestUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * ZuulExcepiton redirect to this controller.
 *
 * Created by @author wangzunhui on 2018/4/16.
 */
@Controller
@RequestMapping("${server.error.path:/error}")
public class GlobalErrorController implements ErrorController {
    private final Logger logger = LoggerFactory.getLogger(GlobalErrorController.class);

    @Override
    public String getErrorPath() {
        return StringUtils.EMPTY;
    }

    @RequestMapping
    @ResponseBody
    public ResponseEntity<Map<String, Object>> error(HttpServletRequest request) {
        Map<String, Object> body = HttpRequestUtil.getErrorResultMap(ErrorCodeEnum.API_INTERNAL_EXCEPTION.getCode(), "zuul exception");
        return  new ResponseEntity<>(body, getStatus(request));
    }

    protected HttpStatus getStatus(HttpServletRequest request) {
        Integer statusCode = (Integer) request
                .getAttribute("javax.servlet.error.status_code");
        if (statusCode == null) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
        try {
            return HttpStatus.valueOf(statusCode);
        }
        catch (Exception ex) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }
}
