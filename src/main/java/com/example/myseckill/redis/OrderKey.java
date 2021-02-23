package com.example.myseckill.redis;

public class OrderKey extends BasePrefix {
    private OrderKey(String prefix, int expireSecondes) {
        super(prefix, expireSecondes);
    }

    public static OrderKey orderKeyPrefix=new OrderKey("order",0);
}
