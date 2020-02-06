package com.mzll.gmall.service;

import com.mzll.gmall.bean.PmsSkuInfo;

import java.util.List;

public interface PmsSkuInfoService {
    void saveSkuInfo(PmsSkuInfo pmsSkuInfo);

    PmsSkuInfo getPmsSkuInfo(String skuId);

    List<PmsSkuInfo> getSkuSaleAttrValue(String productId);

    List<PmsSkuInfo> getAllSku();
}
