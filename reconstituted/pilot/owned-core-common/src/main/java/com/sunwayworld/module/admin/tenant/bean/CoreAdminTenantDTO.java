/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.admin.tenant.bean;

import java.io.Serializable;

public class CoreAdminTenantDTO
implements Serializable {
    private static final long serialVersionUID = -5470283389677559361L;
    private String id;
    private String tenantName;

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTenantName() {
        return this.tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }
}
