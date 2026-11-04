/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.item.datacenter.bean;

import com.sunwayworld.framework.support.domain.AbstractBaseData;

public class CoreDataCenterRoleUserDTO
extends AbstractBaseData {
    private static final long serialVersionUID = -7332965975329037267L;
    private Long dataCenterId;
    private Long roleId;
    private String userId;

    public Long getDataCenterId() {
        return this.dataCenterId;
    }

    public void setDataCenterId(Long dataCenterId) {
        this.dataCenterId = dataCenterId;
    }

    public Long getRoleId() {
        return this.roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
