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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
public class GoodsController {
    @Autowired
    JedisService jedisService;
    @Autowired
    GoodsService goodsService;
    @Autowired
    UserService userService;
    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    ThymeleafViewResolver thymeleafViewResolver;

    public int count=0;

//    查询商品列表
    @RequestMapping("/goodslist")
    @ResponseBody
    public String getGoodsList(HttpServletRequest request, HttpServletResponse response
            , Model model) {
//        Cookie[] cookies = request.getCookies();
//        for (Cookie cookie : cookies) {
//            System.out.println("cookie:"+cookie.getName());
//            System.out.println("cookievalue:"+cookie.getValue());
//        }
        String html = jedisService.get(GoodsKey.goodsList, "", String.class);
        if (html != null&&html.length()>0) {
            return html;
        }
        List<SkGoodsSeckill> goodsList = goodsService.getGoodsList();
        model.addAttribute("goodslist", goodsList);
        WebContext webContext = new WebContext(request, response, request.getServletContext(), request.getLocale(), model.asMap());
        html = thymeleafViewResolver.getTemplateEngine().process("goodlist", webContext);
        if (html != null && html.length() > 0) {
            jedisService.set(GoodsKey.goodsList, "", html);
        }
        return html;
    }

//    查询某个商品详情

    @RequestMapping("/goods_detail/goodsId/{goodsId}")
    @ResponseBody
    public String getGoodsById(Model model, HttpServletRequest request,
                               HttpServletResponse response, @PathVariable("goodsId") long goodsId, SkUser user) {
        System.out.println("goodsId:"+goodsId);
        count++;
        System.out.println("count:"+count);
        System.out.println("user:"+user);
        String html = jedisService.get(GoodsKey.goodsDetails, "" + goodsId, String.class);
        if (html != null&&html.length()>0) {
            System.out.println("取缓存");
            return html;
        }
        System.out.println("从数据库取数据");
        SkGoodsSeckill goodsInfoById = goodsService.getGoodsInfoById(goodsId);
        model.addAttribute("goods", goodsInfoById);
        model.addAttribute("goodsId", goodsId);
//        判断状态
        long start = goodsInfoById.getStartDate().getTime();
        long end = goodsInfoById.getEndDate().getTime();
        long now = System.currentTimeMillis();
        int seckillStatus=0;
        if (now < start) {
            seckillStatus=0;
        } else if (now > end) {
            seckillStatus = 2;
        } else {
            seckillStatus=1;
        }
        model.addAttribute("seckillStatus", seckillStatus);
        WebContext webContext = new WebContext(request, response, request.getServletContext(), request.getLocale(), model.asMap());
        html = thymeleafViewResolver.getTemplateEngine().process("good_details", webContext);
        if (html != null && html.length() > 0) {
            jedisService.set(GoodsKey.goodsDetails, ""+goodsId, html);
        }
        return html;
    }


}
