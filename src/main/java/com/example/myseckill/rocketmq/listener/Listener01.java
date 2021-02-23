package com.example.myseckill.rocketmq.listener;

import com.example.myseckill.entity.SkGoodsSeckill;
import com.example.myseckill.entity.SkOrderInfo;
import com.example.myseckill.entity.SkUser;
import com.example.myseckill.redis.JedisService;
import com.example.myseckill.result.CodeMsg;
import com.example.myseckill.rocketmq.SeckillMessage;
import com.example.myseckill.service.GoodsService;
import com.example.myseckill.service.OrderService;
import com.example.myseckill.service.SeckillService;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.List;
@Service
public class Listener01 implements MessageListenerConcurrently {
    @Autowired
    OrderService orderService;
    @Autowired
    SeckillService seckillService;
    @Autowired
    GoodsService goodsService;
    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
        System.out.println("消费消息==>");
        for (MessageExt msg : list) {
            System.out.println("body:"+msg.getBody());
            byte[] bytes=msg.getBody();
            String message = new String(bytes);

            SeckillMessage seckillMessage = JedisService.stringToBean(message, SeckillMessage.class);
            SkUser user = seckillMessage.getUser();
            long goodsId = seckillMessage.getGoodsId();
            System.out.println("===>消费消息goodsId："+goodsId);
            System.out.println("=====>userid:"+user.getId());
            System.out.println("orderservice====>"+orderService);
//            判断是否重复下单
            SkOrderInfo order = orderService.getOrderByUserIdGoodsId(user.getId(), goodsId);
            if (order != null) {
                System.out.println("不等于null");
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
            System.out.println("=====^^^^^^^^^++++++++");
            SkGoodsSeckill goods = goodsService.getGoodsInfoById(goodsId);
//            写入数据库
            System.out.println("======>写入数据库");
            seckillService.seckill(user, goods);
        }
        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
    }
}
