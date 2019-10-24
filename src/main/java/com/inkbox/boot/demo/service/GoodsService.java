package com.inkbox.boot.demo.service;

import com.alibaba.fastjson.JSON;
import com.inkbox.boot.demo.dao.GoodsDao;
import com.inkbox.boot.demo.dos.GoodsDo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * @author 墨盒
 * @date 2019/10/24
 */
@Service
public class GoodsService {
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private GoodsDao dao;

    @Autowired
    private RedisTemplate<String, String> template;


    public GoodsDo getGoods(Long id) {
        Object value = template.opsForValue().get("goods_id_" + id);
        logger.debug("redis value = {}", value);
        if (value != null) {
            logger.debug("从redis中查询到");
            return JSON.parseObject(value.toString(), GoodsDo.class);
        }
        synchronized (this) {
            value = template.opsForValue().get("goods_id_" + id);
            logger.debug("redis value = {}", value);
            if (value != null) {
                logger.debug("从redis中查询到");
                return JSON.parseObject(value.toString(), GoodsDo.class);
            }

            GoodsDo byId = dao.getOne(id);
            logger.debug("查询数据库db");
            template.opsForValue().set("goods_id_" + id, JSON.toJSONString(byId), 2000L, TimeUnit.SECONDS);

            return byId;
        }


    }


}
