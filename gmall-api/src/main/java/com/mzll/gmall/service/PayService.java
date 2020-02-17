package com.mzll.gmall.service;

import com.mzll.gmall.bean.PaymentInfo;

public interface PayService {
    void addPaymentInfo(PaymentInfo paymentInfo);

    void sendPaySuccessQueue(PaymentInfo paymentInfo);

    void update(PaymentInfo paymentInfo);

    void sendResultCheckQuery(PaymentInfo paymentInfo,Long count);

    PaymentInfo checkPayStatus(String out_trade_no);

    String checkpaystatus(String out_trade_no);

}
