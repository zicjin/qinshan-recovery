/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.admin.tenant.bean;

import com.sunwayworld.module.mdm.user.bean.CoreUserBean;
import java.io.Serializable;

public class CoreAdminUserSyncDTO
implements Serializable {
    private static final long serialVersionUID = 7445649629574248605L;
    private String tenantId;
    private CoreUserBean oldItem;
    private CoreUserBean newItem;

    public String getTenantId() {
        return this.tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public CoreUserBean getOldItem() {
        return this.oldItem;
    }

    public void setOldItem(CoreUserBean oldItem) {
        this.oldItem = oldItem;
    }

    public CoreUserBean getNewItem() {
        return this.newItem;
    }

    public void setNewItem(CoreUserBean newItem) {
        this.newItem = newItem;
    }
}
