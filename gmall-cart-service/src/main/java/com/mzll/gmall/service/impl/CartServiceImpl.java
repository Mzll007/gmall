package com.mzll.gmall.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.mzll.gmall.bean.OmsCartItem;
import com.mzll.gmall.mapper.CartMapper;
import com.mzll.gmall.service.CartService;
import com.mzll.gmall.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;
import tk.mybatis.mapper.entity.Example;


@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private RedisUtil redisUtil;
    @Override
    public OmsCartItem isCartExists(String userId, String skuId) {


        OmsCartItem cartItem = new OmsCartItem();
        cartItem.setProductSkuId(skuId);
        cartItem.setMemberId(userId);
        OmsCartItem item = cartMapper.selectOne(cartItem);
        return item;
    }

    @Override
    public void updateCart(OmsCartItem omsCartItemFromUser) {


        Example example = new Example(OmsCartItem.class);
        example.createCriteria().andEqualTo("id",omsCartItemFromUser.getId());
        cartMapper.updateByExample(omsCartItemFromUser,example);

        // 同步缓存
        String key = "user:"+omsCartItemFromUser.getMemberId()+":cart";
        Jedis jedis = redisUtil.getJedis();
        jedis.hset(key,omsCartItemFromUser.getProductSkuId(), JSON.toJSONString(omsCartItemFromUser));
        jedis.close();

    }

    @Override
    public void addCart(OmsCartItem omsCartItem) {
        cartMapper.insertSelective(omsCartItem);
        // 同步缓存
        String key = "user:"+omsCartItem.getMemberId()+":cart";
        Jedis jedis = redisUtil.getJedis();
        jedis.hset(key,omsCartItem.getProductSkuId(),JSON.toJSONString(omsCartItem));
        jedis.close();
    }
}
