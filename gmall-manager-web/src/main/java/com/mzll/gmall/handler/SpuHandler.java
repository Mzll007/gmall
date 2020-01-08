package com.mzll.gmall.handler;


import com.alibaba.dubbo.config.annotation.Reference;
import com.mzll.gmall.bean.PmsProductInfo;
import com.mzll.gmall.service.PmsProductInfoService;
import org.csource.common.MyException;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.util.List;

@Controller
@CrossOrigin
public class SpuHandler {


    @Reference
    private PmsProductInfoService pmsProductInfoService;



    @ResponseBody
    @RequestMapping("fileUpload")
    public String fileUpload(@RequestParam("file") MultipartFile multipartFile) throws IOException, MyException {
        // 链接trackerServer
        String conf_filename = SpuHandler.class.getClassLoader().getResource("tracker.conf").getPath();

        ClientGlobal.init(conf_filename);

        // 创建一个TrackerClient
        TrackerClient trackerClient = new TrackerClient();
        TrackerServer trackerServer = trackerClient.getTrackerServer();
        StorageClient storageClient = new StorageClient(trackerServer, null);

        String url = "http://192.168.23.129";

        byte[] bytes = multipartFile.getBytes();
        String originalFilename = multipartFile.getOriginalFilename();
        int i = originalFilename.lastIndexOf(".");
        String fileName = originalFilename.substring(i + 1);

        String[] strings = storageClient.upload_file(bytes, fileName, null);

        for (String string : strings) {

            url=url+"/"+string;

        }

        return url;
    }

    @ResponseBody
    @RequestMapping("saveSpuInfo")
    public String saveSpuInfo(@RequestBody PmsProductInfo pmsProductInfo){

        pmsProductInfoService.saveSpuInfo(pmsProductInfo);
        return "success";
    }


    @ResponseBody
    @RequestMapping("spuList")
    public List<PmsProductInfo> spuList(String catalog3Id) {


        return pmsProductInfoService.spuList(catalog3Id);
    }
}
