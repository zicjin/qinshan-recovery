/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.mybatis.aspect;

import com.sunwayworld.framework.mybatis.interceptor.MybatisColumnTypeHolder;
import java.util.Map;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class MybatisColumnTypeAspect {
    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Around(value="within(com.sunwayworld..service..*)||within(com.sunwayworld..schedule..*)")
    public Object doAspect(ProceedingJoinPoint pjp) throws Throwable {
        Map<String, Class<?>> columnTypeMap = MybatisColumnTypeHolder.popColumnTypeMap();
        try {
            Object object = pjp.proceed();
            return object;
        }
        finally {
            MybatisColumnTypeHolder.setColumnTypeMap(columnTypeMap);
        }
    }
}
