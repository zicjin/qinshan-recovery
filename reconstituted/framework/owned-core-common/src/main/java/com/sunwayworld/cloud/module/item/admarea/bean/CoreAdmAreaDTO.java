/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.admarea.bean;

import com.sunwayworld.framework.support.tree.bean.AbstractTreeNode;

public class CoreAdmAreaDTO
extends AbstractTreeNode {
    private static final long serialVersionUID = -817339876119626733L;
    private String id;
    public String admAreaName;
    public String parentId;

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    public String getAdmAreaName() {
        return this.admAreaName;
    }

    public void setAdmAreaName(String admAreaName) {
        this.admAreaName = admAreaName;
    }

    @Override
    public String getParentId() {
        return this.parentId;
    }

    @Override
    public void setParentId(String parentId) {
        this.parentId = parentId;
    }
}
