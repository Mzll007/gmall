package com.mzll.gmall.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.mzll.gmall.bean.PmsBaseSaleAttr;
import com.mzll.gmall.mapper.PmsBaseSaleAttrMapper;
import com.mzll.gmall.service.PmsBaseSaleAttrService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;


@Service
public class PmsBaseSaleAttrServiceImpl implements PmsBaseSaleAttrService {

    @Autowired
    private PmsBaseSaleAttrMapper pmsBaseSaleAttrMapper;
    @Override
    public List<PmsBaseSaleAttr> spuList(String catalog3Id) {
        return pmsBaseSaleAttrMapper.selectAll();
    }
}
