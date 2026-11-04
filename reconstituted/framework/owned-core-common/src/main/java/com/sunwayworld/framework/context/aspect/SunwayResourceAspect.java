/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.context.aspect;

import com.sunwayworld.framework.utils.ArrayUtils;
import com.sunwayworld.framework.utils.ReflectionUtils;
import com.sunwayworld.framework.utils.ServletUtils;
import javax.servlet.http.HttpServletResponse;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;

@Aspect
@Component
@Order(value=0x7FFFFFFF)
public class SunwayResourceAspect {
    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Around(value="within(com.sunwayworld..resource..*) && !within(com.sunwayworld.cloud.module.lcdp.resource.resource..*)")
    public Object doAspectResource(ProceedingJoinPoint pjp) throws Throwable {
        try {
            Object object = pjp.proceed();
            return object;
        }
        finally {
            HttpServletResponse response;
            String[] paths;
            RequestMapping requestMapping = ReflectionUtils.getAnnotation(pjp.getTarget().getClass(), RequestMapping.class);
            if (requestMapping != null && !ArrayUtils.isEmpty(paths = requestMapping.value()) && (response = ServletUtils.getCurrentResponse()) != null) {
                response.setHeader("GIKAM-INSTANT-SAVE-PATH", paths[0] + "/instant");
            }
        }
    }
}
