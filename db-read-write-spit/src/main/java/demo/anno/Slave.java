package demo.anno;

import java.lang.annotation.*;

/**
 * 用于注解需要使用从库的方法
 * create by hanshin on 2021/4/19
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Slave {
}
