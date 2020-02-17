package com.mzll.gmall.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.mzll.gmall.bean.PaymentInfo;
import com.mzll.gmall.pay.mapper.PayMapper;
import com.mzll.gmall.service.PayService;
import com.mzll.gmall.util.ActiveMQUtil;
import org.apache.activemq.ScheduledMessage;
import org.apache.activemq.command.ActiveMQMapMessage;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Example;

import javax.jms.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@Service
public class PayServiceImpl implements PayService {

    @Autowired
    PayMapper payMapper;

    @Autowired
    ActiveMQUtil activeMQUtil;

    @Autowired
    AlipayClient alipayClient;

    @Override
    public void addPaymentInfo(PaymentInfo paymentInfo) {

        payMapper.insertSelective(paymentInfo);
    }

    @Override
    public void sendPaySuccessQueue(PaymentInfo paymentInfo) {
        // 创建mq连接工厂
        ConnectionFactory connectionFactory = activeMQUtil.getConnectionFactory();

        // 从工厂获取一个链接
        try {
            Connection connection = connectionFactory.createConnection();
            // 打开链接
            connection.start();
            // 用连接获取一个session
            // 参数：第一个表示开启事务 第二个相当于0
            Session session = connection.createSession(true, Session.SESSION_TRANSACTED);
            // 创建队列
            Queue queue = session.createQueue("PAY_SUCCESS_QUEUE");
            MessageProducer producer = session.createProducer(queue);
            // 创建一个消息
            MapMessage message = new ActiveMQMapMessage();
            message.setString("out_trade_no", paymentInfo.getOrderSn());
            message.setString("status", "1");
            message.setJMSDeliveryMode(DeliveryMode.PERSISTENT);
            producer.send(message);
            session.commit();
            // 关闭连接
            connection.close();
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(PaymentInfo paymentInfo) {

        Example example = new Example(paymentInfo.getClass());
        example.createCriteria().andEqualTo("orderSn", paymentInfo.getOrderSn());

        payMapper.updateByExampleSelective(paymentInfo, example);
    }

    @Override
    public void sendResultCheckQuery(PaymentInfo paymentInfo, Long count) {

        // 创建一个连接工厂
        ConnectionFactory connectionFactory = activeMQUtil.getConnectionFactory();

        try {
            Connection connection = connectionFactory.createConnection();
            connection.start();
            Session session = connection.createSession(true, 0);
            Queue queue = session.createQueue("PAY_RESULT_CHECK_QUEUE");
            MessageProducer producer = session.createProducer(queue);

            MapMessage message = new ActiveMQMapMessage();
            message.setString("out_trade_no", paymentInfo.getOrderSn());
            message.setLong("count", count);
            message.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_DELAY, 10 * 1000);
            message.setJMSDeliveryMode(DeliveryMode.PERSISTENT);
            producer.send(message);
            session.commit();


            connection.close();

        } catch (JMSException e) {
            e.printStackTrace();
        }

    }

    @Override
    public PaymentInfo checkPayStatus(String out_trade_no) {

        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("out_trade_no", out_trade_no);// 外部订单号
        request.setBizContent(JSON.toJSONString(requestMap));
        AlipayTradeQueryResponse response = null;
        try {
            response = alipayClient.execute(request);
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        PaymentInfo paymentInfo = new PaymentInfo();
        if (response.isSuccess()) {
            paymentInfo.setOrderSn(out_trade_no);
            paymentInfo.setCallbackContent(response.toString());
            paymentInfo.setCreateTime(new Date());

            String status = response.getTradeStatus();

            if (status.equals("TRADE_SUCCESS") || status.equals("TRADE_FINISHED")) {
                paymentInfo.setPaymentStatus("已支付");
                paymentInfo.setAlipayTradeNo(response.getTradeNo());
            }


            System.out.println("调用成功");
        } else {
            System.out.println("调用失败");
        }


        return paymentInfo;
    }


    /**
     * 幂等性测试
     * @param out_trade_no
     */
    @Override
    public String checkpaystatus(String out_trade_no) {

        PaymentInfo paymentInfo = new PaymentInfo();
        paymentInfo.setOrderSn(out_trade_no);
        PaymentInfo paymentInfofromDb = payMapper.selectOne(paymentInfo);
        return paymentInfofromDb.getPaymentStatus();
    }
}
