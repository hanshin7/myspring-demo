package demo.service;


import com.alibaba.dubbo.config.annotation.Service;
import demo.model.User;

/**
 * create by hanshin on 2021/4/20
 */
//此处的@Service是dubbo下的注解，不是spring的注解
@Service
public class UserServiceImpl implements UserService {
    @Override
    public User findUserById(Integer id) {
        User user = new User();
        user.setId(id);
        user.setUsername("test");
        return user;
    }
}
