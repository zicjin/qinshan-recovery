/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.tenant.aspect;

import com.sunwayworld.framework.exception.core.ApplicationRuntimeException;
import com.sunwayworld.framework.tenant.TenantManager;
import com.sunwayworld.framework.tenant.annotation.Tenant;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Profile(value={"tenant"})
public class TenantAspect {
    @Autowired
    private TenantManager tenantManager;

    @Around(value="@annotation(tenant)")
    public Object auditService(ProceedingJoinPoint pjp, Tenant tenant) throws Throwable {
        if ("all".equals(tenant.value())) {
            this.tenantManager.call(pjp.getSignature().getName(), () -> {
                try {
                    pjp.proceed();
                }
                catch (Throwable e) {
                    throw new ApplicationRuntimeException(e);
                }
            }, new String[0]);
            return null;
        }
        return this.tenantManager.call(pjp.getSignature().getName(), tenant.value(), () -> {
            try {
                return pjp.proceed();
            }
            catch (Throwable e) {
                throw new ApplicationRuntimeException(e);
            }
        });
    }
}
