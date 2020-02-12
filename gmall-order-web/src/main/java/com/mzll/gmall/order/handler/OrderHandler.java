package com.mzll.gmall.order.handler;


import com.alibaba.dubbo.config.annotation.Reference;
import com.mzll.gmall.annotation.LoginRequired;
import com.mzll.gmall.bean.OmsCartItem;
import com.mzll.gmall.bean.UmsMemberReceiveAddress;
import com.mzll.gmall.service.CartService;
import com.mzll.gmall.service.UmsMemberService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Controller
public class OrderHandler {


    @Reference
    CartService cartService;

    @Reference
    UmsMemberService umsMemberService;
    @LoginRequired(ifMust = true)
    @RequestMapping("toTrade")
    public String toTrade(HttpServletRequest request, ModelMap modelMap){

        String userId = (String) request.getAttribute("userId");

        // 根据userId查询购物车选中的商品
        List<OmsCartItem> omsCartItemList = cartService.getCartsByMemberId(userId);
        //通过userId获取其他信息
        List<UmsMemberReceiveAddress> umsMemberReceiveAddressList = new ArrayList<>();
        umsMemberReceiveAddressList = umsMemberService.getUmsMemberReceiveAddressByUserId(userId);


        modelMap.put("userAddressList",umsMemberReceiveAddressList);
        modelMap.put("",null);
        return "trade";
    }
}
