/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.admin.menu.bean;

import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.tree.bean.AbstractTreeNode;

public class CoreAdminMenuDTO
extends AbstractTreeNode {
    @Transient
    private static final long serialVersionUID = 3501256693867350690L;
    private String id;
    private String menuName;
    private String menuIcon;
    private String href;
    private String parentId;
    private Long orderNo;

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    public String getMenuName() {
        return this.menuName;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }

    public String getMenuIcon() {
        return this.menuIcon;
    }

    public void setMenuIcon(String menuIcon) {
        this.menuIcon = menuIcon;
    }

    @Override
    public String getHref() {
        return this.href;
    }

    @Override
    public void setHref(String href) {
        this.href = href;
    }

    @Override
    public String getParentId() {
        return this.parentId;
    }

    @Override
    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public Long getOrderNo() {
        return this.orderNo;
    }

    public void setOrderNo(Long orderNo) {
        this.orderNo = orderNo;
    }
}
