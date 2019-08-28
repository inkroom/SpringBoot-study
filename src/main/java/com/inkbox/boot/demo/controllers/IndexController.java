package com.inkbox.boot.demo.controllers;

import com.inkbox.boot.demo.dao.UserDao;
import com.inkbox.boot.demo.dos.UserDo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IndexController {
    @Autowired
    private UserDao dao;

    @GetMapping("index")
    public String index(UserDo userDo) {

        return String.valueOf(dao.save(userDo));

    }

}
