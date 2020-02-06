package com.mzll.gmall.search;

import com.alibaba.dubbo.config.annotation.Reference;
import com.mzll.gmall.bean.PmsSearchSkuInfo;
import com.mzll.gmall.bean.PmsSkuInfo;
import com.mzll.gmall.service.PmsSkuInfoService;
import io.searchbox.client.JestClient;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
public class GmallSearchServiceApplicationTests {


    @Autowired
    private JestClient jestClient;

    @Reference
    private PmsSkuInfoService pmsSkuInfoService;

    @Test
   public void contextLoads() throws IOException {

        // 查询数据
        List<PmsSkuInfo> pmsSkuInfos = pmsSkuInfoService.getAllSku();

        // 封装数据
        List<PmsSearchSkuInfo> pmsSearchSkuInfos = new ArrayList<>();
        for (PmsSkuInfo pmsSkuInfo : pmsSkuInfos) {

            PmsSearchSkuInfo pmsSearchSkuInfo = new PmsSearchSkuInfo();

            BeanUtils.copyProperties(pmsSkuInfo,pmsSearchSkuInfo);

            // 导入数据
            // 插入的dsl封装对象
            Index index = new Index.Builder(pmsSearchSkuInfo).id(pmsSearchSkuInfo.getId()).type("pmsSearchSkuInfo").index("gmall0830").build();

            jestClient.execute(index);
            pmsSearchSkuInfos.add(pmsSearchSkuInfo);

        }






    }

    public void search() throws IOException {

        // 查询的dsl的封装对象
        String query = "";
        query = getMySearchSourceBuilder();
        System.out.println(query);
        Search search = new Search.Builder(query).addIndex("gmall0830").addType("pmsSearchSkuInfo").build();

        SearchResult execute = jestClient.execute(search);

        List<SearchResult.Hit<PmsSearchSkuInfo, Void>> hits = execute.getHits(PmsSearchSkuInfo.class);

        for (SearchResult.Hit<PmsSearchSkuInfo, Void> hit : hits) {
            PmsSearchSkuInfo source = hit.source;
            System.out.println(source.getSkuName());
        }

        System.out.println(execute);
    }

    public String getMySearchSourceBuilder() {
        // _search
        SearchSourceBuilder mySearchSourceBuilder = new SearchSourceBuilder();

        // bool
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();

        // term
        TermQueryBuilder termQueryBuilder = new TermQueryBuilder("id","1");
        boolQueryBuilder.filter(termQueryBuilder);

        // query
        mySearchSourceBuilder.query(boolQueryBuilder);

        return mySearchSourceBuilder.toString();
    }
}
