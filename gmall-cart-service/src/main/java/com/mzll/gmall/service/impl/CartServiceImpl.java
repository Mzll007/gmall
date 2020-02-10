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

import java.util.HashMap;
import java.util.List;


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
    public void updateCart(OmsCartItem omsCartItem) {


        Example example = new Example(OmsCartItem.class);
        example.createCriteria().andEqualTo("id", omsCartItem.getId());
        cartMapper.updateByExampleSelective(omsCartItem, example);

        // 同步缓存
        Jedis jedis = redisUtil.getJedis();

        String key = "user:"+omsCartItem.getMemberId()+":carts";

        jedis.hset(key, omsCartItem.getProductSkuId(),JSON.toJSONString(omsCartItem));

        flushCartListCache(omsCartItem.getMemberId());

        jedis.close();

    }


    @Override
    public void addCart(OmsCartItem omsCartItem) {
        cartMapper.insertSelective(omsCartItem);
        // 同步缓存
        String key = "user:" + omsCartItem.getMemberId() + ":carts";
        Jedis jedis = redisUtil.getJedis();

        jedis.hset(key, omsCartItem.getProductSkuId(), JSON.toJSONString(omsCartItem));

        flushCartListCache(omsCartItem.getMemberId());

        jedis.close();
    }

    @Override
    public List<OmsCartItem> getCartsByMemberId(String memberId) {

        OmsCartItem cartItem = new OmsCartItem();
        cartItem.setMemberId(memberId);
        List<OmsCartItem> omsCartItems = cartMapper.select(cartItem);
        return omsCartItems;
    }

    @Override
    public void updateCartChecked(String userId, String skuId, String isChecked) {

        Example e = new Example(OmsCartItem.class);
        e.createCriteria().andEqualTo("productSkuId",skuId).andEqualTo("memberId",userId);

        OmsCartItem cartItem = new OmsCartItem();
        cartItem.setIsChecked(isChecked);
        cartMapper.updateByExampleSelective(cartItem,e);

        // 同步缓存
        flushCartListCache(userId);
    }

    private void flushCartListCache(String userId) {

        Jedis jedis = redisUtil.getJedis();

        OmsCartItem cartItem = new OmsCartItem();
        cartItem.setMemberId(userId);
        List<OmsCartItem> omsCartItems = cartMapper.select(cartItem);

        if (omsCartItems != null && omsCartItems.size() > 0) {

            HashMap<String, String> map = new HashMap<>();
            for (OmsCartItem omsCartItem : omsCartItems) {
                String k = omsCartItem.getProductSkuId();
                String v = JSON.toJSONString(omsCartItem);
                map.put(k, v);
            }
            jedis.hmset("user:" + userId + ":carts", map);
            jedis.close();
        }
    }
}
