/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.dms.bean;

import com.sunwayworld.framework.support.tree.bean.AbstractTreeNode;

public class CoreDmsCategoryDTO
extends AbstractTreeNode {
    private static final long serialVersionUID = 2070479051822889118L;
    private String id;
    private String dmsCategoryName;

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    public String getDmsCategoryName() {
        return this.dmsCategoryName;
    }

    public void setDmsCategoryName(String dmsCategoryName) {
        this.dmsCategoryName = dmsCategoryName;
    }
}
