package com.example.myseckill.redis;

public interface KeyPrefix {
//    返回过期秒数
    public int expireSeconds();

    //    返回前缀名称
    public String getPrefix();

}
