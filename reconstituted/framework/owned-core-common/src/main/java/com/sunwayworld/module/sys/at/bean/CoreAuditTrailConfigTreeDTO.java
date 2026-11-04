/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.at.bean;

import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.tree.bean.AbstractTreeNode;

public class CoreAuditTrailConfigTreeDTO
extends AbstractTreeNode {
    @Transient
    private static final long serialVersionUID = -10234359040195498L;
    private String id;
    private String auditTrail;

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    public String getAuditTrail() {
        return this.auditTrail;
    }

    public void setAuditTrail(String auditTrail) {
        this.auditTrail = auditTrail;
    }
}
