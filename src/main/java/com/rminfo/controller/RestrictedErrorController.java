package com.rminfo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 */
//@Controller
//public class RestrictedErrorController implements ErrorController {
//    private static final String ERROR_PATH = "/error";
//
//    @Autowired
//    private ErrorAttributes errorAttributes;
//
//    @Override
//    public String getErrorPath() {
//        return ERROR_PATH;
//    }
//
//    @RequestMapping(ERROR_PATH)
//    String error(HttpServletRequest request, Model model) {
//        Map<String, Object> errorMap = errorAttributes.getErrorAttributes((WebRequest) new ServletRequestAttributes(request), false);
//        model.addAttribute("errors", errorMap);
//        return "error";
//    }
//}