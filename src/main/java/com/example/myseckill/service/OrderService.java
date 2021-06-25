package com.example.myseckill.service;

import com.example.myseckill.dao.OrderDao1;
import com.example.myseckill.entity.SkGoodsSeckill;
import com.example.myseckill.entity.SkOrderInfo;
import com.example.myseckill.entity.SkUser;
import com.example.myseckill.redis.JedisService;
import com.example.myseckill.redis.OrderKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class OrderService {
    @Autowired
    JedisService jedisService;
    @Autowired
    OrderDao1 orderDao1;

    //    创建订单
    public SkOrderInfo createOrder(SkUser user, SkGoodsSeckill skGoodsSeckill) {
        SkOrderInfo skOrderInfo = new SkOrderInfo();
        skOrderInfo.setUserId(user.getId());
        skOrderInfo.setGoodsId(skGoodsSeckill.getGoodsId());
        skOrderInfo.setGoodsCount(1);
        skOrderInfo.setDeliveryAddrId(1L);
        skOrderInfo.setCreateDate(new Date());
        skOrderInfo.setStatus(0);
        skOrderInfo.setOrderChannel(1);
        int ret = 0;
//      要循环保证插入成功？
        while (ret == 0) {
            ret=orderDao1.insert(skOrderInfo);
        }
//        更新redis
        jedisService.set(OrderKey.orderKeyPrefix, "" + user.getId() + "_" + skGoodsSeckill.getGoodsId(), skOrderInfo);
        return skOrderInfo;
    }

    public SkOrderInfo getOrderById(long id) {
        return orderDao1.getOrderById(id);
    }

    public SkOrderInfo getOrderByUserIdGoodsId(long userId, long goodsId) {
        SkOrderInfo skOrderInfo = jedisService.get(OrderKey.orderKeyPrefix, "" + userId + "_" + goodsId, SkOrderInfo.class);
        if(skOrderInfo!=null) return skOrderInfo;
        return orderDao1.getOrderByUserIdGoodsId(userId, goodsId);
    }


}
