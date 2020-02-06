package com.mzll.gmall.handler;


import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.mzll.gmall.bean.OmsCartItem;
import com.mzll.gmall.bean.PmsSkuInfo;
import com.mzll.gmall.service.CartService;
import com.mzll.gmall.service.PmsSkuInfoService;
import com.mzll.gmall.util.CookieUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class CartHandler {


    @Reference
    PmsSkuInfoService pmsSkuInfoService;

    @Reference
    CartService cartService;
    @RequestMapping("addToCart")
    public String addToCart(HttpServletRequest request, HttpServletResponse response, String skuId,BigDecimal quantity){

        // 根据skuId查询出商品
        PmsSkuInfo pmsSkuInfo = pmsSkuInfoService.getPmsSkuInfo(skuId);

        List<OmsCartItem> cartItemList = new ArrayList<>();
        // 先判断用户是否登录
        String userId = "1";// 测试数据


        OmsCartItem omsCartItem = new OmsCartItem();
        omsCartItem.setCreateDate(new Date());
        omsCartItem.setIsChecked("1");
        omsCartItem.setPrice(pmsSkuInfo.getPrice());
        omsCartItem.setProductName(pmsSkuInfo.getSkuName());
        omsCartItem.setProductPic(pmsSkuInfo.getSkuDefaultImg());
        omsCartItem.setQuantity(quantity);
        omsCartItem.setProductCategoryId(pmsSkuInfo.getCatalog3Id());
        omsCartItem.setProductId(pmsSkuInfo.getProductId());
        omsCartItem.setProductSkuId(skuId);

        if(StringUtils.isBlank(userId)){
            // 未登录 存cookie里
            String cartListCookieStr = CookieUtil.getCookieValue(request, "cartListCookie", true);
            // 判断cookie是否存在
            if (StringUtils.isNotBlank(cartListCookieStr)){
                // 存在 更新cookie
                // 判断添加的商品在购物车中是否存在
                cartItemList = JSON.parseArray(cartListCookieStr, OmsCartItem.class);
                // 判断是否为新的购物车
               boolean b = is_new_cart(cartItemList,omsCartItem);
               if(b){
                   // 新的购物车 添加
                   cartItemList.add(omsCartItem);
               }else {
                   // 旧的购物车  更新
                   for (OmsCartItem cartItem : cartItemList) {
                       if(cartItem.getProductSkuId().equals(omsCartItem.getProductSkuId())){
                           cartItem.setQuantity(cartItem.getQuantity().add(quantity));
                       }
                   }
               }
            }else {
                // 不存在 添加cookie
                cartItemList.add(omsCartItem);// 没有userId
            }
            // 同步缓存
            CookieUtil.setCookie(request,response,"cartListCookie",JSON.toJSONString(cartItemList),1000*60*60*2,true);
        }else{
            // 登录 存db里
            omsCartItem.setMemberId(userId);
            // 根据userId和skuId判断cart是否存在
           OmsCartItem omsCartItemFromUser = cartService.isCartExists(userId,skuId);

           if(omsCartItemFromUser!=null){
               // 老车 更新
                omsCartItemFromUser.setQuantity(omsCartItemFromUser.getQuantity().add(quantity));
                cartService.updateCart(omsCartItemFromUser);

           }else {

               // 新车添加
               cartService.addCart(omsCartItem);
           }

            // 同步缓存

        }
        return "redirect:/success.html";
    }

    private boolean is_new_cart(List<OmsCartItem> cartItems, OmsCartItem omsCartItem) {
        boolean b = true;

        for (OmsCartItem cartItem : cartItems) {
            if(cartItem.getProductSkuId().equals(omsCartItem.getProductSkuId())){
                b = false;
            }
        }
        return b;
    }
}
