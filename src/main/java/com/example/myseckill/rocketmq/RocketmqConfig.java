package com.example.myseckill.rocketmq;

import com.example.myseckill.rocketmq.listener.Listener01;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RocketmqConfig {
    @Value("${rocketmq.namesrv.addr}")
    private String host;
    @Value("${rocketmq.namesrv.port}")
    private String port;
    @Autowired
    Listener01 listener01;

@Bean
    public DefaultMQProducer defaultMQProducer() throws MQClientException {
        DefaultMQProducer producer = new DefaultMQProducer("pgroup05272328");
        producer.setNamesrvAddr(host+":"+port);
        producer.setSendMsgTimeout(8000);
        producer.start();
        return producer;
    }
@Bean
    public DefaultMQPushConsumer defaultMQPushConsumer() throws Exception{
        DefaultMQPushConsumer defaultMQPushConsumer = new DefaultMQPushConsumer("cgroup05272328");
        defaultMQPushConsumer.setNamesrvAddr(host+":"+port);
        defaultMQPushConsumer.setMessageModel(MessageModel.CLUSTERING);
        defaultMQPushConsumer.subscribe("shop6","*");
//        defaultMQPushConsumer.setMessageListener(new Listener01());
    System.out.println("listener:=========>>>>"+listener01);
        defaultMQPushConsumer.setMessageListener(listener01);
        defaultMQPushConsumer.start();
        return defaultMQPushConsumer;
    }


}
