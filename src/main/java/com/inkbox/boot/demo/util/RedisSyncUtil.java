package com.inkbox.boot.demo.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
public class RedisSyncUtil implements SyncLock {
    @Autowired
    private StringRedisTemplate template;

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public boolean lock(String key, String value) {


        Boolean res = template.opsForValue().setIfAbsent(key, value, 200L, TimeUnit.MILLISECONDS);
        if (res == null) return false;
        return res;
    }

    @Override
    public String lock(String key) {
        return lock(key, 200L);
    }

    @Override
    public String lock(String key, long time) {
        String value = UUID.randomUUID()
                .toString();
        while (true) {
            Boolean res = template.opsForValue().setIfAbsent(key, value, time, TimeUnit.MILLISECONDS);
            if (Boolean.TRUE == res) {
                return value;
            }
        }
    }

    @Override
    public boolean lock(String key, String value, long time) {

        int count = 1;
        long start = System.currentTimeMillis();
        while (!lock(key, value)) {
//            logger.debug("重复获取锁,{}-{}", ++count,System.nanoTime());
            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (System.currentTimeMillis() - start >= time) {
                logger.warn("获取锁{}超时", key);
                //获取锁超时
                return false;
            }
        }
        return true;
    }


    @Override
    public boolean unlock(String key, String value) {

        DefaultRedisScript<Long> script = new DefaultRedisScript<>();
        script.setResultType(Long.class);
        script.setScriptText("if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end");
        Long res = template.execute(script, Arrays.asList(key), value);
        if (res == null) return false;
        return res == 1L;
    }
}
