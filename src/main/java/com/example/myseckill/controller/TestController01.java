package com.example.myseckill.controller;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class TestController01 {
    @Autowired
    DefaultMQProducer defaultMQProducer;
    @Autowired
    DefaultMQPushConsumer consumer;
    @RequestMapping("/hello")
    public String getHello() {
        return "hello";
    }

    //    测试rocketmq
    @RequestMapping("/toProduce")
    public String toProduce() throws Exception{
        System.out.println("对象："+defaultMQProducer);
        System.out.println("消费者对象："+consumer);
        for(int i=0;i<10;i++){
            Message message = new Message("shop","tag1", ("hello,rocketmq" + i).getBytes(RemotingHelper.DEFAULT_CHARSET));
            SendResult result = defaultMQProducer.send(message);
            System.out.println(result);
        }
        System.out.println("生成者发送消息");
        return "hello";
    }






}
