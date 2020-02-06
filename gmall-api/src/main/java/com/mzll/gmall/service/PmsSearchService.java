package com.mzll.gmall.service;

import com.mzll.gmall.bean.PmsSearchParam;
import com.mzll.gmall.bean.PmsSearchSkuInfo;

import java.util.List;

public interface PmsSearchService {

    List<PmsSearchSkuInfo> search(PmsSearchParam pmsSearchParam);
}
