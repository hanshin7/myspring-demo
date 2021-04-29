package demo.annotaion;

import java.lang.annotation.*;

/**
 * create by hanshin on 2021/4/28
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MyRequestParam {
    String value();
}
