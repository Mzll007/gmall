package com.mzll.gmall.service;

import com.mzll.gmall.bean.PmsSkuImage;

import java.util.List;

public interface PmsSkuImageService {
    List<PmsSkuImage> getPmsSkuImage(String skuId);
}
