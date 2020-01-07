package com.mzll.gmall.service;

import com.mzll.gmall.bean.PmsBaseCatalog1;
import com.mzll.gmall.bean.PmsBaseCatalog2;
import com.mzll.gmall.bean.PmsBaseCatalog3;

import java.util.List;

public interface CatalogService {
    List<PmsBaseCatalog1> getAll();

    List<PmsBaseCatalog2> getCatalog2(String catalog1Id);

    List<PmsBaseCatalog3> getCatalog3(String catalog2Id);
}
