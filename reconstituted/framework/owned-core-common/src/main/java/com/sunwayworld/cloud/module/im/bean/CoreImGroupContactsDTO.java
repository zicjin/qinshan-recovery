/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.im.bean;

import com.sunwayworld.cloud.module.im.bean.CoreImContactsDTO;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CoreImGroupContactsDTO
implements Serializable {
    private static final long serialVersionUID = -971878280142761646L;
    private Long id;
    private String groupName;
    private Integer userQty;
    private Long orderNo;
    List<CoreImContactsDTO> children = new ArrayList<CoreImContactsDTO>();

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGroupName() {
        return this.groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public Integer getUserQty() {
        return this.userQty;
    }

    public void setUserQty(Integer userQty) {
        this.userQty = userQty;
    }

    public Long getOrderNo() {
        return this.orderNo;
    }

    public void setOrderNo(Long orderNo) {
        this.orderNo = orderNo;
    }

    public List<CoreImContactsDTO> getChildren() {
        return this.children;
    }

    public void setChildren(List<CoreImContactsDTO> children) {
        this.children = children;
    }
}
