package demo.annotaion;

import java.lang.annotation.*;

/**
 * create by hanshin on 2021/4/28
 */
@Target({ElementType.TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MyRequestMapping {
    String value() default "";
}
