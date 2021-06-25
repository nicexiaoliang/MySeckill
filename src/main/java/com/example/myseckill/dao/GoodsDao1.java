package com.example.myseckill.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.myseckill.entity.SkGoodsSeckill;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface GoodsDao1 extends BaseMapper<SkGoodsSeckill> {

    //    减库存
    @Update("update sk_goods_seckill set stock_count=stock_count-1,version=version+1" +
            " where goods_id=#{goodsId} and stock_count>0 and version=#{version}")
    public int reduceStockByVersion(SkGoodsSeckill skGoodsSeckill);

    @Update("update sk_goods_seckill set stock_count=stock_count-1 where goods_id = #{id}")
    public int reduceTest(@Param("id") long id);

    @Select("select version from sk_goods_seckill where goods_id = #{id}")
    public int getVersionById(@Param("id") long id);

    @Update("update sk_goods_seckill set stock_count=stock_count - 1 where goods_id = #{id} and stock_count>0")
    public int reduceStockByGoodsId(@Param("id") long goodsId);
}
