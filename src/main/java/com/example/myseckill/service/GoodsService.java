package com.example.myseckill.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
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
    private static final int DEFAULT_MAX_RETRIES=100;
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

    //    通过id查询最新版本
    public long getVersion(long id) {
        return goodsDao1.getVersionById(id);
    }

    //    减库存，每次减1,使用乐观锁，不适合写多读少的场景，写冲突严重
    public boolean reduceStock(SkGoodsSeckill skGoodsSeckill) {
//        int count=0;
        int ret=0;

        while (ret == 0) {
            try {
                int version = goodsDao1.getVersionById(skGoodsSeckill.getGoodsId());
                System.out.println("====>>>version:"+version);
                skGoodsSeckill.setVersion(version);
                ret = goodsDao1.reduceStockByVersion(skGoodsSeckill);
            } catch (Exception e) {
                e.printStackTrace();
            }
//            count++;
        }
//        更新redis
        if (ret > 0) {
            long goodsId = skGoodsSeckill.getGoodsId();
            jedisService.set(GoodsKey.goodsStock, ""+goodsId, skGoodsSeckill.getStockCount());
        }
        return ret>0;
    }

    //    使用悲观锁减库存
    public boolean reduceStockByLock(SkGoodsSeckill skGoodsSeckill) {
        int count=0;
        int ret=0;
        long goodsId=skGoodsSeckill.getGoodsId();
        while (ret == 0) {
            if(count>5) {
                try {
//                    Thread.sleep(5000);
//                    System.out.println("线程睡眠...");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            try {
//                UpdateWrapper<SkGoodsSeckill> updateWrapper = new UpdateWrapper<>();
//                updateWrapper.eq("goods_id", skGoodsSeckill.getGoodsId());
//                updateWrapper.gt("stock_count", 0);
//                updateWrapper.setSql("stock_count = stock_count-1");
//                ret = goodsDao1.update(skGoodsSeckill, updateWrapper);
                ret = goodsDao1.reduceStockByGoodsId(goodsId);
                System.out.println("mysql update ret:"+ret);
            } catch (Exception e) {
                e.printStackTrace();
            }
            count++;
        }
//        更新redis
//        if (ret > 0) {
//            jedisService.set(GoodsKey.goodsStock, ""+goodsId, skGoodsSeckill.getStockCount());
//        }
        return ret>0;
    }
}
