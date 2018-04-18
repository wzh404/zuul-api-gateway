package com.nuctech.platform.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.zuul.context.RequestContext;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Created by @author wangzunhui on 2017/8/14.
 */
public class HttpRequestUtil {
    private static final String UNKNOWN = "unKnown";

    private HttpRequestUtil() {
        throw new IllegalStateException("Utility Http Request");
    }

    /**
     * 根据cookie名称查询cookie值
     *
     * @param request
     * @param name cookie名称
     * @return
     */
    public static Optional<String> getCookieValue(HttpServletRequest request, String name){
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return Optional.ofNullable(null);
        }

        Optional<Cookie> cookie = Arrays.stream(cookies)
                .filter(c -> name.equalsIgnoreCase(c.getName()))
                .findFirst();

        return cookie.map(c -> Optional.of(c.getValue()))
                .orElse(Optional.empty());
    }

    /**
     * 获取客户端真是IP
     *
     * @param request
     * @return
     */
    public static String getRemoteAddr(HttpServletRequest request) {
        String[] filters = {"X-Forwarded-For",
                "X-Real-IP",
                "Proxy-Client-IP",
                "WL-Proxy-Client-IP",
                "HTTP_CLIENT_IP",
                "HTTP_X_FORWARDED_FOR"
        };

        return Arrays.stream(filters)
                .map(name -> check(request, name).orElse(UNKNOWN))
                .filter(s -> !UNKNOWN.equalsIgnoreCase(s))
                .findFirst()
                .orElseGet(request::getRemoteAddr);
    }

    /**
     * 检查header是否存在name的值，并取逗号分隔的第一个值。
     *
     * @param request
     * @param name
     * @return
     */
    private static Optional<String> check(HttpServletRequest request, String name){
        String ip = request.getHeader(name);
        if(StringUtils.isNotEmpty(ip) && !UNKNOWN.equalsIgnoreCase(ip)){
            // Get the first ip address.
            int index = ip.indexOf(',');
            return Optional.of(index == -1 ? ip : ip.substring(0, index));
        }

        return Optional.empty();
    }

    /**
     * Send the error code to web browser or client.
     *
     * @param status
     * @param err
     */
    public static void rejectZuul(int status, ErrorCodeEnum err) {
        RequestContext ctx = RequestContext.getCurrentContext();
        ctx.setSendZuulResponse(false);
        ctx.setResponseStatusCode(status);
        ctx.getResponse().setContentType("application/json");
        ctx.setResponseBody(error2json(err.getCode()));
    }

    public static String error2json(String errorCode){
        Map<String, Object> map = getErrorResultMap(errorCode, "");

        try {
            return (new ObjectMapper()).writeValueAsString(map);
        } catch (IOException e) {
            StringBuilder message = new StringBuilder("{'flag': false,'errorCode':'");
            message.append(errorCode);
            message.append("'}");
            return message.toString();
        }
    }

    public static Map<String, Object> getErrorResultMap(String errorCode, String message){
        Map<String, Object> map = new HashMap<>(16);
        map.put("flag", false);
        map.put("errorCode", errorCode);
        map.put("message", message);

        return map;
    }
}
