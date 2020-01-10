package com.mzll.gmall.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.mzll.gmall.bean.PmsProductImage;
import com.mzll.gmall.mapper.PmsProductImageMapper;
import com.mzll.gmall.service.PmsProductImageService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;


@Service
public class PmsProductImageServiceImpl implements PmsProductImageService {

    @Autowired
    private PmsProductImageMapper pmsProductImageMapper;

    @Override
    public List<PmsProductImage> spuImageList(String spuId) {

        PmsProductImage pmsProductImage = new PmsProductImage();
        pmsProductImage.setProductId(spuId);

        return pmsProductImageMapper.select(pmsProductImage);
    }
}
