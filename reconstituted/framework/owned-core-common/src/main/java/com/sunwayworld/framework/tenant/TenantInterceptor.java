/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.tenant;

import com.sunwayworld.framework.tenant.TenantContext;
import com.sunwayworld.framework.utils.StringUtils;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

public class TenantInterceptor
implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String tenant = request.getHeader("sunway.tenant");
        if (!StringUtils.isBlank(tenant)) {
            TenantContext.setTenant(tenant);
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable ModelAndView modelAndView) throws Exception {
        TenantContext.removeTenant();
    }
}
