package com.mzll.gmall.pay.listeners;


import com.mzll.gmall.bean.PaymentInfo;
import com.mzll.gmall.service.PayService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.MapMessage;

@Component
public class PayListener {


    @Autowired
    PayService payService;

    @JmsListener(containerFactory = "jmsQueueListener",destination = "PAY_RESULT_CHECK_QUEUE")
    public void payListener(MapMessage mapMessage) throws JMSException {
        String out_trade_no = mapMessage.getString("out_trade_no");
        Long count = mapMessage.getLong("count");


        // 向支付宝发请求查询支付状态
        PaymentInfo paymentInfoForCheck = payService.checkPayStatus(out_trade_no);
        count ++;
        String paymentStatus = paymentInfoForCheck.getPaymentStatus();
        if(StringUtils.isBlank(paymentStatus)||paymentStatus.equals("WAIT_BUYER_PAY")){
            if(count<=7){
                // 一直发送查询请求
                PaymentInfo paymentInfo = new PaymentInfo();
                paymentInfo.setOrderSn(out_trade_no);
                payService.sendResultCheckQuery(paymentInfo,count);
            }else {
                // 发送请求超过7次
                paymentInfoForCheck.setOrderSn(out_trade_no);
                paymentInfoForCheck.setPaymentStatus("扫码未支付");
                // 更新
                payService.update(paymentInfoForCheck);
                payService.sendPaySuccessQueue(paymentInfoForCheck);
            }
        }else {
            String checkpaystatus = payService.checkpaystatus(out_trade_no);
            if(StringUtils.isNotBlank(checkpaystatus)&&!checkpaystatus.equals("已支付")){
                payService.update(paymentInfoForCheck);
                payService.sendPaySuccessQueue(paymentInfoForCheck);
            }


        }

    }
}
