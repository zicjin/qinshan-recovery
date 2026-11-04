/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.monitor.core.aspect;

import com.sunwayworld.cloud.module.monitor.core.cache.CoreMonitorReqCache;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Profile(value={"monitor"})
@Order(value=0x7FFFFFFF)
public class CoreMonitorAspect {
    @Around(value="within(com.sunwayworld..resource..*)")
    public Object handleResource(ProceedingJoinPoint joinPoint) throws Throwable {
        CoreMonitorReqCache.INSTANCE.increaseTps();
        return joinPoint.proceed();
    }
}
