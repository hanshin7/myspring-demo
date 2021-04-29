package demo.service.impl;

import demo.annotaion.MyService;
import demo.service.MyDemoService;

/**
 * create by hanshin on 2021/4/28
 */
@MyService
public class MyDemoServiceImpl implements MyDemoService {
    @Override
    public String test() {
        return "hello world!";
    }
}
