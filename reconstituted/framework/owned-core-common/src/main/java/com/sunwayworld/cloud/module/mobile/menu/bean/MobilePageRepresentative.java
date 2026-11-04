/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.menu.bean;

import com.alibaba.fastjson.annotation.JSONField;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.tree.bean.AbstractTreeNode;
import java.io.Serializable;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

public class MobilePageRepresentative
extends AbstractTreeNode
implements Serializable,
Comparable<MobilePageRepresentative> {
    @Transient
    private static final long serialVersionUID = 2788693385693472513L;
    private Long menuId;
    private String pageNo;
    private String pageName;
    private String remark;
    private String listShowConfig;
    private String listClickConfig;
    private String listSearchConfig;
    private String chooseBtnConfig;
    private String menuBtnConfig;
    private String createdById;
    private String createdByName;
    @JSONField(format="yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;
    private String createdByOrgId;
    private String createdByOrgName;
    private Long orderNo;

    public Long getMenuId() {
        return this.menuId;
    }

    public void setMenuId(Long menuId) {
        this.menuId = menuId;
    }

    public String getPageNo() {
        return this.pageNo;
    }

    public void setPageNo(String pageNo) {
        this.pageNo = pageNo;
    }

    public String getPageName() {
        return this.pageName;
    }

    public void setPageName(String pageName) {
        this.pageName = pageName;
    }

    public String getRemark() {
        return this.remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getListShowConfig() {
        return this.listShowConfig;
    }

    public void setListShowConfig(String listShowConfig) {
        this.listShowConfig = listShowConfig;
    }

    public String getListClickConfig() {
        return this.listClickConfig;
    }

    public void setListClickConfig(String listClickConfig) {
        this.listClickConfig = listClickConfig;
    }

    public String getListSearchConfig() {
        return this.listSearchConfig;
    }

    public void setListSearchConfig(String listSearchConfig) {
        this.listSearchConfig = listSearchConfig;
    }

    public String getChooseBtnConfig() {
        return this.chooseBtnConfig;
    }

    public void setChooseBtnConfig(String chooseBtnConfig) {
        this.chooseBtnConfig = chooseBtnConfig;
    }

    public String getMenuBtnConfig() {
        return this.menuBtnConfig;
    }

    public void setMenuBtnConfig(String menuBtnConfig) {
        this.menuBtnConfig = menuBtnConfig;
    }

    public String getCreatedById() {
        return this.createdById;
    }

    public void setCreatedById(String createdById) {
        this.createdById = createdById;
    }

    public String getCreatedByName() {
        return this.createdByName;
    }

    public void setCreatedByName(String createdByName) {
        this.createdByName = createdByName;
    }

    public LocalDateTime getCreatedTime() {
        return this.createdTime;
    }

    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }

    public String getCreatedByOrgId() {
        return this.createdByOrgId;
    }

    public void setCreatedByOrgId(String createdByOrgId) {
        this.createdByOrgId = createdByOrgId;
    }

    public String getCreatedByOrgName() {
        return this.createdByOrgName;
    }

    public void setCreatedByOrgName(String createdByOrgName) {
        this.createdByOrgName = createdByOrgName;
    }

    public Long getOrderNo() {
        return this.orderNo;
    }

    public void setOrderNo(Long orderNo) {
        this.orderNo = orderNo;
    }

    @Override
    public int compareTo(MobilePageRepresentative v) {
        if (this.getLvl() > v.getLvl()) {
            return 1;
        }
        if (this.getLvl() == v.getLvl() && this.getOrderNo() > v.getOrderNo()) {
            return 1;
        }
        return -1;
    }
}
