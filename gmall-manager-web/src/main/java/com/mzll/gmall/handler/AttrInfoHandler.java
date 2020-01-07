package com.mzll.gmall.handler;


import com.alibaba.dubbo.config.annotation.Reference;
import com.mzll.gmall.bean.PmsBaseAttrInfo;
import com.mzll.gmall.service.PmsBaseAttrInfoService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@CrossOrigin
public class AttrInfoHandler {


    @Reference
    private PmsBaseAttrInfoService pmsBaseAttrInfoService;


    @ResponseBody
    @RequestMapping("attrInfoList")
    public List<PmsBaseAttrInfo> attrInfoList(String catalog3Id){

        return pmsBaseAttrInfoService.getAttrInfoList(catalog3Id);
    }

}
