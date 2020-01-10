package com.mzll.gmall.handler;


import com.alibaba.dubbo.config.annotation.Reference;
import com.mzll.gmall.bean.PmsBaseCatalog1;
import com.mzll.gmall.bean.PmsBaseCatalog2;
import com.mzll.gmall.bean.PmsBaseCatalog3;
import com.mzll.gmall.service.CatalogService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@CrossOrigin
public class Catalog1Handler {


    @Reference
    private CatalogService catalogService;


    @ResponseBody
    @RequestMapping("getCatalog1")
    public List<PmsBaseCatalog1> getCatalog1() {

        return catalogService.getAll();
    }

    @ResponseBody
    @RequestMapping("getCatalog2")
    public List<PmsBaseCatalog2> getCatalog2(String catalog1Id) {

        List<PmsBaseCatalog2> catalogServiceCatalog2 = catalogService.getCatalog2(catalog1Id);
        return catalogServiceCatalog2;
    }

    @ResponseBody
    @RequestMapping("getCatalog3")
    public List<PmsBaseCatalog3> getCatalog3(String catalog2Id) {


        List<PmsBaseCatalog3> catalogServiceCatalog3 = catalogService.getCatalog3(catalog2Id);
        return catalogServiceCatalog3;
    }
}
