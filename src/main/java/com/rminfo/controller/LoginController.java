package com.rminfo.controller;

import com.hazelcast.util.MD5Util;
import com.rminfo.model.User;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    @RequestMapping("/login.html")
    public String loginTemplate() {

        return "login";
    }

    @RequestMapping(value = "/login",method = RequestMethod.POST)
    public String login(
            User user,
            @RequestParam(value="rememberMe",required = false)boolean rememberMe){
        // 1、 封装用户名、密码、是否记住我到token令牌对象  [支持记住我]
        AuthenticationToken token = new UsernamePasswordToken(
                user.getMobile(), MD5Util.toMD5String(user.getPassword()),rememberMe);

        return "index";
    }

}