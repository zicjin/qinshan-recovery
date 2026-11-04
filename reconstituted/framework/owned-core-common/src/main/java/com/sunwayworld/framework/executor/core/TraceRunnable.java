/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.executor.core;

import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.executor.core.PriorityTask;
import com.sunwayworld.framework.executor.core.ThreadPriority;
import com.sunwayworld.framework.security.bean.LoginUser;
import com.sunwayworld.framework.tenant.TenantContext;
import java.util.Locale;
import org.springframework.context.i18n.LocaleContextHolder;

public abstract class TraceRunnable
extends PriorityTask
implements Runnable {
    private final ThreadPriority priority;
    private final String spanName;
    private final Locale locale;
    private final LoginUser loginUser;
    private final String tenant;

    public TraceRunnable(String spanName) {
        this(spanName, TenantContext.getTenant(), ThreadPriority.Normal);
    }

    public TraceRunnable(String spanName, ThreadPriority priority) {
        this(spanName, TenantContext.getTenant(), priority);
    }

    public TraceRunnable(String spanName, String tenant, ThreadPriority priority) {
        this.spanName = spanName;
        this.priority = priority;
        this.locale = LocaleContextHolder.getLocale();
        this.loginUser = LocalContextHelper.getLoginPrincipal();
        this.tenant = tenant;
    }

    public String getSpanName() {
        return this.spanName;
    }

    @Override
    public final int getPriorityValue() {
        return this.priority.getValue();
    }

    @Override
    public Locale getLocale() {
        return this.locale;
    }

    @Override
    public final String getTenant() {
        return this.tenant;
    }

    @Override
    public final LoginUser getLoginUser() {
        return this.loginUser;
    }

    public static TraceRunnable of(Runnable runnable) {
        if (runnable instanceof TraceRunnable) {
            return (TraceRunnable)runnable;
        }
        return TraceRunnable.of("default", TenantContext.getTenant(), ThreadPriority.Normal, runnable);
    }

    public static TraceRunnable of(String spanName, Runnable runnable) {
        return TraceRunnable.of(spanName, TenantContext.getTenant(), ThreadPriority.Normal, runnable);
    }

    public static TraceRunnable of(String spanName, String tenant, Runnable runnable) {
        return TraceRunnable.of(spanName, tenant, ThreadPriority.Normal, runnable);
    }

    public static TraceRunnable of(String spanName, ThreadPriority priority, Runnable runnable) {
        return TraceRunnable.of(spanName, TenantContext.getTenant(), priority, runnable);
    }

    public static TraceRunnable of(String spanName, String tenant, ThreadPriority priority, final Runnable runnable) {
        return new TraceRunnable(spanName, tenant, priority){

            @Override
            public void run() {
                try {
                    LocaleContextHolder.setLocale(this.getLocale());
                    LocalContextHelper.setUserLogin(this.getLoginUser());
                    TenantContext.setTenant(this.getTenant());
                    runnable.run();
                }
                finally {
                    LocaleContextHolder.resetLocaleContext();
                    LocalContextHelper.removeUserLogin();
                    TenantContext.removeTenant();
                }
            }
        };
    }
}
