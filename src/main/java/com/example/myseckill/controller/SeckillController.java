package com.example.myseckill.controller;

import com.example.myseckill.entity.SkGoodsSeckill;
import com.example.myseckill.entity.SkOrderInfo;
import com.example.myseckill.entity.SkUser;
import com.example.myseckill.redis.GoodsKey;
import com.example.myseckill.redis.JedisService;
import com.example.myseckill.redis.SeckillKey;
import com.example.myseckill.result.CodeMsg;
import com.example.myseckill.rocketmq.SeckillMessage;
import com.example.myseckill.service.GoodsService;
import com.example.myseckill.service.OrderService;
import com.example.myseckill.service.SeckillService;
import com.google.common.util.concurrent.RateLimiter;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import redis.clients.jedis.JedisPool;

import java.io.UnsupportedEncodingException;
import java.util.List;

@Controller
public class SeckillController {
    @Autowired
    GoodsService goodsService;
    @Autowired
    OrderService orderService;
    @Autowired
    SeckillService seckillService;
    @Autowired
    JedisService jedisService;

    @Autowired
    DefaultMQProducer sender;

    int init_count=0;
    RateLimiter rateLimiter = RateLimiter.create(10);

    @RequestMapping("/seckill/goodsid/{goodsId}")
    public String doSeckill(@PathVariable("goodsId") long goodsId, SkUser user, Model model) throws UnsupportedEncodingException, InterruptedException, RemotingException, MQClientException, MQBrokerException {
        System.out.println("收到goodsId:"+goodsId);
        System.out.println("收到user:"+user);
//        初始化
        if (init_count == 0) {
            init();
            init_count++;
        }
//        限流

//        判断是否卖完了
        boolean over = seckillService.goodsIsOver(goodsId);
        if (over) {
//            卖完了
            model.addAttribute("msg", CodeMsg.SECKILL_OVER);
            return "error";
        }

//        在redis中判断是否重复秒杀
        Boolean aBoolean = jedisService.get(SeckillKey.wasBought, "" + user.getId() + "_" + goodsId, boolean.class);


        if (aBoolean != null) {
            System.out.println("aboolean:"+aBoolean);
            if (aBoolean) {
                model.addAttribute("msg", "重复秒杀1");
                return "error";
            }
        }

//        判断是否重复秒杀?????有问题？
        SkOrderInfo order = orderService.getOrderByUserIdGoodsId(user.getId(), goodsId);
        if (order != null) {
            model.addAttribute("msg", "重复秒杀2");
            return "error";
        }

//        redis预减库存
        Long stock = jedisService.decre(GoodsKey.goodsStock, goodsId + "");
        System.out.println("stock:"+stock);
        if (stock < 0) {
            seckillService.setGoodsOver(goodsId);
            model.addAttribute("msg", "秒杀结束了");
            return "error";
        }
        // 入队
        SeckillMessage seckillMessage = new SeckillMessage();
        seckillMessage.setUser(user);
        seckillMessage.setGoodsId(goodsId);
        String string = JedisService.beanToString(seckillMessage);
        Message message = new Message("shop1", "tag1", string.getBytes(RemotingHelper.DEFAULT_CHARSET));
        sender.send(message);
//        标记下单过了
        jedisService.set(SeckillKey.wasBought, "" + user.getId() + "_" + goodsId, true);
        return "hello";
    }

    //    初始化
    public void init() {
        List<SkGoodsSeckill> goodsList = goodsService.getGoodsList();
        if (goodsList == null) {
            return;
        }
        for (SkGoodsSeckill goods : goodsList) {
            jedisService.set(GoodsKey.goodsStock,""+goods.getGoodsId(),goods.getStockCount());
            jedisService.set(SeckillKey.isGoodOver, ""+goods.getGoodsId(),false);
        }
    }


}
