package com.inkbox.boot.demo.controllers;

import com.inkbox.boot.demo.dao.UserDao;
import com.inkbox.boot.demo.dos.UserDo;
import com.inkbox.boot.demo.dto.Position;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class IndexController {
    @Autowired
    private UserDao dao;

    private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private HttpServletRequest request;

    @GetMapping("index")
    public String index(UserDo userDo) {


        request.getSession().setAttribute("user", userDo);

        return String.valueOf(dao.save(userDo));

    }

    @GetMapping("show")
    @CrossOrigin
    public String show() {


        //TODO 2019/8/29 如果引入了devtools会导致相同的class也无法强转
        UserDo userDo = (UserDo) request.getSession().getAttribute("user");
        if (userDo == null)
            return "session没存上";
        logger.debug("原本的{},hashcode={},反序列化的{},hashcode={}", UserDo.class, UserDo.class.hashCode(), userDo.getClass(), userDo.getClass().hashCode());

        return userDo.toString() + "  -  " + userDo.getClass() + "  --   " + (UserDo.class.equals(userDo.getClass()));

    }

    @RequestMapping("dto")
    public Position dto() {
        return new Position(239, 43);
    }

}
