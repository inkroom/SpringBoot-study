package com.inkbox.boot.demo.controllers;

import com.inkbox.boot.demo.config.MQConfig;
import com.inkbox.boot.demo.dao.UserDao;
import com.inkbox.boot.demo.dos.UserDo;
import com.inkbox.boot.demo.dto.Position;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
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
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @GetMapping("index")
    public String index(UserDo userDo) {


        request.getSession().setAttribute("user", userDo);

        return String.valueOf(dao.save(userDo));

    }

    /**
     * @param msg 消息
     * @param ttl 时延，单位秒
     * @return
     */
    @GetMapping("msg")
    public String msg(String msg, int ttl) {

        logger.debug("发送消息:{}", msg);



        rabbitTemplate.convertAndSend(MQConfig.CUSTOM_DELAYED_EXCHANGE_NAME, MQConfig.DELAY_QUEUEA_ROUTING_KEY, msg, message -> {
            message.getMessageProperties().setDelay(ttl * 1000);
            return message;
        });

        return msg;

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
