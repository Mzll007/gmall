package com.mzll.gmall.hander;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

@Controller
public class DownloadPicHandler {


    @RequestMapping("/")
    public String index(){
        return "index";
    }

    @RequestMapping("getImage")
    public String getImage(){
        String basePath2 = "http://www.viwik.com/tag/5d8a2f1aebc8b202208da675.aspx?page=";
        String basePath1 = "http://www.viwik.com/tag/5d8b75d2ebc8b2022092620c.aspx?page=";
        String basePath = "http://www.viwik.com/tag/5d8a2a29ebc8b202208a42e2.aspx?page=";
        String path;
        for (int i = 2; i < 70; i++) {
            path = basePath + i;
            List<String> images = getImages(path);
            for (String string : images) {
                getImage(string);
            }
        }
        return "success";
    }

    public List<String> getImages(String indexPage) {
        List<String> returnString = new ArrayList<>();
        FileOutputStream fos = null;
        InputStream is = null;
        try {
            URL url1 = new URL(indexPage);

            URLConnection connection5 = url1.openConnection();
            is = connection5.getInputStream();
            fos = new FileOutputStream(new File("D://log/xiazai.html"));
            byte[] buffer1 = new byte[1024];
            int len1;
            while ((len1 = is.read(buffer1)) != -1) {
                fos.write(buffer1, 0, len1);
            }
        } catch (IOException e2) {
            e2.printStackTrace();
        } finally {
            try {
                if (fos != null)
                    fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (is != null)
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
        // 以上得到网页源码
        StringBuilder sb = null;
        try {
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream("D://log/xiazai.html"));
            sb = new StringBuilder("");
            byte[] buffer = new byte[1024];
            int len;
            while ((len = bis.read(buffer)) != -1) {

                sb.append(new String(buffer, 0, len));
            }
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        String str = new String(sb);
        // String[] strings = str.split("class=pic");// src资源链接数组
        String str1 = str.substring(str.indexOf("<ul>"), str.lastIndexOf("</ul>"));
        String strings1 = str1.substring(str1.indexOf("<ul>"), str1.lastIndexOf("</ul>"));
        // System.out.println(strings1);
        String[] strings = strings1.split("<div>");

        // System.out.println(Arrays.toString(strings));

        int i = 0;
        for (String string : strings) {
            if (string.contains(".html")) {
                //System.out.println(string + "11111111111111111111");
                int begin = string.indexOf("class=\"pic\"");
                String string2 = string.substring(begin + 18, string.indexOf(".html") + 5);// 资源详细位置的相对路径
                System.out.println("加载"+string2);
                File file = new File(string2);// https://www.tooopen.com/view/2064537.html

                BufferedOutputStream bos = null;
                InputStream bis1 = null;
                try {
                    URL url = new URL(string2);

                    URLConnection connection = url.openConnection();
                    bis1 = connection.getInputStream();
                    File file1 = new File("D://log/image/html");
                    if(!file1.exists()){
                        file1.mkdirs();
                    }
                    returnString.add(file1.getPath()+string2.substring(string2.lastIndexOf(".") - 7));
                    bos = new BufferedOutputStream(new FileOutputStream(file1.getPath()+file.getName()));
                    byte[] buff = new byte[1024];
                    int leng;
                    while ((leng = bis1.read(buff)) != -1) {
                        bos.write(buff, 0, leng);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (bos != null)
                            bos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (bis1 != null)
                        try {
                            bis1.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                }
            }

        }
        return returnString;
    }

    public void getImage(String index) {
        StringBuilder sb = null;
        try {
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(index));
            sb = new StringBuilder("");
            byte[] buffer = new byte[1024*10];
            int len;
            while ((len = bis.read(buffer)) != -1) {

                sb.append(new String(buffer, 0, len));
            }
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        String str = new String(sb);// 得到html数据
        String string = str.substring(str.indexOf("主体内容"), str.indexOf("右侧内容"));

        int begin = string.indexOf("src");
        String string2 = string.substring(begin + 5, string.indexOf(".jpg") + 4);// 资源详细位置的相对路径
        File file = new File(string2);// https://www.tooopen.com/view/2064537.html
        BufferedOutputStream bos = null;
        InputStream bis1 = null;
        try {
            URL url = new URL(string2);

            URLConnection connection = url.openConnection();
            bis1 = connection.getInputStream();
            File file2 = new File("D://test");
            if(!file2.exists()){
                file2.mkdir();
            }
            String name = "D:/" + file.getName();

            bos = new BufferedOutputStream(new FileOutputStream(name));
            byte[] buff = new byte[1024*10];
            int leng;
            System.out.println(file.getName()+"正在下载...");
            while ((leng = bis1.read(buff)) != -1) {
                bos.write(buff, 0, leng);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bos != null)
                    bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (bis1 != null)
                try {
                    bis1.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }

}
