package com.mzll.gmall.order.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.mzll.gmall.bean.OmsOrder;
import com.mzll.gmall.bean.OmsOrderItem;
import com.mzll.gmall.bean.UmsMemberReceiveAddress;
import com.mzll.gmall.order.mapper.OmsOrderItemMapper;
import com.mzll.gmall.order.mapper.OmsOrderMapper;
import com.mzll.gmall.order.mapper.UmsMemberReceiveAddressMapper;
import com.mzll.gmall.service.OrderService;
import com.mzll.gmall.util.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;
import tk.mybatis.mapper.entity.Example;

import java.util.Collections;
import java.util.List;


@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    UmsMemberReceiveAddressMapper receiveAddressMapper;

    @Autowired
    OmsOrderItemMapper omsOrderItemMapper;

    @Autowired
    OmsOrderMapper omsOrderMapper;

    @Autowired
    RedisUtil redisUtil;

    @Override
    public UmsMemberReceiveAddress getReceiveAddressesById(String addressId) {


        UmsMemberReceiveAddress umsMemberReceiveAddress = new UmsMemberReceiveAddress();
        umsMemberReceiveAddress.setId(addressId);
        UmsMemberReceiveAddress memberReceiveAddresses = receiveAddressMapper.selectOne(umsMemberReceiveAddress);
        return memberReceiveAddresses;
    }

    @Override
    public void addOrder(OmsOrder omsOrder) {

        // 保存订单项
        List<OmsOrderItem> omsOrderItems = omsOrder.getOmsOrderItems();
        for (OmsOrderItem omsOrderItem : omsOrderItems) {
            omsOrderItemMapper.insertSelective(omsOrderItem);
        }
        // 保存订单
        omsOrderMapper.insertSelective(omsOrder);

    }

    @Override
    public void putTradeCodeToCache(String tradeCode, String userId) {
        Jedis jedis = redisUtil.getJedis();
        String key = "user:" + userId + ":tradeCode";
        jedis.setex(key, 60 * 60 * 2, tradeCode);

        jedis.close();

    }

    @Override
    public String getTradeCodeByUserId(String userId) {

        Jedis jedis = redisUtil.getJedis();

        String key = "user:" + userId + ":tradeCode";
        String tradeCode = jedis.get(key);
        if (StringUtils.isNotBlank(tradeCode)) {

            String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
            jedis.eval(script, Collections.singletonList(key), Collections.singletonList(tradeCode));
        }

        jedis.close();
        return tradeCode;
    }

    @Override
    public OmsOrder getOrderByOutTradeNo(String out_trade_no) {

        OmsOrder omsOrder = new OmsOrder();
        omsOrder.setOrderSn(out_trade_no);
        OmsOrder omsOrder1 = omsOrderMapper.selectOne(omsOrder);

        OmsOrderItem omsOrderItem = new OmsOrderItem();
        omsOrderItem.setOrderSn(out_trade_no);
        List<OmsOrderItem> select = omsOrderItemMapper.select(omsOrderItem);

        omsOrder1.setOmsOrderItems(select);

        return omsOrder1;
    }

    @Override
    public void updateOrder(OmsOrder omsOrder) {
        Example example = new Example(omsOrder.getClass());
        example.createCriteria().andEqualTo("orderSn",omsOrder.getOrderSn());

        omsOrderMapper.updateByExampleSelective(omsOrder,example);
    }


}
