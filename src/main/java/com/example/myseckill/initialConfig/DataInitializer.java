package com.example.myseckill.initialConfig;

import com.example.myseckill.entity.SkGoodsSeckill;
import com.example.myseckill.redis.GoodsKey;
import com.example.myseckill.redis.JedisService;
import com.example.myseckill.redis.SeckillKey;
import com.example.myseckill.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataInitializer implements ApplicationRunner {
    @Autowired
    GoodsService goodsService;
    @Autowired
    JedisService jedisService;
    @Override
    public void run(ApplicationArguments args) throws Exception {
        System.out.println("初始化......");
        List<SkGoodsSeckill> goodsList = goodsService.getGoodsList();
        if (goodsList == null) {
            return;
        }
        for (SkGoodsSeckill goods : goodsList) {
            jedisService.set(GoodsKey.goodsStock,""+goods.getGoodsId(),goods.getStockCount());
            jedisService.set(SeckillKey.isGoodOver, ""+goods.getGoodsId(),false);
        }
    }
}
