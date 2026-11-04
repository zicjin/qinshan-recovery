/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.riskfactor.bean;

import com.sunwayworld.framework.support.tree.bean.AbstractTreeNode;

public class CoreRiskFactorDTO
extends AbstractTreeNode {
    private static final long serialVersionUID = -1306112348609399708L;
    private String id;
    private String parentId;
    private String riskFactor;

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getParentId() {
        return this.parentId;
    }

    @Override
    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getRiskFactor() {
        return this.riskFactor;
    }

    public void setRiskFactor(String riskFactor) {
        this.riskFactor = riskFactor;
    }
}
