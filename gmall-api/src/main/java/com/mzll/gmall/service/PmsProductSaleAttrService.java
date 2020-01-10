package com.mzll.gmall.service;

import com.mzll.gmall.bean.PmsBaseSaleAttr;
import com.mzll.gmall.bean.PmsProductSaleAttr;
import com.mzll.gmall.bean.PmsProductSaleAttrValue;

import java.util.List;

public interface PmsProductSaleAttrService {
    List<PmsBaseSaleAttr> baseSaleAttrList();

    List<PmsProductSaleAttr> spuSaleAttrList(String spuId);
}
