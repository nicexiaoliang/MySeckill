package com.example.myseckill.redis;

public abstract class BasePrefix implements KeyPrefix{
    private int expireSecondes;
    private String prefix;

    public BasePrefix(String prefix,int expireSecondes) {
        this.expireSecondes=expireSecondes;
        this.prefix=prefix;
    }

    public BasePrefix(String prefix) {
        this(prefix, 0);
    }
    @Override
    public int expireSeconds() {
        return expireSecondes;
    }

    @Override
    public String getPrefix() {
        return prefix;
    }
}
