package com.example.myseckill.redis;

import com.alibaba.fastjson.JSON;
import com.example.myseckill.entity.SkUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Service
public class JedisService {
    @Autowired
    public JedisPool jedisPool;

    //    存储
    public <T> boolean set(KeyPrefix keyPrefix, String key, T value) {
        Jedis jedis=null;
        try {
            jedis = jedisPool.getResource();
            String str = beanToString(value);
            if (str == null || str.length() <= 0) {
                return false;
            }
            String realKey=keyPrefix.getPrefix()+key;
            int expireSeconds=keyPrefix.expireSeconds();
            if (expireSeconds <= 0) {
                jedis.set(realKey,str);
            }else {
                jedis.setex(realKey,expireSeconds,str);
            }
            return true;
        }finally {
            returnPool(jedis);
        }

    }
//获取数据
    public <T> T get(KeyPrefix keyPrefix, String key, Class<T> tClass) {
        Jedis jedis=null;
        try {
            jedis = jedisPool.getResource();
            String realKey=keyPrefix.getPrefix()+key;
            String s = jedis.get(realKey);
            if (s == null || s.length() <= 0) {
                return null;
            }
            T t = stringToBean(s,tClass);
            return t;
        }finally {
            if (jedis != null) {
                returnPool(jedis);
            }
        }
    }

    //    删除key
    public boolean delete(KeyPrefix keyPrefix, String key) {
        Jedis jedis=null;
        try {
            jedis=jedisPool.getResource();
            String realKey=keyPrefix.getPrefix()+key;
            Long del = jedis.del(realKey);
            return del>0;
        }finally {
            returnPool(jedis);
        }
    }
//判断key存在
    public boolean keyIsExists(KeyPrefix keyPrefix, String key) {
        Jedis jedis=null;
        try {
            jedis=jedisPool.getResource();
            String realKey=keyPrefix.getPrefix()+key;
            Boolean exists = jedis.exists(realKey);
            return exists;
        }finally {
            returnPool(jedis);
        }
    }

//    增加值
    public Long incre(KeyPrefix keyPrefix,String key){
        Jedis jedis=null;
        try {
            jedis=jedisPool.getResource();
            String realKey=keyPrefix.getPrefix()+key;
            Long incr = jedis.incr(realKey);
            return incr;
        }finally {
            returnPool(jedis);
        }
    }

    //    减1
    public Long decre(KeyPrefix keyPrefix, String key) {
        Jedis jedis=null;
        try {
            jedis=jedisPool.getResource();
            String realKey=keyPrefix.getPrefix()+key;
            Long decr = jedis.decr(realKey);
            return  decr;
        }finally {
            returnPool(jedis);
        }
    }

    //    定义一些工具方法
    public static <T> T stringToBean(String str, Class<T> tClass) {
        if (str == null || str.length() <= 0 || tClass == null) {
            return null;
        } else if (tClass == int.class || tClass == Integer.class) {
            return (T) Integer.valueOf(str);
        } else if (tClass == long.class || tClass == Long.class) {
            return (T) Long.valueOf(str);
        } else if (tClass == double.class || tClass == Double.class) {
            return (T) Double.valueOf(str);
        } else if (tClass == String.class) {
            return (T) str;
        } else if (tClass == boolean.class) {
            return (T) Boolean.valueOf(str);
        } else {
            return JSON.toJavaObject(JSON.parseObject(str), tClass);
        }
    }

    public static <T> String beanToString(T value) {
        if (value == null) {
            return null;
        }
        Class<?> clazz = value.getClass();
        if (clazz == int.class || clazz == Integer.class) {
            return String.valueOf(value);
        } else if (clazz == long.class || clazz == Long.class) {
            return String.valueOf(value);
        } else if (clazz == String.class) {
            return (String) value;
        } else if (clazz==boolean.class) {
            return String.valueOf(value);
        } else {
            return JSON.toJSONString(value);
        }

    }

    public void returnPool(Jedis jedis) {
        if (jedis != null) {
            jedis.close();
        }
    }


}
