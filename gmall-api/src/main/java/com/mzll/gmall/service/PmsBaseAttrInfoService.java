package com.mzll.gmall.service;

import com.mzll.gmall.bean.PmsBaseAttrInfo;

import java.util.List;

public interface PmsBaseAttrInfoService {
    List<PmsBaseAttrInfo> getAttrInfoList(String catalog3Id);
}
