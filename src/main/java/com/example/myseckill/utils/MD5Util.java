package com.example.myseckill.utils;

import org.springframework.util.DigestUtils;

public class MD5Util {
    public static String md5(String string) {
        return DigestUtils.md5DigestAsHex(string.getBytes());
    }

    public static void main(String[] args) {
        System.out.println(MD5Util.md5("fengzongliang"));
    }
}
