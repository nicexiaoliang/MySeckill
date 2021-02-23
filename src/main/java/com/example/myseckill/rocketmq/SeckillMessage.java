package com.example.myseckill.rocketmq;

import com.example.myseckill.entity.SkUser;
import lombok.Data;

@Data
public class SeckillMessage {
    private SkUser user;
    private long goodsId;
}
