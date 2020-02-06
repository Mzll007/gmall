package com.mzll.gmall.service;

import com.mzll.gmall.bean.OmsCartItem;

public interface CartService {
    OmsCartItem isCartExists(String userId, String skuId);

    void updateCart(OmsCartItem omsCartItemFromUser);

    void addCart(OmsCartItem omsCartItem);
}
