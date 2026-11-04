/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.context.aspect;

import com.sunwayworld.framework.context.SunwayAopContext;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Order(value=-2147483648)
public class SunwayContextAspect {
    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Around(value="(within(com.sunwayworld..service..*)||within(com.sunwayworld..schedule..*)||within(com.sunwayworld..dao..*))&&!@annotation(com.sunwayworld.framework.aspect.annotation.IgnoreAop)")
    public Object doAspect(ProceedingJoinPoint pjp) throws Throwable {
        boolean init = SunwayAopContext.initCache();
        Object old = SunwayAopContext.currentProxy();
        try {
            SunwayAopContext.setCurrentProxy(pjp.getThis());
            Object object = pjp.proceed();
            return object;
        }
        finally {
            SunwayAopContext.setCurrentProxy(old);
            if (init) {
                SunwayAopContext.clearCache();
            }
        }
    }
}
