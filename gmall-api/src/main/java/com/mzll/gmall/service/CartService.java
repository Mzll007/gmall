package com.mzll.gmall.service;

import com.mzll.gmall.bean.OmsCartItem;

import java.util.List;

public interface CartService {
    OmsCartItem isCartExists(String userId, String skuId);

    void updateCart(OmsCartItem OmsCartItem);

    void addCart(OmsCartItem omsCartItem);

    List<OmsCartItem> getCartsByMemberId(String memberId);

    void updateCartChecked(String userId, String skuId, String isChecked);
}
