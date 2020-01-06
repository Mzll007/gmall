package com.mzll.gmall.user.handler;


import com.mzll.gmall.bean.UmsMember;
import com.mzll.gmall.bean.service.UmsMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
public class UmsMemberHandler {



    @Autowired
    private UmsMemberService umsMemberService;

    @RequestMapping("get/all/member")
    public List<UmsMember> getAllMember(){

        return umsMemberService.getAll();
    }

}
