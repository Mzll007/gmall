package com.mzll.gmall.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.mzll.gmall.bean.PmsProductInfo;
import com.mzll.gmall.bean.PmsProductSaleAttr;
import com.mzll.gmall.bean.PmsProductSaleAttrValue;
import com.mzll.gmall.mapper.PmsProductInfoMapper;
import com.mzll.gmall.mapper.PmsProductSaleAttrMapper;
import com.mzll.gmall.mapper.PmsProductSaleAttrValueMapper;
import com.mzll.gmall.service.PmsProductInfoService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;


@Service
public class PmsProductInfoServiceImpl implements PmsProductInfoService {

    @Autowired
    private PmsProductInfoMapper pmsProductInfoMapper;

    @Autowired
    private PmsProductSaleAttrMapper pmsProductSaleAttrMapper;

    @Autowired
    private PmsProductSaleAttrValueMapper pmsProductSaleAttrValueMapper;

    @Override
    public List<PmsProductInfo> spuList(String catalog3Id) {

        PmsProductInfo pmsProductInfo = new PmsProductInfo();
        pmsProductInfo.setCatalog3Id(catalog3Id);

        List<PmsProductInfo> select = pmsProductInfoMapper.select(pmsProductInfo);
        for (PmsProductInfo productInfo : select) {
            productInfo.setSpuName(productInfo.getProductName());
        }
        return select;
    }

    @Override
    public void saveSpuInfo(PmsProductInfo pmsProductInfo) {
        // 保存基本信息
        pmsProductInfoMapper.insertSelective(pmsProductInfo);

        // 获取自增id
        String productId = pmsProductInfo.getId();


        // 获取销售属性列表
        List<PmsProductSaleAttr> spuSaleAttrList = pmsProductInfo.getSpuSaleAttrList();
        // 保存销售属性
        for (PmsProductSaleAttr pmsProductSaleAttr : spuSaleAttrList) {

            // 逐个添加
            pmsProductSaleAttr.setProductId(productId);

            pmsProductSaleAttrMapper.insertSelective(pmsProductSaleAttr);

            // 获取sale_attr_id
            String saleAttrId = pmsProductSaleAttr.getSaleAttrId();

            // 获取销售属性值
            List<PmsProductSaleAttrValue> spuSaleAttrValueList = pmsProductSaleAttr.getSpuSaleAttrValueList();

            // 保存销售属性值
            for (PmsProductSaleAttrValue pmsProductSaleAttrValue : spuSaleAttrValueList) {


                pmsProductSaleAttrValue.setProductId(productId);
                pmsProductSaleAttrValue.setSaleAttrId(saleAttrId);
                pmsProductSaleAttrValueMapper.insertSelective(pmsProductSaleAttrValue);
            }


        }


    }
}
