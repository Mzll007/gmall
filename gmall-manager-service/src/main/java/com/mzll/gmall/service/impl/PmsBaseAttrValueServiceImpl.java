package com.mzll.gmall.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.mzll.gmall.bean.PmsBaseAttrValue;
import com.mzll.gmall.bean.PmsProductSaleAttrValue;
import com.mzll.gmall.mapper.PmsBaseAttrValueMapper;
import com.mzll.gmall.service.PmsBaseAttrValueService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;


@Service
public class PmsBaseAttrValueServiceImpl implements PmsBaseAttrValueService {

    @Autowired
    private PmsBaseAttrValueMapper pmsBaseAttrValueMapper;

    @Override
    public List<PmsBaseAttrValue> getAttrValueList(String attrId) {

        return pmsBaseAttrValueMapper.selectAll();
    }


}
