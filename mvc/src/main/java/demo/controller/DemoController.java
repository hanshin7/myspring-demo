package demo.controller;

import demo.annotaion.MyAutowired;
import demo.annotaion.MyController;
import demo.annotaion.MyRequestMapping;
import demo.annotaion.MyRequestParam;
import demo.service.MyDemoService;

/**
 * create by hanshin on 2021/4/28
 */
@MyController
@MyRequestMapping("/demo")
public class DemoController {

    @MyAutowired
    private MyDemoService myDemoService;

    @MyRequestMapping("/test")
    public String test(){
        String r = myDemoService.test();
        System.out.println(r);
        return r;
    }
}
