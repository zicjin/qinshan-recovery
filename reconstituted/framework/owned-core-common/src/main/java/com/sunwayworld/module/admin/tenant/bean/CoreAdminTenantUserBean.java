/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.admin.tenant.bean;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.support.domain.AbstractPersistable;

@Table(value="T_CORE_ADMIN_TENANT_USER")
public class CoreAdminTenantUserBean
extends AbstractPersistable<Long> {
    private static final long serialVersionUID = 5878658185953851447L;
    @Id
    private Long id;
    private String tenantId;
    private String userId;

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getTenantId() {
        return this.tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
