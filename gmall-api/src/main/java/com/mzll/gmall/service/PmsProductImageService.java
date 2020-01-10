package com.mzll.gmall.service;

import com.mzll.gmall.bean.PmsProductImage;

import java.util.List;

public interface PmsProductImageService {
    List<PmsProductImage> spuImageList(String spuId);
}
