package com.mzll.gmall.search.handler;


import com.alibaba.dubbo.config.annotation.Reference;
import com.mzll.gmall.bean.*;
import com.mzll.gmall.service.PmsBaseAttrInfoService;
import com.mzll.gmall.service.PmsSearchService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

@Controller
public class ListHandler {


    @Reference
    private PmsSearchService pmsSearchService;
    @Reference
    private PmsBaseAttrInfoService pmsBaseAttrInfoService;

    @RequestMapping("list.html")
    public String search(PmsSearchParam pmsSearchParam, ModelMap modelMap) {
        // 检索
        List<PmsSearchSkuInfo> pmsSkuInfoList = pmsSearchService.search(pmsSearchParam);
        if (pmsSkuInfoList != null && pmsSkuInfoList.size() > 0) {
            HashSet<String> set = new HashSet<>();

            for (PmsSearchSkuInfo pmsSearchSkuInfo : pmsSkuInfoList) {
                List<PmsSkuAttrValue> skuAttrValueList = pmsSearchSkuInfo.getSkuAttrValueList();
                for (PmsSkuAttrValue pmsSkuAttrValue : skuAttrValueList) {
                    String id = pmsSkuAttrValue.getValueId();
                    set.add(id);
                }
            }

            // 根据id查询出属性值的集合
            List<PmsBaseAttrInfo> pmsBaseAttrInfoList = pmsBaseAttrInfoService.getAttrListByValueIds(set);

            // 面包屑功能
            String[] valueIds = pmsSearchParam.getValueId();
            if (valueIds != null && valueIds.length > 0) {

                ArrayList<PmsSearchCrumb> pmsSearchCrumbs = new ArrayList<>();
                // 加入面包屑
                for (String valueId : valueIds) {
                    PmsSearchCrumb pmsSearchCrumb = new PmsSearchCrumb();
                    pmsSearchCrumb.setValueId(valueId);
                    pmsSearchCrumb.setUrlParam(getUrlParam(pmsSearchParam, valueId));

                    Iterator<PmsBaseAttrInfo> iterator = pmsBaseAttrInfoList.iterator();
                    while (iterator.hasNext()) {
                        List<PmsBaseAttrValue> attrValueList = iterator.next().getAttrValueList();
                        for (PmsBaseAttrValue pmsBaseAttrValue : attrValueList) {
                            String valueName = pmsBaseAttrValue.getValueName();
                            String attrId = pmsBaseAttrValue.getAttrId();
                            if (attrId.equals(valueId)) {
                                pmsSearchCrumb.setValueName(valueName);
                                iterator.remove();
                            }

                        }
                    }

                    pmsSearchCrumbs.add(pmsSearchCrumb);
                }
                modelMap.addAttribute("attrValueSelectedList", pmsSearchCrumbs);

            }
            modelMap.addAttribute("attrList", pmsBaseAttrInfoList);
            modelMap.addAttribute("skuLsInfoList", pmsSkuInfoList);
            modelMap.addAttribute("urlParam", getUrlParam(pmsSearchParam));


        }

        return "list";
    }

    private String getUrlParam(PmsSearchParam pmsSearchParam, String... deletedValueId) {
        String urlParam = "";
        String catalog3Id = pmsSearchParam.getCatalog3Id();
        String keyword = pmsSearchParam.getKeyword();
        String[] valueIds = pmsSearchParam.getValueId();

        if (StringUtils.isNotBlank(catalog3Id)) {
            urlParam += catalog3Id;
        }
        if (StringUtils.isNotBlank(keyword)) {

            if (StringUtils.isNotBlank(catalog3Id)) {
                urlParam += "&";
            }

            urlParam += keyword;
        }
        if (valueIds != null && valueIds.length > 0) {
            if (StringUtils.isNotBlank(catalog3Id)) {
                urlParam += "&";
            }
            for (String valueId : valueIds) {
                if (!(deletedValueId != null && deletedValueId.length > 0 && deletedValueId[0].equals(valueId))) {
                    urlParam += "&valueId=" + valueId;
                }
            }
        }

        return urlParam;
    }

    private String getUrlParam(PmsSearchParam pmsSearchParam) {

        String urlParam = "";
        // 获取其平台属性id和平台属性值id
        String catalog3Id = pmsSearchParam.getCatalog3Id();
        String keyword = pmsSearchParam.getKeyword();
        String[] valueId = pmsSearchParam.getValueId();


        if (StringUtils.isNotBlank(catalog3Id)) {

            urlParam += "catalog3Id=" + catalog3Id;
        }
        if (StringUtils.isNotBlank(keyword)) {

            if (StringUtils.isNotBlank(catalog3Id)) {
                urlParam += "&";
            }
            urlParam += "keyword=" + keyword;
        }

        if (valueId != null && valueId.length > 0) {
            for (String id : valueId) {
                urlParam += "id=" + id;
            }

        }

        return urlParam;

    }

    @RequestMapping("/index")
    public String index() {

        return "index";
    }
}
