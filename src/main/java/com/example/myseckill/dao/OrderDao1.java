package com.example.myseckill.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.myseckill.entity.SkOrderInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface OrderDao1 extends BaseMapper<SkOrderInfo> {
    //    通过userId和goodsId查询订单
    @Select("select * from sk_order_info where user_id=#{uid} and goods_id=#{gid}")
    public SkOrderInfo getOrderByUserIdGoodsId(@Param("uid") long userId, @Param("gid") long goodsId);

    @Select("select * from sk_order_info where id = #{id}")
    public SkOrderInfo getOrderById(@Param("id") long id);
}
