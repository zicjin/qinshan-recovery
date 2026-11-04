/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.menu.bean;

import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.tree.bean.AbstractTreeNode;
import java.io.Serializable;

public class CoreMenuGroupLineRepresentation
extends AbstractTreeNode
implements Serializable {
    @Transient
    private static final long serialVersionUID = 2188030665183562281L;
    private String groupId;
    private String menuId;
    private String menuName;
    private String param;
    private Long orderNo;

    public String getGroupId() {
        return this.groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getMenuId() {
        return this.menuId;
    }

    public void setMenuId(String menuId) {
        this.menuId = menuId;
    }

    public String getMenuName() {
        return this.menuName;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }

    public String getParam() {
        return this.param;
    }

    public void setParam(String param) {
        this.param = param;
    }

    public Long getOrderNo() {
        return this.orderNo;
    }

    public void setOrderNo(Long orderNo) {
        this.orderNo = orderNo;
    }
}
