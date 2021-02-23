package com.example.myseckill.controller;

import com.example.myseckill.entity.SkGoodsSeckill;
import com.example.myseckill.entity.SkUser;
import com.example.myseckill.redis.GoodsKey;
import com.example.myseckill.redis.JedisService;
import com.example.myseckill.service.GoodsService;
import com.example.myseckill.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
public class LoginController {
    @Autowired
    UserService userService;
    @Autowired
    GoodsService goodsService;
    @Autowired
    JedisService jedisService;
    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    ThymeleafViewResolver thymeleafViewResolver;


    @GetMapping("/tologin")
    public String toLogin() {
        return "index";
    }

    @RequestMapping("/dologin")
    @ResponseBody
    public String doLogin(String id,String password, HttpServletRequest request,HttpServletResponse response, Model model) {
        System.out.println("id:"+id);
        System.out.println("password:"+password);
        String token = userService.processLogin(response, String.valueOf(id), password);

//        查询商品列表
        String html = jedisService.get(GoodsKey.goodsList, "", String.class);
        if (html != null&&html.length()>0) {
            return html;
        }

        List<SkGoodsSeckill> goodsList = goodsService.getGoodsList();
        model.addAttribute("goodslist", goodsList);
        model.addAttribute("user", userService.getUserById(Long.valueOf(id)));
//        手动渲染
//        SpringWebContext springWebContext = new SpringWebContext(request, response, request.getServletContext(), request.getLocale(),
//                model.asMap(), applicationContext);
//        html = thymeleafViewResolver.getTemplateEngine().process("goodlist", springWebContext);
//spring5渲染方式
        WebContext webContext = new WebContext(request, response, request.getServletContext(), request.getLocale(), model.asMap());
        html = thymeleafViewResolver.getTemplateEngine().process("goodlist", webContext);
        if (html != null && html.length() > 0) {
            jedisService.set(GoodsKey.goodsList, "", html);
        }
        return html;
    }
}
