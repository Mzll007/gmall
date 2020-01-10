package com.mzll.gmall.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.mzll.gmall.bean.PmsBaseSaleAttr;
import com.mzll.gmall.bean.PmsProductSaleAttr;
import com.mzll.gmall.bean.PmsProductSaleAttrValue;
import com.mzll.gmall.mapper.PmsBaseSaleAttrMapper;
import com.mzll.gmall.mapper.PmsProductSaleAttrMapper;
import com.mzll.gmall.mapper.PmsProductSaleAttrValueMapper;
import com.mzll.gmall.service.PmsProductSaleAttrService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;


@Service
public class PmsProductSaleAttrServiceImpl implements PmsProductSaleAttrService {

    @Autowired
    private PmsBaseSaleAttrMapper pmsBaseSaleAttrMapper;

    @Autowired
    private PmsProductSaleAttrMapper pmsProductSaleAttrMapper;
    @Autowired
    private PmsProductSaleAttrValueMapper pmsProductSaleAttrValueMapper;

    @Override
    public List<PmsBaseSaleAttr> baseSaleAttrList() {
        return pmsBaseSaleAttrMapper.selectAll();
    }

    @Override
    public List<PmsProductSaleAttr> spuSaleAttrList(String spuId) {

        // 根据spuId查询出attr
        PmsProductSaleAttr pmsProductSaleAttr = new PmsProductSaleAttr();
        pmsProductSaleAttr.setProductId(spuId);
        List<PmsProductSaleAttr> pmsProductSaleAttrs = pmsProductSaleAttrMapper.select(pmsProductSaleAttr);


        // 把attrvalue列表设置进实体类里
        for (PmsProductSaleAttr productSaleAttr : pmsProductSaleAttrs) {
            String attrId = productSaleAttr.getSaleAttrId();
            // 根据spuId查村出attrvalue列表
            PmsProductSaleAttrValue pmsProductSaleAttrValue = new PmsProductSaleAttrValue();
            pmsProductSaleAttrValue.setProductId(spuId);
            pmsProductSaleAttrValue.setSaleAttrId(attrId);
            //
            List<PmsProductSaleAttrValue> pmsProductSaleAttrValues = pmsProductSaleAttrValueMapper.select(pmsProductSaleAttrValue);


            productSaleAttr.setSpuSaleAttrValueList(pmsProductSaleAttrValues);
        }

        // 返回结果
        return pmsProductSaleAttrs;
    }
}
