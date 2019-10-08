package com.inkbox.boot.demo;

import com.inkbox.boot.demo.dao.GoodsDao;
import com.inkbox.boot.demo.dao.UserDao;
import com.inkbox.boot.demo.dos.GoodsDo;
import com.inkbox.boot.demo.util.RedisSyncUtil;
import com.inkbox.boot.demo.util.SyncLock;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.test.context.junit4.SpringRunner;

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DemoApplicationTests {
    @Autowired
    private UserDao dao;
    @Autowired(required = false)
    private StringRedisTemplate template;
    private Logger logger = LoggerFactory.getLogger(getClass());

    //    Redisson
    @Test
    public void contextLoads() {
        logger.debug("数据={}", dao.findAll());


        logger.debug("指定数据={}", dao.findById(1L));

        Assert.assertNotNull("没有reids操作实例", template);


        logger.debug("{}", dao.findAllByName("inkbox"));
    }

    @Test
    public void testSync() {

        logger.debug("加锁的结果=:{}", template.opsForValue().setIfAbsent("sync", UUID.randomUUID().toString(), 200L, TimeUnit.MILLISECONDS));

        DefaultRedisScript<Long> script = new DefaultRedisScript<>();
        script.setResultType(Long.class);
        script.setScriptText("if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end");
        logger.debug("执行lua的结果:{}", template.execute(script, Arrays.asList("sync"), "2322"));

    }

    @Autowired
    private RedisSyncUtil syncUtil;

    @Test
    public void testThreadSync() {


        Runnable runnable = new Runnable() {
            @Override
            public void run() {

                logger.debug("开始获取锁");
                String key = "sync";
                String value = "111111";
                long time = 200L;

                //开始获取锁
                if (syncUtil.lock(key, value, time)) {

                    logger.debug("拿到锁{}", System.nanoTime());
                    //拿到锁
//                    try {
//                        Thread.sleep(3);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }

                    syncUtil.unlock(key, value);
                    logger.debug("解锁,{}", System.nanoTime());

                } else {
                    logger.debug("获取锁失败");
                }


            }
        };

        new Thread(runnable).start();
        new Thread(runnable).start();

        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        ExecutorService executorService = Executors.newFixedThreadPool(3);
//        Future<?> submit = executorService.submit(new Runnable() {
//            @Override
//            public void run() {
//
//                logger.debug("开始获取锁");
//                if (syncUtil.lock("sync", "12334")) {
//                    //拿到锁
//                    logger.debug("拿到锁");
//                }else {
//                    logger.debug("获取锁失败");
//                }
//
//
//            }
//        });

//        try {
//            submit.get();
//            submit.get();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        } catch (ExecutionException e) {
//            e.printStackTrace();
//        }


        //第一个线程去获取锁
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//
//                if (syncUtil.lock("sync","12334")){
//                    //拿到锁
//                    logger.debug("拿到锁");
//                }
//
//
//            }
//        }).start();


    }

    @Autowired
    private GoodsDao goodsDao;

    @Test
    public void add() {


        GoodsDo goodsDo = new GoodsDo();
        goodsDo.setCount(10);

        goodsDao.save(goodsDo);

    }


    @Test
    public void test() throws Exception {

        logger.debug("获取expire={}", template.getExpire("3333"));

        CountDownLatch latch = new CountDownLatch(1);
        int count = 10;
        for (int i = 0; i < count; i++) {
            new Thread(new KillRun(latch, syncUtil, goodsDao)).start();
        }

        latch.countDown();
        Thread.sleep(6000L);
    }


    class KillRun implements Runnable {

        private CountDownLatch latch;
        private SyncLock lock;

        private GoodsDao dao;

        public KillRun(CountDownLatch latch, SyncLock lock, GoodsDao goodsDao) {
            this.lock = lock;
            this.dao = goodsDao;
            this.latch = latch;
        }

        private Logger logger = LoggerFactory.getLogger(getClass());

        @Override
        public void run() {

            try {
                latch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            logger.debug("开始获取锁");
            String value = lock.lock("22233");
            logger.debug("锁拿到，开始-1");
            GoodsDo goods = dao.findById(7L).get();
            if (goods.getCount() != 0) {
                logger.debug("获取的数据={},{}", goods.getClass(), goods);

                goods.setCount(goods.getCount() - 1);


                dao.save(goods);
            } else {
                logger.debug("count已为零");
            }
            lock.unlock("22233", value);


        }
    }
//    @Test
//    public void testRedisson() {
//        RedissonClient redisson = Redisson.create();
//
//        redisson.getLock("ddd").lock();
//
//    }
}
