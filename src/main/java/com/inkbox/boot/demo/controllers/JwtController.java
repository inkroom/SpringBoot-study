package com.inkbox.boot.demo.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("jwt")
public class JwtController {

    private Logger logger = LoggerFactory.getLogger(getClass());


    private static final String TOKEN_NAME = "_TOKEN_";

    /**
     * 创建jwt
     *
     * @return
     */
    @RequestMapping("create")
    public String create() {

        return null;
    }

    /**
     * 解析jwt
     *
     * @return
     */
    public String get() {
        return null;
    }
}
