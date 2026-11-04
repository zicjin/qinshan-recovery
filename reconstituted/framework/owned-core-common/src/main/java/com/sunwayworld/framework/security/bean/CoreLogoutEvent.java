/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.security.bean;

import com.sunwayworld.framework.tenant.TenantContext;
import org.springframework.context.ApplicationEvent;

public class CoreLogoutEvent
extends ApplicationEvent {
    private static final long serialVersionUID = 2603167180485626135L;
    private String userId;
    private String tenant;

    public CoreLogoutEvent(String userId) {
        super(userId);
        this.userId = userId;
        this.tenant = TenantContext.getTenant();
    }

    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTenant() {
        return this.tenant;
    }

    public void setTenant(String tenant) {
        this.tenant = tenant;
    }
}
