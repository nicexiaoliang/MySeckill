package com.example.myseckill;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.myseckill.dao.GoodsDao1;
import com.example.myseckill.dao.SkUserDao1;
import com.example.myseckill.entity.SkGoodsSeckill;
import com.example.myseckill.entity.SkOrderInfo;
import com.example.myseckill.entity.SkUser;
import com.example.myseckill.redis.JedisService;
import com.example.myseckill.service.GoodsService;
import com.example.myseckill.service.OrderService;
import com.example.myseckill.service.SeckillService;
import com.example.myseckill.service.UserService;
import lombok.SneakyThrows;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.sql.SQLException;
import java.util.List;

@SpringBootTest
class MySeckillApplicationTests {
    @Autowired
    public DruidDataSource druidDataSource;
    @Autowired
    public SkUserDao1 skUserDao1;
    @Autowired
    public JedisService jedisService;
    @Autowired
    public DefaultMQPushConsumer defaultMQPushConsumer;
    @Autowired
    public DefaultMQProducer defaultMQProducer;
    @Autowired
    OrderService orderService;

    @Autowired
    GoodsService goodsService;
    @Autowired
    UserService userService;
    @Autowired
    SeckillService seckillService;
//    redis池
    @Autowired
    public JedisPool jedisPool;
    @Autowired
    public GoodsDao1 goodsDao1;

    @Test
    void contextLoads() throws SQLException {
        DruidPooledConnection connection = druidDataSource.getConnection();
        System.out.println(connection);
    }
@Test
    public void testDao() {
    System.out.println(skUserDao1);
    List<SkUser> skUsers = skUserDao1.selectList(new QueryWrapper<>());
    for (SkUser user : skUsers) {
        System.out.println(user.getNickname());
    }

}
@Test
    public void testJedis() {
    Jedis jedis = jedisPool.getResource();
    Long count = jedis.decr("count");
//    返回的是自减后的结果
    System.out.println("===>"+count);
}
@Test
    public void testConsume() throws MQClientException {


    }
@Test
    public void testGetOrder() {
    SkOrderInfo orderByUserIdGoodsId = orderService.getOrderByUserIdGoodsId(13232192824L, 1);
    System.out.println(orderByUserIdGoodsId);
}
@Test
    public void testReduce() {
    int i = goodsDao1.reduceTest(5L);
//    返回的是更新条数
    System.out.println("====>"+i);
}
@Test
    public void testWrite() {
        SkGoodsSeckill goods = goodsService.getGoodsInfoById(3);
        int i = goodsDao1.reduceStockByVersion(goods);
        System.out.println("=====>"+i);
    }




}
