package com.example.myseckill.entity;
import lombok.Data;

import java.util.Date;
@Data
public class SkGoodsSeckill {
  private long goodsId;
  private String goodsName;
  private String goodsTitle;
  private String goodsImg;
  private String goodsDetail;
  private double seckillPrice;
  private int stockCount;
  private Date startDate;
  private Date endDate;
  private int version;


}
