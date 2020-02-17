package com.mzll.gmall.service;

import com.mzll.gmall.bean.OmsOrder;
import com.mzll.gmall.bean.UmsMemberReceiveAddress;


public interface OrderService {
    UmsMemberReceiveAddress getReceiveAddressesById(String addressId);
    void addOrder(OmsOrder omsOrder);

    void putTradeCodeToCache(String tradeCode, String userId);

    String getTradeCodeByUserId(String userId);

    OmsOrder getOrderByOutTradeNo(String out_trade_no);

    void updateOrder(OmsOrder omsOrder);
}
