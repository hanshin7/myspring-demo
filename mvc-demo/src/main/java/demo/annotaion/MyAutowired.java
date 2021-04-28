package demo.annotaion;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * create by hanshin on 2021/4/28
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MyAutowired {
    String value() default "";
}
