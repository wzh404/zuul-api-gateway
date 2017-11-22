package com.nuctech.platform.controller;

import com.nuctech.platform.auth.User;
import com.nuctech.platform.auth.UserPermission;
import com.nuctech.platform.auth.UserService;
import com.nuctech.platform.util.CryptoUtil;
import com.nuctech.platform.util.HttpRequestUtil;
import com.nuctech.platform.util.KeyPool;
import com.nuctech.platform.util.TokenUtil;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wangzunhui on 2017/8/1.
 */
@RestController
public class UserController {
    private final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/home",method = RequestMethod.GET)
    public String home(HttpServletRequest request){
        //MDC.put("Trace-Id", Long.toString(UserRealm.snowFlake.nextId()));
        String uid = request.getHeader("X-USER-ID");
        String tid = request.getHeader("X-TRACE-ID");
        String sign = request.getHeader("X-AUTH-CODE");
        logger.info(request.getHeader("X-TRACE-ID") + "------" + request.getHeader("X-USER-ID"));
        logger.info(request.getHeader("X-AUTH-CODE"));

        String calSign = CryptoUtil.signature(KeyPool.DEFAULT_KEY, uid, tid);
        if (calSign.equalsIgnoreCase(sign)){
            logger.info("-------------Signatured----------");
        }
        return "please login";
    }

    @RequestMapping(value = "/403",method = RequestMethod.GET)
    public String unauth(){
        logger.info("--------------403----------------");
        UserPermission permission = userService.permits("admin");
        //logger.info("----------" + (permission==null? 0: permission.getPermissions().size()));
        if (permission != null){
            permission.getPermissions().forEach(s -> logger.info(s));
        }
        return "Unauthorized";
    }

    @RequestMapping(value = "/i18n",method = RequestMethod.GET)
    public String i18n(HttpServletRequest request, @RequestParam String v1){

        String ip = HttpRequestUtil.getRemoteAddr(request);
        logger.info("[" + ip + "]");
//        throw new IllegalStateException("test");
        return "i18n";
    }

    @RequestMapping(value = "/hi",method = {RequestMethod.GET, RequestMethod.POST})
    public User sayHi(/*@RequestParam String name*/ @RequestBody User user){
        logger.info(user.getTime() + " " + user.getType() + " {" + user.getName() + " - " + user.getFree() + ":" + user.getTotal() + "}");

        return null;//userService.getUser(name);
    }

    @RequestMapping(value = "/user/permits",method = RequestMethod.GET)
    public Map<String, Object> permits(@RequestParam String name) {
        logger.info("--------------permits----------------");
        Map<String, Object> map = new HashMap();
        map.put("flag", true);
        List<String> permits = new ArrayList<String>();
        permits.add("/user/**");
        permits.add("/test/home");
        permits.add("/test/hi");
        map.put("user", permits);

        return map;
    }

    @RequestMapping(value = "/user",method = RequestMethod.GET)
    public User getUser(@RequestParam String name){
        User user = new User();
        logger.info("--------------user----------------");
        //user.addRole("admin", new String[]{"a", "b"});
        //user.addRole("user", new String[]{"u1", "u2"});
        //user.setResultCode("OK");

        return user;
    }

    @RequestMapping(value = "/slogin",method = RequestMethod.GET)
    public User slogin(HttpServletResponse response, @RequestParam String name, @RequestParam String pwd){
        String jwtToken = TokenUtil.generatorToken(name);
        String csrfToken = TokenUtil.generatorCSRFToken(jwtToken);

        Cookie cookie = new Cookie(TokenUtil.TOKEN, jwtToken);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        // TODO change to true in https
        cookie.setSecure(false);

        Cookie langCookie = new Cookie("lang", "en_US");
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        // TODO change to true in https
        cookie.setSecure(false);

        Cookie csrfCookie = new Cookie(TokenUtil.CSRF_TOKEN, csrfToken);
        cookie.setPath("/");

        response.addCookie(cookie);
        response.addCookie(csrfCookie);
        response.addCookie(langCookie);

        logger.info("--------------slogin----------------");
        User user = new User();
        //user.setResultCode("OK");
        /*UsernamePasswordToken token = new UsernamePasswordToken(name, pwd, null);
        try {
            SecurityUtils.getSubject().login(token);
            user.setResultCode("OK");
        } catch (AuthenticationException e){
            user.setResultCode("ERR09");
            user.setResultMsg(e.getMessage());
        }*/
        return user;
    }

    @RequestMapping(value = "/login",method = RequestMethod.GET)
    public User login(HttpServletResponse response, @RequestParam String name, @RequestParam String pwd){
        User user = new User();
        //user.addRole("admin", new String[]{"a", "b"});
        //user.addRole("user", new String[]{"u1", "u2"});
        //user.setResultCode("OK");
        //user.setResultMsg("ERROR");
        logger.info("--------------login----------------");
        return user;
    }
}
