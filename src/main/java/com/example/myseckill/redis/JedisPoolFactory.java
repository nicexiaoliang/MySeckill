package com.example.myseckill.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Service
public class JedisPoolFactory {
    @Autowired
    public RedisConfig redisConfig;
@Bean
    public JedisPool jedisPool() {
    JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
    jedisPoolConfig.setMaxTotal(redisConfig.getMaxActive());
    jedisPoolConfig.setMaxIdle(redisConfig.getMaxIdle());
    jedisPoolConfig.setMaxWaitMillis(redisConfig.getMaxWait().toMillis());
    System.out.println("host:"+redisConfig.getHost());
    JedisPool jedisPool = new JedisPool(jedisPoolConfig, redisConfig.getHost());

    return jedisPool;
}
}
