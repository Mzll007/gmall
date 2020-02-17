package com.mzll.gmall.pay.handler;


import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.mzll.gmall.annotation.LoginRequired;
import com.mzll.gmall.bean.OmsOrder;
import com.mzll.gmall.bean.PaymentInfo;
import com.mzll.gmall.pay.conf.AlipayConfig;
import com.mzll.gmall.service.OrderService;
import com.mzll.gmall.service.PayService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Controller
public class PayHandler {

    @Autowired
    AlipayClient alipayClient;
    @Reference
    OrderService orderService;

    @Autowired
    PayService payService;

    @RequestMapping("alipay/callback/return")
    public String alipayCallback(HttpServletRequest request){
        String trade_no = request.getParameter("trade_no");
        String out_trade_no = request.getParameter("out_trade_no");
        String sign = request.getParameter("sign");
        String app_id = request.getParameter("app_id");


        //根据支付宝回调结果设置状态
        PaymentInfo paymentInfo = new PaymentInfo();
        paymentInfo.setPaymentStatus("已支付");
        paymentInfo.setAlipayTradeNo(trade_no);
        paymentInfo.setCallbackTime(new Date());
        paymentInfo.setCallbackContent(request.getQueryString());
        paymentInfo.setOrderSn(out_trade_no);

        // 给订单系统发送订单消息 更新订单状态
        String checkpaystatus = payService.checkpaystatus(out_trade_no);
        if(StringUtils.isNotBlank(checkpaystatus)&&!checkpaystatus.equals("已支付")){
            payService.sendPaySuccessQueue(paymentInfo);
            payService.update(paymentInfo);
        }

        return "finish";
    }

    @LoginRequired(ifMust = true)
    @RequestMapping("alipay/submit")
    @ResponseBody
    public String alipaySubmit(String out_trade_no, HttpServletRequest request){

        String userId = (String)request.getAttribute("userId");
        String nickname = (String)request.getAttribute("nickname");

        // 创建请求接口的对象
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();//创建API对应的request
        alipayRequest.setReturnUrl(AlipayConfig.return_payment_url);
        alipayRequest.setNotifyUrl(AlipayConfig.notify_payment_url);//在公共参数中设置回跳和通知地址


        OmsOrder omsOrder = orderService.getOrderByOutTradeNo(out_trade_no);

        Map<String,Object> requestMap = new HashMap<>();
        requestMap.put("out_trade_no",out_trade_no);// 外部订单号
        requestMap.put("product_code","FAST_INSTANT_TRADE_PAY");// 支付宝的产品名
        requestMap.put("total_amount",0.01);// 总金额
        requestMap.put("subject",omsOrder.getOmsOrderItems().get(0).getProductName());//订单的商品名称
        String requestMapJson = JSON.toJSONString(requestMap);

        alipayRequest.setBizContent(requestMapJson);

        String form = "";
        try {
            form = alipayClient.pageExecute(alipayRequest).getBody(); //调用SDK生成表单
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }

        // 生成支付数据保存到后台db
        PaymentInfo paymentInfo = new PaymentInfo();
        paymentInfo.setOrderSn(omsOrder.getOrderSn());
        paymentInfo.setCreateTime(new Date());
        paymentInfo.setOrderId(omsOrder.getId());
        paymentInfo.setTotalAmount(omsOrder.getTotalAmount());
        paymentInfo.setPaymentStatus("未支付");
        paymentInfo.setSubject(omsOrder.getOmsOrderItems().get(0).getProductName());

        payService.addPaymentInfo(paymentInfo);
        // 给支付宝发送延迟查询队列
        payService.sendResultCheckQuery(paymentInfo,1L);





        return form;
    }

    @RequestMapping("index")
    public String index(String nickname, String out_trade_no, String totalAmount, ModelMap modelMap){

        modelMap.put("nickname",nickname);
        modelMap.put("out_trade_no",out_trade_no);
        modelMap.put("totalAmount",totalAmount);
        return "index";
    }
}
