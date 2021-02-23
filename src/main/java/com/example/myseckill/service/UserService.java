package com.example.myseckill.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.myseckill.dao.SkUserDao1;
import com.example.myseckill.entity.SkUser;
import com.example.myseckill.exception.GlobalException;
import com.example.myseckill.redis.JedisService;
import com.example.myseckill.redis.UserKey;
import com.example.myseckill.result.CodeMsg;
import com.example.myseckill.utils.UUidUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@Service
public class UserService {
    @Autowired
    private SkUserDao1 skUserDao1;

    @Autowired
    private JedisService jedisService;

    public static final String COOKIE_NAME_TOKEN = "token";

    public SkUser getUserById(long id) {
        SkUser user = jedisService.get(UserKey.getById, "" + id, SkUser.class);
        if (user != null) {
            return user;
        }
        QueryWrapper<SkUser> skUserQueryWrapper = new QueryWrapper<>();
        skUserQueryWrapper.eq("id", id);
        user = skUserDao1.selectOne(skUserQueryWrapper);
        if (user != null) {
            jedisService.set(UserKey.getById, "" + id, user);
        }
        return user;
    }

    public void addCookie(HttpServletResponse response, String token, SkUser user) {
        jedisService.set(UserKey.token, token, user);
        Cookie cookie = new Cookie(COOKIE_NAME_TOKEN, token);
        cookie.setMaxAge(UserKey.token.expireSeconds());
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    //    根据token获取用户信息
    public SkUser getUserByToken(HttpServletResponse response, String token) {
        if (token == null || token.length() <= 0) {
            return null;
        }
        SkUser user = jedisService.get(UserKey.token, token, SkUser.class);
        if (user != null) {
            addCookie(response,token,user);
        }
        return user;
    }

    //    处理登录请求
    public String processLogin(HttpServletResponse response, String account, String password) {
        if (account == null||account.length()<=0) {
            throw new GlobalException(CodeMsg.MOBILE_EMPTY);
        } else if (password == null || password.length() <= 0) {
            throw new GlobalException(CodeMsg.PASSWORD_EMPTY);
        }
        SkUser user = getUserById(Long.parseLong(account));
        if (user == null) {
            throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);
        }
//        略过加密处理

        String password1 = user.getPassword();
        if (!password.equals(password1)) {
            throw new GlobalException(CodeMsg.PASSWORD_ERROR);
        }
        String token = UUidUtil.getUUid();
        addCookie(response,token,user);
        return token;
    }

//    更新密码

}
