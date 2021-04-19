package demo.aop;

import demo.entity.DataSourceThread;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * create by hanshin on 2021/4/19
 */
@Aspect
@Order(value = 1)
@Component
public class DataSourceAop {
    @Around("@annotation(demo.anno.Slave)")
    public Object setDynamicDataSource(ProceedingJoinPoint pjp) throws Throwable {
        boolean clear = true;
        try {
            DataSourceThread.set();
            System.out.println("切换数据源");
            return pjp.proceed();
        } finally {
            if (clear) {
                DataSourceThread.clear();
            }
        }
    }
}
