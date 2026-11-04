/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.menu.bean;

import com.alibaba.fastjson.annotation.JSONField;
import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.support.domain.AbstractInsertable;
import com.sunwayworld.framework.support.domain.Insertable;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

@Table(value="T_CORE_MOBILE_PAGE")
public class MobilePageBean
extends AbstractInsertable<Long>
implements Insertable<Long> {
    private static final long serialVersionUID = 3983597583732243066L;
    @Id
    private Long id;
    private Long menuId;
    private Long parentId;
    private String pageNo;
    private String pageName;
    private Integer lvl;
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

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public Long getMenuId() {
        return this.menuId;
    }

    public void setMenuId(Long menuId) {
        this.menuId = menuId;
    }

    public Long getParentId() {
        return this.parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
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

    public Integer getLvl() {
        return this.lvl;
    }

    public void setLvl(Integer lvl) {
        this.lvl = lvl;
    }

    public String getRemark() {
        return this.remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
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

    public Long getOrderNo() {
        return this.orderNo;
    }

    public void setOrderNo(Long orderNo) {
        this.orderNo = orderNo;
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
}
