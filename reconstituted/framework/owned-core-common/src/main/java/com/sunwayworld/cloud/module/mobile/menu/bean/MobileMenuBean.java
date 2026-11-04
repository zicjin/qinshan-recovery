/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.menu.bean;

import com.alibaba.fastjson.annotation.JSONField;
import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.NotNull;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.support.domain.AbstractInsertable;
import com.sunwayworld.framework.support.domain.Activatable;
import com.sunwayworld.framework.support.domain.Insertable;
import com.sunwayworld.framework.support.domain.Orderable;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

@Table(value="T_CORE_MOBILE_MENU")
public class MobileMenuBean
extends AbstractInsertable<Long>
implements Insertable<Long>,
Activatable<Long>,
Orderable<Long> {
    private static final long serialVersionUID = -2306264335731898897L;
    @Id
    private Long id;
    private String menuName;
    private String remark;
    private String icon;
    private String menuCategoryCode;
    private String menuCategoryName;
    @NotNull(defaultValue="0")
    private String badgeFlag;
    private String badgeConfig;
    private String clickConfig;
    @NotNull(defaultValue="0")
    private String activatedFlag;
    private String activatedById;
    private String activatedByName;
    @JSONField(format="yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime activatedTime;
    private String createdById;
    private String createdByName;
    @JSONField(format="yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;
    private String createdByOrgId;
    private String createdByOrgName;
    private Long orderNo;

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getMenuCategoryCode() {
        return this.menuCategoryCode;
    }

    public void setMenuCategoryCode(String menuCategoryCode) {
        this.menuCategoryCode = menuCategoryCode;
    }

    public String getMenuCategoryName() {
        return this.menuCategoryName;
    }

    public void setMenuCategoryName(String menuCategoryName) {
        this.menuCategoryName = menuCategoryName;
    }

    public String getRemark() {
        return this.remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public String getActivatedFlag() {
        return this.activatedFlag;
    }

    @Override
    public void setActivatedFlag(String activatedFlag) {
        this.activatedFlag = activatedFlag;
    }

    @Override
    public String getActivatedById() {
        return this.activatedById;
    }

    @Override
    public void setActivatedById(String activatedById) {
        this.activatedById = activatedById;
    }

    @Override
    public String getActivatedByName() {
        return this.activatedByName;
    }

    @Override
    public void setActivatedByName(String activatedByName) {
        this.activatedByName = activatedByName;
    }

    @Override
    public LocalDateTime getActivatedTime() {
        return this.activatedTime;
    }

    @Override
    public void setActivatedTime(LocalDateTime activatedTime) {
        this.activatedTime = activatedTime;
    }

    @Override
    public String getCreatedById() {
        return this.createdById;
    }

    @Override
    public void setCreatedById(String createdById) {
        this.createdById = createdById;
    }

    @Override
    public String getCreatedByName() {
        return this.createdByName;
    }

    @Override
    public void setCreatedByName(String createdByName) {
        this.createdByName = createdByName;
    }

    @Override
    public LocalDateTime getCreatedTime() {
        return this.createdTime;
    }

    @Override
    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }

    @Override
    public String getCreatedByOrgId() {
        return this.createdByOrgId;
    }

    @Override
    public void setCreatedByOrgId(String createdByOrgId) {
        this.createdByOrgId = createdByOrgId;
    }

    @Override
    public String getCreatedByOrgName() {
        return this.createdByOrgName;
    }

    @Override
    public void setCreatedByOrgName(String createdByOrgName) {
        this.createdByOrgName = createdByOrgName;
    }

    @Override
    public Long getOrderNo() {
        return this.orderNo;
    }

    @Override
    public void setOrderNo(Long orderNo) {
        this.orderNo = orderNo;
    }

    public String getMenuName() {
        return this.menuName;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }

    public String getIcon() {
        return this.icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getBadgeFlag() {
        return this.badgeFlag;
    }

    public void setBadgeFlag(String badgeFlag) {
        this.badgeFlag = badgeFlag;
    }

    public String getBadgeConfig() {
        return this.badgeConfig;
    }

    public void setBadgeConfig(String badgeConfig) {
        this.badgeConfig = badgeConfig;
    }

    public String getClickConfig() {
        return this.clickConfig;
    }

    public void setClickConfig(String clickConfig) {
        this.clickConfig = clickConfig;
    }
}
