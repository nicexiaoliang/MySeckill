package com.example.myseckill.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.myseckill.dao.GoodsDao1;
import com.example.myseckill.entity.SkGoodsSeckill;
import com.example.myseckill.redis.GoodsKey;
import com.example.myseckill.redis.JedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

import java.util.List;

@Service
public class GoodsService {
    private static final int DEFAULT_MAX_RETRIES=5;
    @Autowired
    GoodsDao1 goodsDao1;
    @Autowired
    JedisService jedisService;

    //    查询商品列表
    public List<SkGoodsSeckill> getGoodsList() {
        QueryWrapper<SkGoodsSeckill> skGoodsQueryWrapper = new QueryWrapper<>();
        List<SkGoodsSeckill> skGoodsList = goodsDao1.selectList(skGoodsQueryWrapper);
        return skGoodsList;
    }

    //    通过id查询商品详情
    public SkGoodsSeckill getGoodsInfoById(long id) {
        QueryWrapper<SkGoodsSeckill> skGoodsQueryWrapper = new QueryWrapper<>();
        skGoodsQueryWrapper.eq("goods_id", Long.valueOf(id));
        SkGoodsSeckill skGoodsSeckill = goodsDao1.selectOne(skGoodsQueryWrapper);
        return skGoodsSeckill;
    }

    //    减库存，每次减1
    public boolean reduceStock(SkGoodsSeckill skGoodsSeckill) {
        int count=0;
        int ret=0;
        while (ret == 0&&count<DEFAULT_MAX_RETRIES) {
            try {
                ret = goodsDao1.reduceStockByVersion(skGoodsSeckill);
            } catch (Exception e) {
                e.printStackTrace();
            }
            count++;
        }
//        更新redis
        if (ret > 0) {
            long goodsId = skGoodsSeckill.getGoodsId();
            jedisService.set(GoodsKey.goodsStock, ""+goodsId, skGoodsSeckill.getStockCount());
        }
        return ret>0;
    }
}
