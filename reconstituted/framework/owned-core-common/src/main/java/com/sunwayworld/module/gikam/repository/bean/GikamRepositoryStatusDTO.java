/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.gikam.repository.bean;

import java.io.Serializable;

public class GikamRepositoryStatusDTO
implements Serializable {
    private static final long serialVersionUID = -4914836446505070991L;
    private Boolean editable;
    private String lockUserId;
    private String lockUserName;

    public Boolean getEditable() {
        return this.editable;
    }

    public void setEditable(Boolean editable) {
        this.editable = editable;
    }

    public String getLockUserId() {
        return this.lockUserId;
    }

    public void setLockUserId(String lockUserId) {
        this.lockUserId = lockUserId;
    }

    public String getLockUserName() {
        return this.lockUserName;
    }

    public void setLockUserName(String lockUserName) {
        this.lockUserName = lockUserName;
    }
}
