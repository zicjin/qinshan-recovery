/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.mdm.org.bean;

import com.sunwayworld.framework.support.tree.bean.AbstractTreeNode;

public class CoreOrgDTO
extends AbstractTreeNode {
    private static final long serialVersionUID = -7742602734174964058L;
    private String id;
    private String orgName;

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    public String getOrgName() {
        return this.orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }
}
