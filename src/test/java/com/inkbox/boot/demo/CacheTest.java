package com.inkbox.boot.demo;

import com.inkbox.boot.demo.dao.GoodsDao;
import com.inkbox.boot.demo.dos.GoodsDo;
import com.inkbox.boot.demo.service.GoodsService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;

import javax.transaction.Transactional;
import java.util.concurrent.CountDownLatch;

/**
 * @author 墨盒
 * @date 2019/10/24
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class CacheTest {

    @Autowired
    private GoodsService se;

    @Autowired
    private GoodsDao goodsDao;

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Test
    public void test() throws Exception {
        GoodsDo goodsDo = new GoodsDo();
        goodsDo.setCount(
                33
        );

        goodsDo = goodsDao.save(goodsDo);

        logger.debug("count={}", goodsDao.count());


        int count = 10;
        CountDownLatch countDownLatch = new CountDownLatch(count);
        for (int i = 0; i < count; i++) {
            GoodsDo finalGoodsDo = goodsDo;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        countDownLatch.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    se.getGoods(finalGoodsDo.getId());
                }
            }).start();
            countDownLatch.countDown();
        }

        Thread.currentThread().join();

    }
}
