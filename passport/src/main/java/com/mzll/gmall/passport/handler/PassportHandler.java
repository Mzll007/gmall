package com.mzll.gmall.passport.handler;


import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.mzll.gmall.bean.OmsCartItem;
import com.mzll.gmall.bean.UmsMember;
import com.mzll.gmall.service.CartService;
import com.mzll.gmall.service.UmsMemberService;
import com.mzll.gmall.util.CookieUtil;
import com.mzll.gmall.util.HttpclientUtil;
import com.mzll.gmall.util.JwtUtil;
import jdk.nashorn.internal.parser.Token;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@Controller
public class PassportHandler {

    @Reference
    UmsMemberService umsMemberService;

    @Reference
    CartService cartService;

    @RequestMapping("vlogin")
    public String vlogin(String code, HttpServletRequest request, ModelMap modelMap) {

        // 根据code获取交换码
        String accessTokenUrl = "https://api.weibo.com/oauth2/access_token";
        Map<String, String> accessTokenUrlMap = new HashMap<>();
        accessTokenUrlMap.put("client_id", "2769498344");
        accessTokenUrlMap.put("client_secret", "4f1681ef4da07bcf0fce173c9bed5a36");
        accessTokenUrlMap.put("grant_type", "authorization_code");
        accessTokenUrlMap.put("redirect_uri", "http://passport.gmall.com/vlogin");
        accessTokenUrlMap.put("code", code);

        String json = HttpclientUtil.doPost(accessTokenUrl, accessTokenUrlMap);
        if (json != null) {

            Map accessMap = JSON.parseObject(json, accessTokenUrlMap.getClass());
            String access_token = (String) accessMap.get("access_token");
            String uid = (String) accessMap.get("uid");

            // 交换用户信息
            String showUserUrl = "https://api.weibo.com/2/users/show.json?access_token=" + access_token + "&uid=" + uid;

            // 获取用户信息
            String userJsonStr = HttpclientUtil.doGet(showUserUrl);
            Map<String, String> userMap = new HashMap<>();

            userMap = JSON.parseObject(userJsonStr, userMap.getClass());

            // 保存用户数据
            UmsMember umsMember = new UmsMember();
            umsMember.setSourceType("2");
            umsMember.setSourceUid(uid);
            umsMember.setNickname((String) userMap.get("screen_name"));
            umsMember.setAccessCode(code);
            umsMember.setAccessToken(access_token);
            umsMember.setCreateTime(new Date());
            umsMember.setCity((String) userMap.get("city"));

            // 保存数据库
            UmsMember member = umsMemberService.addVloginUser(umsMember);

            // 生成token
            String key = "jwtToken";
            String ip = "";
            ip = request.getHeader("X-forwarded-for");
            if (StringUtils.isBlank(ip)) {
                ip = request.getRemoteAddr();
                if (StringUtils.isBlank(ip)) {
                    ip = "127.0.0.1";
                }
            }
            // 用户信息
            Map<String, String> mapToken = new HashMap<>();
            mapToken.put("userId", member.getId());
            mapToken.put("nickname", member.getNickname());

            String token = JwtUtil.encode(key, mapToken, ip);
            // 同步缓存
            umsMemberService.putToken(token, member);
            // 重定向到首页
            return "redirect:http://search.gmall.com/index?newToken=" + token;


        }
        return "index";
    }


    @RequestMapping("verify")
    @ResponseBody
    public String verify(String token) {
        UmsMember umsMember = umsMemberService.verify(token);

        if (umsMember == null) {
            return null;
        }
        Map<String, String> map = new HashMap<>();
        map.put("userId", umsMember.getId());
        map.put("nickname", umsMember.getNickname());


        String mapResult = JSON.toJSONString(map);

        return mapResult;
    }

    @RequestMapping("login")
    @ResponseBody
    public String login(HttpServletRequest request, UmsMember umsMember, HttpServletResponse response) {

        UmsMember umsMemberFromDb = umsMemberService.login(umsMember);
        String token = "";
        if (umsMemberFromDb == null) {
            token = "fail";
        } else {
            Map<String, String> map = new HashMap<>();
            map.put("userId", umsMemberFromDb.getId());
            map.put("nickname", umsMemberFromDb.getNickname());
            String ip = request.getRemoteAddr();

            token = JwtUtil.encode("jwtToken", map, ip);

            umsMemberService.putToken(token, umsMemberFromDb);

            // 发送一登陆的消息给中间件
            umsMemberService.sendHadLogin(umsMember.getId(),umsMember.getNickname());

            

        }

        return token;
    }

    @RequestMapping("index")
    public String index(String ReturnUrl, ModelMap modelMap) {

        modelMap.put("ReturnUrl", ReturnUrl);

        return "index";
    }
}
