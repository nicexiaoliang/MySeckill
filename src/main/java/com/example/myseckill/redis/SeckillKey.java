package com.example.myseckill.redis;

public class SeckillKey extends BasePrefix {
    private SeckillKey(String prefix) {
        super(prefix);
    }

//    表示商品是否已经卖完了
    public static SeckillKey isGoodOver = new SeckillKey("go");

    //    表示用户是否已经买过这件商品
    public static SeckillKey wasBought = new SeckillKey("bought");


}
