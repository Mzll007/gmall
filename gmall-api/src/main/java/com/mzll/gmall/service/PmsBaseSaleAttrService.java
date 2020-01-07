package com.mzll.gmall.service;

import com.mzll.gmall.bean.PmsBaseSaleAttr;
import com.mzll.gmall.bean.PmsProductInfo;

import java.util.List;

public interface PmsBaseSaleAttrService {
    List<PmsBaseSaleAttr> spuList(String catalog3Id);
}
