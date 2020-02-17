package com.mzll.gmall.order.handler;


import com.alibaba.dubbo.config.annotation.Reference;
import com.mzll.gmall.annotation.LoginRequired;
import com.mzll.gmall.bean.OmsCartItem;
import com.mzll.gmall.bean.OmsOrder;
import com.mzll.gmall.bean.OmsOrderItem;
import com.mzll.gmall.bean.UmsMemberReceiveAddress;
import com.mzll.gmall.service.CartService;
import com.mzll.gmall.service.OrderService;
import com.mzll.gmall.service.UmsMemberService;
import com.mzll.gmall.util.CookieUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
public class OrderHandler {


    @Reference
    CartService cartService;

    @Reference
    UmsMemberService umsMemberService;

    @Reference
    OrderService orderService;


    @LoginRequired(ifMust = true)
    @RequestMapping("submitOrder")
    public String submitOrder(String tradeCode, String addressId, HttpServletRequest request , HttpServletResponse response, ModelMap modelMap){

        String userId = (String) request.getAttribute("userId");
        String nickname = (String) request.getAttribute("nickname");

        // 缓存中取出tradeCode
        String tradeCodeFromCache = orderService.getTradeCodeByUserId(userId);



        if (tradeCode.equals(tradeCodeFromCache)){
            // 根据addressId获取收货信息
            UmsMemberReceiveAddress umsMemberReceiveAddress = orderService.getReceiveAddressesById(addressId);
            // 获取商品信息
            List<OmsCartItem> cartItems = cartService.getCartsByMemberId(userId);

            // 生成订单信息
            OmsOrder omsOrder = new OmsOrder();
            // 生成订单号
            Date date = new Date();
            SimpleDateFormat yyyyMMddHHmmss = new SimpleDateFormat("yyyyMMddHHmmss");
            String dateStr = yyyyMMddHHmmss.format(date);

            String orderNum = "mzll"+System.currentTimeMillis()+dateStr;
            omsOrder.setOrderSn(orderNum);// 外部订单号，out_trade_no，其他系统使用的不重复的订单号
            omsOrder.setStatus("0");
            omsOrder.setNote("硅谷订单");
            omsOrder.setSourceType(0);
            omsOrder.setReceiverCity(umsMemberReceiveAddress.getCity());
            omsOrder.setReceiverDetailAddress(umsMemberReceiveAddress.getDetailAddress());
            omsOrder.setReceiverName(umsMemberReceiveAddress.getName());
            omsOrder.setReceiverPhone(umsMemberReceiveAddress.getPhoneNumber());
            omsOrder.setReceiverPostCode(umsMemberReceiveAddress.getPostCode());
            omsOrder.setReceiverProvince(umsMemberReceiveAddress.getProvince());
            omsOrder.setReceiverRegion(umsMemberReceiveAddress.getRegion());
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DATE,3);
            Date time = calendar.getTime();
            omsOrder.setReceiveTime(time);// 预计送达时间
            omsOrder.setCreateTime(new Date());
            omsOrder.setMemberUsername(nickname);
            omsOrder.setPayAmount(getSum(cartItems));
            omsOrder.setMemberId(userId);
            omsOrder.setTotalAmount(getSum(cartItems));

            //
            List<OmsOrderItem> omsOrderItems = new ArrayList<>();
            for (OmsCartItem omsCartItem : cartItems) {
                OmsOrderItem omsOrderItem = new OmsOrderItem();

                omsOrderItem.setProductQuantity(omsCartItem.getQuantity());
                omsOrderItem.setProductPrice(omsCartItem.getPrice());
                omsOrderItem.setProductPic(omsCartItem.getProductPic());
                omsOrderItem.setProductName(omsCartItem.getProductName());
                omsOrderItem.setProductCategoryId(omsCartItem.getProductCategoryId());
                omsOrderItem.setProductSkuId(omsCartItem.getProductSkuId());
                omsOrderItem.setProductId(omsCartItem.getProductId());
                omsOrderItem.setOrderSn(orderNum);

                omsOrderItems.add(omsOrderItem);

            }
            // 设置订单详情
            omsOrder.setOmsOrderItems(omsOrderItems);
            // 添加订单
            orderService.addOrder(omsOrder);
            // 删除已经结账得到订单
            cartService.deleteHadPayCart(userId);
            CookieUtil.deleteCookie(request, response,"cartListCookie");


            return "redirect:http://pay.gmall.com/index?out_trade_no="+orderNum+"&nickname="+nickname+"&ca="+getSum(cartItems);
        }else {
            return "tradeFail";
        }


    }

    @LoginRequired(ifMust = true)
    @RequestMapping("toTrade")
    public String toTrade(HttpServletRequest request, ModelMap modelMap){

        String userId = (String) request.getAttribute("userId");

        // 根据userId查询购物车选中的商品
        List<OmsCartItem> omsCartItemList = cartService.getCartsByMemberId(userId);
        //通过userId获取其他信息
        List<UmsMemberReceiveAddress> umsMemberReceiveAddressList = new ArrayList<>();
        umsMemberReceiveAddressList = umsMemberService.getUmsMemberReceiveAddressByUserId(userId);

        ArrayList<OmsCartItem> omsCartItems = new ArrayList<>();
        for (OmsCartItem cartItem : omsCartItemList) {

            if(cartItem.getIsChecked().equals("1")){
                OmsOrderItem omsOrderItem = new OmsOrderItem();

                omsOrderItem.setProductId(cartItem.getProductId());
                omsOrderItem.setProductSkuId(cartItem.getProductSkuId());
                omsOrderItem.setProductCategoryId(cartItem.getProductCategoryId());
                omsOrderItem.setProductName(cartItem.getProductName());
                omsOrderItem.setProductPic(cartItem.getProductPic());
                omsOrderItem.setProductPrice(cartItem.getPrice());
                omsOrderItem.setProductQuantity(cartItem.getQuantity());

                omsCartItems.add(cartItem);
            }

        }

        // 生成tradeCode
        String tradeCode = UUID.randomUUID().toString();
        // 存进redis
        orderService.putTradeCodeToCache(tradeCode,userId);


        modelMap.put("userAddressList",umsMemberReceiveAddressList);
        modelMap.put("orderDetailList",omsCartItems);
        modelMap.put("tradeCode",tradeCode);
        modelMap.put("totalAmount",getSum(omsCartItems));
        return "trade";
    }
    private BigDecimal getSum(List<OmsCartItem> omsCartItems) {

        BigDecimal sum = new BigDecimal("0");
        if (omsCartItems != null && omsCartItems.size() > 0) {
            for (OmsCartItem omsCartItem : omsCartItems) {
                if (omsCartItem.getIsChecked().equals("1")) {
                    sum = sum.add(omsCartItem.getPrice().multiply(omsCartItem.getQuantity()));
                }
            }
        }
        return sum;
    }
}
