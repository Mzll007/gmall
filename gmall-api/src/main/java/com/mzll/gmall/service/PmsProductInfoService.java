package com.mzll.gmall.service;

import com.mzll.gmall.bean.PmsProductInfo;

import java.util.List;

public interface PmsProductInfoService {
    List<PmsProductInfo> spuList(String catalog3Id);

    void saveSpuInfo(PmsProductInfo pmsProductInfo);
}
