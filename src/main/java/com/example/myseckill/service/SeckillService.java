package com.example.myseckill.service;

import com.example.myseckill.entity.SkGoodsSeckill;
import com.example.myseckill.entity.SkOrderInfo;
import com.example.myseckill.entity.SkUser;
import com.example.myseckill.redis.GoodsKey;
import com.example.myseckill.redis.JedisService;
import com.example.myseckill.redis.OrderKey;
import com.example.myseckill.redis.SeckillKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SeckillService {
    @Autowired
    JedisService jedisService;
    @Autowired
    GoodsService goodsService;
    @Autowired
    OrderService orderService;

    //    当商品卖完了，就在redis中保存卖完了的商品
    public void setGoodsOver(long goodsId) {
        jedisService.set(SeckillKey.isGoodOver, "" + goodsId, true);
    }

    public boolean goodsIsOver(long goodsId) {
//        boolean ret = jedisService.keyIsExists(SeckillKey.isGoodOver, "" + goodsId);
//        return ret;
        Boolean aBoolean = jedisService.get(SeckillKey.isGoodOver, "" + goodsId, boolean.class);
        return aBoolean;
    }

    //    下单，保证事务
    @Transactional
    public SkOrderInfo seckill(SkUser user, SkGoodsSeckill skGoodsSeckill) {
//        使用悲观锁，超过5次重试失败之后，线程睡眠5秒后重试
        boolean success = goodsService.reduceStockByLock(skGoodsSeckill);
        if (success) {
            return orderService.createOrder(user, skGoodsSeckill);
        } else {
            setGoodsOver(skGoodsSeckill.getGoodsId());
            return null;
        }
    }

    //    获取秒杀结果，返回订单id
    public long getSeckillResult(long userId,long goodsId) {
        SkOrderInfo info = orderService.getOrderByUserIdGoodsId(userId, goodsId);
        if(info!=null) return info.getId();
        else {
            boolean over = goodsIsOver(goodsId);
            if(over) return -1;
            else return 0;
        }
    }
}
