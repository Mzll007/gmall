package com.mzll.gmall.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.mzll.gmall.bean.PmsSearchParam;
import com.mzll.gmall.bean.PmsSearchSkuInfo;
import com.mzll.gmall.service.PmsSearchService;
import io.searchbox.client.JestClient;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@Service
public class PmsSearchServiceImpl implements PmsSearchService {


    @Autowired
    private JestClient jestClient;

    @Override
    public List<PmsSearchSkuInfo> search(PmsSearchParam pmsSearchParam) {

        List<PmsSearchSkuInfo> pmsSearchSkuInfoList = new ArrayList<>();

        // 查询dsl的封装的对象
        String query = getMySearchSourceBuilder(pmsSearchParam);
        System.out.println(query);
        Search search = new Search.Builder(query).addIndex("gmall0830").addType("pmsSearchSkuInfo").build();
        SearchResult execute = null;
        try {
            execute = jestClient.execute(search);
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<SearchResult.Hit<PmsSearchSkuInfo, Void>> hits = execute.getHits(PmsSearchSkuInfo.class);

        for (SearchResult.Hit<PmsSearchSkuInfo, Void> hit : hits) {
            PmsSearchSkuInfo searchSkuInfo = hit.source;
            pmsSearchSkuInfoList.add(searchSkuInfo);

        }

        return pmsSearchSkuInfoList;
    }

    //
    public String getMySearchSourceBuilder(PmsSearchParam pmsSearchParam) {
        String catalog3Id = pmsSearchParam.getCatalog3Id();
        String keyword = pmsSearchParam.getKeyword();
        String[] valueIds = pmsSearchParam.getValueId();
        // 封装查询参数
        // _search
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        // bool
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();

        // term
        if (StringUtils.isNotBlank(catalog3Id)) {
            TermQueryBuilder termQueryBuilder = new TermQueryBuilder("catalog3Id", catalog3Id);
            boolQueryBuilder.filter(termQueryBuilder);
        }
        if (valueIds != null && valueIds.length > 0) {
            for (String valueId : valueIds) {

                TermQueryBuilder termQueryBuilder = new TermQueryBuilder("skuAttrValueList.valueId", valueId);
                boolQueryBuilder.filter(termQueryBuilder);
            }
        }

        // match
        if (StringUtils.isNotBlank(keyword)) {
            MatchQueryBuilder matchQueryBuilder = new MatchQueryBuilder("skuName", keyword);
            boolQueryBuilder.must(matchQueryBuilder);
        }
        // query
        searchSourceBuilder.query(boolQueryBuilder);
        searchSourceBuilder.from(0);
        searchSourceBuilder.size(20);

        return searchSourceBuilder.toString();
    }
}
