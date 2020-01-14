package com.mzll.gmall.mapper;

import com.mzll.gmall.bean.PmsProductSaleAttr;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface PmsProductSaleAttrMapper extends Mapper<PmsProductSaleAttr> {
    List<PmsProductSaleAttr> getPmsProductSaleAttrs(@Param("skuId") String skuId, @Param("productId") String productId);
}
