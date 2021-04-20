package demo.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import demo.model.User;
import demo.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * create by hanshin on 2021/4/20
 */
@Controller
public class OrderController {
    @Reference  //注入要调用的服务
    private UserService userService;

    @RequestMapping("/user/{id}")
    @ResponseBody
    public User getUser(@PathVariable Integer id){
        //调用服务
        User user= userService.findUserById(id);
        return user;
    }
}
