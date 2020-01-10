package com.mzll.gmall.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.mzll.gmall.bean.PmsSkuAttrValue;
import com.mzll.gmall.bean.PmsSkuImage;
import com.mzll.gmall.bean.PmsSkuInfo;
import com.mzll.gmall.bean.PmsSkuSaleAttrValue;
import com.mzll.gmall.mapper.PmsSkuAttrValueMapper;
import com.mzll.gmall.mapper.PmsSkuImageMapper;
import com.mzll.gmall.mapper.PmsSkuInfoMapper;
import com.mzll.gmall.mapper.PmsSkuSaleAttrValueMapper;
import com.mzll.gmall.service.PmsSkuInfoService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;


@Service
public class PmsSkuInfoServiceImpl implements PmsSkuInfoService {
    @Autowired
    private PmsSkuInfoMapper pmsSkuInfoMapper;

    @Autowired
    private PmsSkuImageMapper pmsSkuImageMapper;

    @Autowired
    private PmsSkuAttrValueMapper pmsSkuAttrValueMapper;
    @Autowired
    private PmsSkuSaleAttrValueMapper pmsSkuSaleAttrValueMapper;

    @Override
    public void saveSkuInfo(PmsSkuInfo pmsSkuInfo) {
        // 保存skuinfo
        pmsSkuInfoMapper.insertSelective(pmsSkuInfo);

        // 获取id
        String id = pmsSkuInfo.getId();


        // 保存skuImageList
        List<PmsSkuImage> skuImageList = pmsSkuInfo.getSkuImageList();

        for (PmsSkuImage pmsSkuImage : skuImageList) {


            pmsSkuImage.setSkuId(id);
            pmsSkuImageMapper.insertSelective(pmsSkuImage);
        }
        // 保存skuAttrValueList
        List<PmsSkuAttrValue> skuAttrValueList = pmsSkuInfo.getSkuAttrValueList();
        for (PmsSkuAttrValue pmsSkuAttrValue : skuAttrValueList) {
            pmsSkuAttrValue.setSkuId(id);
            pmsSkuAttrValueMapper.insertSelective(pmsSkuAttrValue);

        }

        // 保存skuSaleAttrValueList
        List<PmsSkuSaleAttrValue> skuSaleAttrValueList = pmsSkuInfo.getSkuSaleAttrValueList();
        for (PmsSkuSaleAttrValue pmsSkuSaleAttrValue : skuSaleAttrValueList) {
            pmsSkuSaleAttrValue.setSkuId(id);
            pmsSkuSaleAttrValueMapper.insertSelective(pmsSkuSaleAttrValue);
        }

    }
}