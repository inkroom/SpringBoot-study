package com.inkbox.boot.demo;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.context.WebApplicationContext;

import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisTest {
    @Autowired
    private RedisTemplate<Object, Object> template;
    @Autowired
    private WebApplicationContext context;

    private Logger logger = LoggerFactory.getLogger(getClass());


    static class TestPosition {
        int x, y;

        public int getX() {
            return x;
        }

        public void setX(int x) {
            this.x = x;
        }

        public int getY() {
            return y;
        }

        public void setY(int y) {
            this.y = y;
        }

        @Override
        public String toString() {
            return "TestPosition{" +
                    "x=" + x +
                    ", y=" + y +
                    '}';
        }
    }

    @Test
    public void testSerlization() {

        String value = "结果";
        String key = "key";
        template.opsForValue().set(key, value, 3L, TimeUnit.MINUTES);

        String res = ((String) template.opsForValue().get(key));

        logger.debug("获取的值，{}", res);

        Assert.assertEquals(value, res);


        RedisTest.TestPosition position = new TestPosition();
        position.setX(3223);
        position.setY(9900);


        template.opsForValue().set(key, position);

        logger.debug("复杂对象：{}", template.opsForValue().get(key));


    }
}
