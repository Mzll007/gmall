package com.mzll.gmall.search.handler;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ListHandler {


    @ResponseBody
    @RequestMapping("index")
    public String index(){

        return "index";
    }
}
