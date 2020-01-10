package com.mzll.gmall.handler;


import com.alibaba.dubbo.config.annotation.Reference;
import com.mzll.gmall.bean.PmsBaseAttrInfo;
import com.mzll.gmall.bean.PmsBaseAttrValue;
import com.mzll.gmall.bean.PmsBaseSaleAttr;
import com.mzll.gmall.service.PmsBaseAttrInfoService;
import com.mzll.gmall.service.PmsBaseAttrValueService;
import com.mzll.gmall.service.PmsProductSaleAttrService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@CrossOrigin
public class AttrInfoHandler {


    @Reference
    private PmsBaseAttrInfoService pmsBaseAttrInfoService;

    @Reference
    private PmsProductSaleAttrService pmsProductSaleAttrService;

    @Reference
    private PmsBaseAttrValueService pmsBaseAttrValueService;

    @ResponseBody
    @RequestMapping("getAttrValueList")
    public List<PmsBaseAttrValue> getAttrValueList(String attrId) {


        return pmsBaseAttrValueService.getAttrValueList(attrId);
    }

    @ResponseBody
    @RequestMapping("baseSaleAttrList")
    public List<PmsBaseSaleAttr> baseSaleAttrList() {


        return pmsProductSaleAttrService.baseSaleAttrList();
    }


    @ResponseBody
    @RequestMapping("saveAttrInfo")
    public String saveAttrInfo(@RequestBody PmsBaseAttrInfo pmsBaseAttrInfo) {
        pmsBaseAttrInfoService.saveAttrInfo(pmsBaseAttrInfo);
        return "success";
    }

    @ResponseBody
    @RequestMapping("attrInfoList")
    public List<PmsBaseAttrInfo> attrInfoList(String catalog3Id) {

        return pmsBaseAttrInfoService.getAttrInfoList(catalog3Id);
    }

}
