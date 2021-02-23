package com.example.myseckill.redis;

public class GoodsKey extends BasePrefix {
    private GoodsKey(String prefix, int expireSecondes) {
        super(prefix, expireSecondes);
    }

    public static GoodsKey goodsList = new GoodsKey("gl", 60);
    public static GoodsKey goodsDetails = new GoodsKey("gd", 60);
    public static GoodsKey goodsStock = new GoodsKey("gs", 0);
}
