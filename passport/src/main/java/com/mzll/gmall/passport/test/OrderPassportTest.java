package com.mzll.gmall.passport.test;

import com.mzll.gmall.util.HttpclientUtil;

import java.util.HashMap;
import java.util.Map;

public class OrderPassportTest {

    public static void main(String[] args) {

        String code = "230b9de6321ac5ebcb2a246049550259";

        // 授权地址
        String authUrl = "https://api.weibo.com/oauth2/authorize?client_id=2769498344&response_type=code&redirect_uri=http://passport.gmall.com/vlogin";

        // 回调地址
        String callBackUrl = "http://passport.gmall.com/vlogin?code="+code;

        // 交换授权码地址
        String accessTokenUrl = "https://api.weibo.com/oauth2/access_token";
        Map<String,String> map = new HashMap<>();
        map.put("client_id","2769498344");
        map.put("client_secret","4f1681ef4da07bcf0fce173c9bed5a36");
        map.put("grant_type","authorization_code");
        map.put("redirect_uri","http://passport.gmall.com/vlogin");
        map.put("code",code);

        String json = HttpclientUtil.doPost(accessTokenUrl, map);
        System.out.println(json);

        // 用交换码交换用户信息
        String access_token = "2.00QeuTPGICX7BDd94636a17bL_RMhC";
        String uid = "5725571304";


        String showUserUrl = "https://api.weibo.com/2/users/show.json?access_token="+access_token+"&uid="+uid;


    }
}
