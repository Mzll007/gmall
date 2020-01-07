package com.mzll.gmall.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.mzll.gmall.bean.PmsBaseAttrInfo;
import com.mzll.gmall.bean.PmsBaseAttrValue;
import com.mzll.gmall.mapper.PmsBaseAttrInfoMapper;
import com.mzll.gmall.mapper.PmsBaseAttrValueMapper;
import com.mzll.gmall.service.PmsBaseAttrInfoService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;


@Service
public class PmsBaseAttrInfoServiceImpl implements PmsBaseAttrInfoService {

    @Autowired
    private PmsBaseAttrInfoMapper pmsBaseAttrInfoMapper;
    @Autowired
    private PmsBaseAttrValueMapper pmsBaseAttrValueMapper;

    @Override
    public List<PmsBaseAttrInfo> getAttrInfoList(String catalog3Id) {

        PmsBaseAttrInfo pmsBaseAttrInfo = new PmsBaseAttrInfo();
        pmsBaseAttrInfo.setCatalog3Id(catalog3Id);
        List<PmsBaseAttrInfo> select = pmsBaseAttrInfoMapper.select(pmsBaseAttrInfo);
        return select;
    }

    @Override
    public void saveAttrInfo(PmsBaseAttrInfo pmsBaseAttrInfo) {

        // 插入信息表
        pmsBaseAttrInfoMapper.insertSelective(pmsBaseAttrInfo);
        // 获取信息表自增的id
        String id = pmsBaseAttrInfo.getId();

        // 获取pmsbaseattrinfo的value
        List<PmsBaseAttrValue> pmsBaseAttrValues = pmsBaseAttrInfo.getAttrValueList();


        // 逐条插入
        for (PmsBaseAttrValue baseAttrValue : pmsBaseAttrValues) {
            pmsBaseAttrValueMapper.insertSelective(baseAttrValue);
        }
    }
}