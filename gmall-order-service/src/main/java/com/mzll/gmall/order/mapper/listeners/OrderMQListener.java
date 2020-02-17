package com.mzll.gmall.order.mapper.listeners;


import com.mzll.gmall.bean.OmsOrder;
import com.mzll.gmall.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import java.util.Date;

@Component
public class OrderMQListener {


    @Autowired
    OrderService orderService;

    @JmsListener(containerFactory = "jmsQueueListener",destination = "PAY_SUCCESS_QUEUE")
    public void orderConsumer(MapMessage message) throws JMSException {
        String out_trade_no = message.getString("out_trade_no");
        String status = message.getString("status");

        // 更新订单
        if("订单未支付".equals(status)){
            status = "5";
        }
        OmsOrder omsOrder = new OmsOrder();
        omsOrder.setOrderSn(out_trade_no);
        omsOrder.setStatus(status);
        omsOrder.setCreateTime(new Date());
        orderService.updateOrder(omsOrder);

    }
}
