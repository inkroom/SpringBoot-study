package com.inkbox.boot.demo.controllers;

import com.inkbox.boot.demo.config.MQConfig;
import com.inkbox.boot.demo.dao.UserDao;
import com.inkbox.boot.demo.dos.UserDo;
import com.inkbox.boot.demo.dto.Position;
import org.apache.tomcat.util.http.fileupload.IOUtils;
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

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Enumeration;

@RestController
public class IndexController {
    @Autowired
    private UserDao dao;

    private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @RequestMapping("zdb")
    public String data() throws IOException {
        //获取参数
        Enumeration<String> parameterNames = request.getParameterNames();

        while (parameterNames.hasMoreElements()) {
            String key = parameterNames.nextElement();
            logger.debug("name-{}={}", key, request.getParameter(key));
        }

        logger.debug("请求体中的数据");

        ServletInputStream inputStream = request.getInputStream();


        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line = null;
        while ((line = reader.readLine()) != null) {
            logger.debug(line);
        }
        return "{\"data\":\"ok\"}";
//http://inkbox.vaiwan.com/zdb
    }

    @GetMapping("index")
    public String index(UserDo userDo) {
//        {"app_key":"5767ae612a1902be39ce0f75b1a667af","type":10,"timestamp":1577800196,"message":"{\"orderId\":\"2019123150132\",\"out_order_sn\":\"2019123150132\",\"status\":\"WAIT_DELIVERY\",\"logistics_id\":\"0\",\"logistics_name\":\"\",\"order_real_photo_info\":[],\"receive_store_name\":null,\"receive_store_phone\":null}","requestId":"6e308126-2bd4-11ea-b693-00163e0d6143","sig":"6f8e92a9179ba84304428603b7fd6d23"}
//        {"app_key":"5767ae612a1902be39ce0f75b1a667af","type":10,"timestamp":1577800196,"message":"{\"orderId\":\"20191231287520397\",\"out_order_sn\":\"2019123150132\",\"status\":\"WAIT_DELIVERY\",\"logistics_id\":\"0\",\"logistics_name\":\"\",\"order_real_photo_info\":[],\"receive_store_name\":null,\"receive_store_phone\":null}","requestId":"6e308126-2bd4-11ea-b693-00163e0d6143","sig":"6f8e92a9179ba84304428603b7fd6d23"}
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



        rabbitTemplate.convertAndSend(MQConfig.DEAD_LETTER_EXCHANGE, MQConfig.DELAY_QUEUEA_ROUTING_KEY, msg, message -> {
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
