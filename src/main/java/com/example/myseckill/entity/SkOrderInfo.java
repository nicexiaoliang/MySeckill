package com.example.myseckill.entity;
import lombok.Data;

import java.util.Date;
@Data
public class SkOrderInfo {

  private long id;
  private long userId;
  private long goodsId;
  private long deliveryAddrId;
  private int goodsCount;
  private int orderChannel;
  private int status;
  private Date createDate;
  private Date payDate;

}
