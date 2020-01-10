package com.mzll.gmall.user.handler;


import com.alibaba.dubbo.config.annotation.Reference;
import com.mzll.gmall.bean.UmsMember;
import com.mzll.gmall.service.UmsMemberService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class UmsMemberHandler {


    @Reference
    private UmsMemberService umsMemberService;


    @ResponseBody
    @RequestMapping("get/all/member")
    public List<UmsMember> getAllMember() {

        return umsMemberService.getAll();
    }

}
