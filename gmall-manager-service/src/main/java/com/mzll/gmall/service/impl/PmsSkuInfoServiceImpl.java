package com.mzll.gmall.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.mzll.gmall.bean.PmsSkuAttrValue;
import com.mzll.gmall.bean.PmsSkuImage;
import com.mzll.gmall.bean.PmsSkuInfo;
import com.mzll.gmall.bean.PmsSkuSaleAttrValue;
import com.mzll.gmall.mapper.PmsSkuAttrValueMapper;
import com.mzll.gmall.mapper.PmsSkuImageMapper;
import com.mzll.gmall.mapper.PmsSkuInfoMapper;
import com.mzll.gmall.mapper.PmsSkuSaleAttrValueMapper;
import com.mzll.gmall.service.PmsSkuInfoService;
import com.mzll.gmall.util.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;


@Service
public class PmsSkuInfoServiceImpl implements PmsSkuInfoService {
    int i = 0;
    @Autowired
    private PmsSkuInfoMapper pmsSkuInfoMapper;

    @Autowired
    private PmsSkuImageMapper pmsSkuImageMapper;

    @Autowired
    private PmsSkuAttrValueMapper pmsSkuAttrValueMapper;
    @Autowired
    private PmsSkuSaleAttrValueMapper pmsSkuSaleAttrValueMapper;

    @Autowired
    private RedisUtil redisUtil;

    @Override
    public void saveSkuInfo(PmsSkuInfo pmsSkuInfo) {
        // 保存skuinfo
        pmsSkuInfoMapper.insertSelective(pmsSkuInfo);

        // 获取id
        String id = pmsSkuInfo.getId();


        // 保存skuImageList
        List<PmsSkuImage> skuImageList = pmsSkuInfo.getSkuImageList();

        for (PmsSkuImage pmsSkuImage : skuImageList) {


            pmsSkuImage.setSkuId(id);
            pmsSkuImageMapper.insertSelective(pmsSkuImage);
        }
        // 保存skuAttrValueList
        List<PmsSkuAttrValue> skuAttrValueList = pmsSkuInfo.getSkuAttrValueList();
        for (PmsSkuAttrValue pmsSkuAttrValue : skuAttrValueList) {
            pmsSkuAttrValue.setSkuId(id);
            pmsSkuAttrValueMapper.insertSelective(pmsSkuAttrValue);

        }

        // 保存skuSaleAttrValueList
        List<PmsSkuSaleAttrValue> skuSaleAttrValueList = pmsSkuInfo.getSkuSaleAttrValueList();
        for (PmsSkuSaleAttrValue pmsSkuSaleAttrValue : skuSaleAttrValueList) {
            pmsSkuSaleAttrValue.setSkuId(id);
            pmsSkuSaleAttrValueMapper.insertSelective(pmsSkuSaleAttrValue);
        }

    }

    public PmsSkuInfo getPmsSkuInfoFromDb(String skuId) {
        PmsSkuInfo pmsSkuInfo = new PmsSkuInfo();
        pmsSkuInfo.setId(skuId);

        PmsSkuInfo pmsSkuInfo1 = pmsSkuInfoMapper.selectOne(pmsSkuInfo);

        PmsSkuImage pmsSkuImage = new PmsSkuImage();
        pmsSkuImage.setSkuId(skuId);

        List<PmsSkuImage> pmsSkuImages = pmsSkuImageMapper.select(pmsSkuImage);

        pmsSkuInfo1.setSkuImageList(pmsSkuImages);

        return pmsSkuInfo1;
    }

    @Override
    public PmsSkuInfo getPmsSkuInfo(String skuId) {
        PmsSkuInfo pmsSkuInfo = null;
        Jedis jedis = null;
        try {
            // 查询缓存
            jedis = redisUtil.getJedis();
            String skuInfo = jedis.get("sku:" + skuId + ":info");
            if (StringUtils.isNotBlank(skuInfo)) {
                System.err.println("redis" + i++);
                pmsSkuInfo = JSON.parseObject(skuInfo, PmsSkuInfo.class);
            } else {
                System.err.println("mysql");
                String v = UUID.randomUUID().toString();

                String k = "sku:" + skuId + ":lock";

                // 加锁
                jedis.set(k, v, "nx", "ex", 10);


                String sV = jedis.get(k);
                if (Objects.equals(v, sV)) {

//                    // 释放锁方法二：k
                    String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
                    jedis.eval(script, Collections.singletonList(k), Collections.singletonList(v));
                    // 查询数据库
                    pmsSkuInfo = getPmsSkuInfoFromDb(skuId);
                    // 同步到缓存
                    jedis.set("sku:" + skuId + ":info", JSON.toJSONString(pmsSkuInfo));
                    // 释放锁方法一：
                    //   jedis.del("sku:"+skuId+":lock");

                } else {
                    return getPmsSkuInfo(skuId);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            jedis.close();
        }

        return pmsSkuInfo;

    }

    @Override
    public List<PmsSkuInfo> getSkuSaleAttrValue(String productId) {


        PmsSkuInfo pmsSkuInfo = new PmsSkuInfo();
        pmsSkuInfo.setProductId(productId);
        List<PmsSkuInfo> pmsSkuInfos = pmsSkuInfoMapper.select(pmsSkuInfo);

        for (PmsSkuInfo skuInfo : pmsSkuInfos) {
            String id = skuInfo.getId();

            PmsSkuSaleAttrValue pmsSkuSaleAttrValue = new PmsSkuSaleAttrValue();

            pmsSkuSaleAttrValue.setSkuId(id);

            List<PmsSkuSaleAttrValue> pmsSkuSaleAttrValues = pmsSkuSaleAttrValueMapper.select(pmsSkuSaleAttrValue);
            skuInfo.setSkuSaleAttrValueList(pmsSkuSaleAttrValues);
        }

        return pmsSkuInfos;
    }

    @Override
    public List<PmsSkuInfo> getAllSku() {
        List<PmsSkuInfo> pmsSkuInfoList = pmsSkuInfoMapper.selectAll();
        for (PmsSkuInfo pmsSkuInfo : pmsSkuInfoList) {

            String id = pmsSkuInfo.getId();
            // 根据id查询attrvalue
            PmsSkuAttrValue pmsSkuAttrValue = new PmsSkuAttrValue();
            pmsSkuAttrValue.setSkuId(id);

            List<PmsSkuAttrValue> skuAttrValues = pmsSkuAttrValueMapper.select(pmsSkuAttrValue);
            pmsSkuInfo.setSkuAttrValueList(skuAttrValues);
        }

        return pmsSkuInfoList;
    }


}