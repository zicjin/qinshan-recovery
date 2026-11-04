/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.table.bean;

import com.sunwayworld.framework.support.tree.bean.AbstractTreeNode;

public class LcdpTableTreeNodeDTO
extends AbstractTreeNode {
    private static final long serialVersionUID = -8648024492068214686L;
    private String id;
    private String name;
    private String type;
    private String checkOutFlag;
    private String resourceStatus;

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCheckOutFlag() {
        return this.checkOutFlag;
    }

    public void setCheckOutFlag(String checkOutFlag) {
        this.checkOutFlag = checkOutFlag;
    }

    public String getResourceStatus() {
        return this.resourceStatus;
    }

    public void setResourceStatus(String resourceStatus) {
        this.resourceStatus = resourceStatus;
    }
}
