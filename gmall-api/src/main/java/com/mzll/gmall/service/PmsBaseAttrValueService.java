package com.mzll.gmall.service;

import com.mzll.gmall.bean.PmsBaseAttrValue;
import com.mzll.gmall.bean.PmsProductSaleAttrValue;

import java.util.List;

public interface PmsBaseAttrValueService {
    List<PmsBaseAttrValue> getAttrValueList(String attrId);

}
