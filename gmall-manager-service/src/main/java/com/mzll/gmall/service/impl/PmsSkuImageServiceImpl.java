package com.mzll.gmall.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.mzll.gmall.bean.PmsSkuImage;
import com.mzll.gmall.mapper.PmsSkuImageMapper;
import com.mzll.gmall.service.PmsSkuImageService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;


@Service
public class PmsSkuImageServiceImpl implements PmsSkuImageService {

    @Autowired
    private PmsSkuImageMapper pmsSkuImageMapper;
    @Override
    public List<PmsSkuImage> getPmsSkuImage(String skuId) {

        PmsSkuImage pmsSkuImage = new PmsSkuImage();
        pmsSkuImage.setSkuId(skuId);

        return pmsSkuImageMapper.select(pmsSkuImage);
    }
}
