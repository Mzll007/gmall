package com.mzll.gmall.interceptors;


import com.alibaba.fastjson.JSON;
import com.mzll.gmall.annotation.LoginRequired;
import com.mzll.gmall.util.CookieUtil;
import com.mzll.gmall.util.HttpclientUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;


@Component
public class AuthInterceptor extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HandlerMethod hm = (HandlerMethod) handler;
        LoginRequired methodAnnotation = hm.getMethodAnnotation(LoginRequired.class);
        if (methodAnnotation == null) {
            return true;
        }
        // 获取token
        String token = "";
        String oldToken = CookieUtil.getCookieValue(request, "oldToken", true);
        String newToken = request.getParameter("newToken");

        if (oldToken != null) {
            token = oldToken;
        }
        if (newToken != null) {
            token = newToken;
        }
        // 判断token是否存在
        if (StringUtils.isNotBlank(token)) {
            // 验证


            String json = HttpclientUtil.doGet("http://passport.gmall.com/verify?token=" + token);

            Map<String, String> mapResult = new HashMap<>();
            Map map = JSON.parseObject(json, mapResult.getClass());
            String userId = (String) map.get("userId");
            String nickname = (String) map.get("nickname");

            if (StringUtils.isNotBlank(userId)) {
                // 验证成功
                // 把token时间更新
                CookieUtil.setCookie(request, response, "oldToken", token, 60 * 60 * 24, true);
                request.setAttribute("userId", userId);
                request.setAttribute("nickname", nickname);
                return true;
            }

        }
        boolean b = methodAnnotation.ifMust();
        if(b){
            // 去登陆
            StringBuffer ReturnUrl = request.getRequestURL();

            response.sendRedirect("http://passport.gmall.com/index?ReturnUrl="+ReturnUrl);
            return false;
        }

        return true;
    }
}
