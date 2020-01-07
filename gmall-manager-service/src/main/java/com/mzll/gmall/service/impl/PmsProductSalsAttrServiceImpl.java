package com.mzll.gmall.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.mzll.gmall.bean.PmsBaseSaleAttr;
import com.mzll.gmall.mapper.PmsBaseSaleAttrMapper;
import com.mzll.gmall.service.PmsProductSaleAttrService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;


@Service
public class PmsProductSalsAttrServiceImpl implements PmsProductSaleAttrService {

    @Autowired
    private PmsBaseSaleAttrMapper pmsBaseSaleAttrMapper;

    @Override
    public List<PmsBaseSaleAttr> baseSaleAttrList() {
        return pmsBaseSaleAttrMapper.selectAll();
    }
}
