package com.example.myseckill.controller;

import com.example.myseckill.entity.SkGoodsSeckill;
import com.example.myseckill.entity.SkOrderInfo;
import com.example.myseckill.entity.SkUser;
import com.example.myseckill.redis.GoodsKey;
import com.example.myseckill.redis.JedisService;
import com.example.myseckill.redis.SeckillKey;
import com.example.myseckill.result.CodeMsg;
import com.example.myseckill.result.Result;
import com.example.myseckill.rocketmq.SeckillMessage;
import com.example.myseckill.service.GoodsService;
import com.example.myseckill.service.OrderService;
import com.example.myseckill.service.SeckillService;
import com.google.common.util.concurrent.RateLimiter;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import redis.clients.jedis.JedisPool;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

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

    volatile int init_count=0;

    RateLimiter rateLimiter = RateLimiter.create(10);

    @RequestMapping("/seckill/goodsid/{goodsId}")
//    @ResponseBody
    public String doSeckill(@PathVariable("goodsId") long goodsId, SkUser user, Model model) throws UnsupportedEncodingException, InterruptedException, RemotingException, MQClientException, MQBrokerException {
//        System.out.println("收到goodsId:"+goodsId);
//        System.out.println("收到user:"+user);
        if (!rateLimiter.tryAcquire(1000, TimeUnit.MILLISECONDS)) {
            return "error";
        }
//        初始化
        if (init_count == 0) {
            init();
            init_count=1;
        }
//        限流
        if (!rateLimiter.tryAcquire()) {
            return "error";
        }
//        判断是否卖完了
        boolean over = seckillService.goodsIsOver(goodsId);
        if (over) {
//            卖完了
            model.addAttribute("msg", CodeMsg.SECKILL_OVER);
            return "error";
        }

//        在redis中判断是否重复秒杀.
        Boolean wasBought = jedisService.get(SeckillKey.wasBought, "" + user.getId() + "_" + goodsId, boolean.class);

        if (wasBought != null) {
            System.out.println("aboolean:"+wasBought);
            if (wasBought) {
                model.addAttribute("msg", "redis中检测到重复秒杀");
                return "error";
            }
        }

//        判断是否重复秒杀?????不必要？
//        如果redis崩溃了，这里可以派上用场.
        SkOrderInfo order = orderService.getOrderByUserIdGoodsId(user.getId(), goodsId);
        if (order != null) {
            model.addAttribute("msg", "mysql中检测到重复秒杀");
            return "error";
        }

//        redis预减库存,redis是单线程处理
//
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
        SendResult sendResult = sender.send(message);
//        System.out.println(send.getSendStatus());
//        logger.info(send.getSendStatus().toString());
//        send_count++;
//        System.out.println("===============>消息发送总数："+send_count);
//        标记下单过了
        jedisService.set(SeckillKey.wasBought, "" + user.getId() + "_" + goodsId, true);
        //返回hello页面说明成功了
        return "hello";
    }

    //    初始化
    public void init() {
        System.out.println("初始化。。。。。");
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
