package com.mzll.gmall.listeners;


import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.mzll.gmall.bean.OmsCartItem;
import com.mzll.gmall.service.CartService;
import com.mzll.gmall.util.CookieUtil;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@Component
public class CartListener {

    @Reference
    CartService cartService;

    @JmsListener(containerFactory = "jmsQueueListener",destination = "LOGIN_SUCCESS")
    public void cartListener(MapMessage message, HttpServletRequest request, HttpServletResponse response) throws JMSException {
        String userId = message.getString("userId");
        String nickname = message.getString("nickname");

        // 同步购物车
        // 取出cookie里的数据
        String cartListCookieStr = CookieUtil.getCookieValue(request, "cartListCookie", true);
        List<OmsCartItem> omsCartItemList = new ArrayList<>();
        omsCartItemList = JSON.parseArray(cartListCookieStr, OmsCartItem.class);
        // 存入db里
        for (OmsCartItem cartItem : omsCartItemList) {

            cartService.addCart(cartItem);
            String cartKeyInCache = "user:" + cartItem.getMemberId() + ":carts";
            // 把缓存中的“key”存进cookie
            CookieUtil.setCookie(request,response,"cartKeyInCache",cartKeyInCache,60*60*24,true);

        }
    }
}
