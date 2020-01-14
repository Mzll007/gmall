package com.mzll.gmall.item.handler;


import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.mzll.gmall.bean.*;
import com.mzll.gmall.service.PmsProductSaleAttrService;
import com.mzll.gmall.service.PmsSkuImageService;
import com.mzll.gmall.service.PmsSkuInfoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class ItemHandler {

    @Reference
    private PmsSkuInfoService pmsSkuInfoService;

    @Reference
    private PmsSkuImageService pmsSkuImageService;

    @Reference
    private PmsProductSaleAttrService pmsProductSaleAttrService;

    @RequestMapping("{skuId}.htm")
    public String getPmsSkuInfo1(@PathVariable String skuId, ModelMap modelMap) {
        // 获取sku信息
        PmsSkuInfo pmsSkuInfo = pmsSkuInfoService.getPmsSkuInfo(skuId);
        // 获取销售属性
        List<PmsProductSaleAttr> pmsProductSaleAttrs =
                pmsProductSaleAttrService.getPmsProductSaleAttrs(skuId, pmsSkuInfo.getProductId());

        modelMap.addAttribute("skuInfo", pmsSkuInfo);
        modelMap.addAttribute("spuSaleAttrListCheckBySku", pmsProductSaleAttrs);

        String productId = pmsSkuInfo.getProductId();


        modelMap.addAttribute("spuId", productId);

        return "item";
    }

    @ResponseBody
    @RequestMapping("getMySpu")
    public String getMySpu(String spuId){

        List<PmsSkuInfo> pmsSkuInfos = pmsSkuInfoService.getSkuSaleAttrValue(spuId);

        Map<String,String> map = new HashMap<>();

        for (PmsSkuInfo skuInfo : pmsSkuInfos) {
            List<PmsSkuSaleAttrValue> skuSaleAttrValueList = skuInfo.getSkuSaleAttrValueList();
            String k = "";
            for (PmsSkuSaleAttrValue pmsSkuSaleAttrValue : skuSaleAttrValueList) {
                k = k + "|" + pmsSkuSaleAttrValue.getSaleAttrValueId();
            }
            String v = skuInfo.getId();
            map.put(k,v);
        }

        String json = JSON.toJSONString(map);

        // 把json序列化
        File file = new File("D:/spuId_" + spuId + ".json");

        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(json.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return json;
    }
 @RequestMapping("{skuId}.html")
    public String getPmsSkuInfo(@PathVariable String skuId, ModelMap modelMap) {
        // 获取sku信息
        PmsSkuInfo pmsSkuInfo = pmsSkuInfoService.getPmsSkuInfo(skuId);
        // 获取销售属性
        List<PmsProductSaleAttr> pmsProductSaleAttrs =
                pmsProductSaleAttrService.getPmsProductSaleAttrs(skuId, pmsSkuInfo.getProductId());

        modelMap.addAttribute("skuInfo", pmsSkuInfo);
        modelMap.addAttribute("spuSaleAttrListCheckBySku", pmsProductSaleAttrs);

        String productId = pmsSkuInfo.getProductId();

        List<PmsSkuInfo> pmsSkuInfos = pmsSkuInfoService.getSkuSaleAttrValue(productId);


        Map<String,String> map = new HashMap<String ,String>();
        for (PmsSkuInfo skuInfo : pmsSkuInfos) {
            List<PmsSkuSaleAttrValue> skuSaleAttrValueList = skuInfo.getSkuSaleAttrValueList();
            String k = "";
            for (PmsSkuSaleAttrValue pmsSkuSaleAttrValue : skuSaleAttrValueList) {
                k = k + "|" + pmsSkuSaleAttrValue.getSaleAttrValueId();
            }
            String v = skuInfo.getId();
            map.put(k,v);
        }
        modelMap.addAttribute("skuIdMap", JSON.toJSON(map));

        return "item";
    }

    @RequestMapping("index")
    public String test(ModelMap modelMap) {
        List<Object> objects = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            objects.add("h" + i);
        }
        modelMap.addAttribute("hellos", objects);
        return "index";
    }
}
