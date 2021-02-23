package com.example.myseckill.redis;

public class UserKey extends BasePrefix {
//    过期时间，默认两天
    public static final int TOKEN_EXPIRE=3600*24*2;

    private UserKey(String prefix, int expireSecondes) {
        super(prefix, expireSecondes);
    }

    public static UserKey token = new UserKey("token", TOKEN_EXPIRE);
    public static UserKey getById=new UserKey("id",0);


}
