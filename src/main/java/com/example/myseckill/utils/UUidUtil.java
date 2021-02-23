package com.example.myseckill.utils;

import java.util.UUID;

public class UUidUtil {
    public static String getUUid() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
