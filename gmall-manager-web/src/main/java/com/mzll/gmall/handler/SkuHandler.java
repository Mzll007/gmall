package com.mzll.gmall.handler;


import com.alibaba.dubbo.config.annotation.Reference;
import com.mzll.gmall.bean.PmsSkuInfo;
import com.mzll.gmall.service.PmsSkuInfoService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@CrossOrigin
public class SkuHandler {


    @Reference
    private PmsSkuInfoService pmsSkuInfoService;

    @RequestMapping("saveSkuInfo")
    @ResponseBody
    public String saveSkuInfo(@RequestBody PmsSkuInfo pmsSkuInfo){
        pmsSkuInfoService.saveSkuInfo(pmsSkuInfo);
        return "success";
    }
}
