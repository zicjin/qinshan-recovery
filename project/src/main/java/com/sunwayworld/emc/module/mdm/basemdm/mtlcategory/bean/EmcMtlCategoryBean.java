package com.sunwayworld.emc.module.mdm.basemdm.mtlcategory.bean;

import java.time.LocalDateTime;

import com.alibaba.fastjson.annotation.JSONField;
import org.springframework.format.annotation.DateTimeFormat;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractPersistable;
import com.sunwayworld.framework.support.domain.Insertable;

/**
 * 材料类型
 *
 * @author magh@sunwayworld.com
 * @date 2019-09-16
 */

@Table("T_EMC_MTL_CATEGORY")
public class EmcMtlCategoryBean extends AbstractPersistable<Long> implements Insertable<Long> {

    @Transient
    private static final long serialVersionUID = 6419273658687341393L;

    @Id
    private Long id;// 主键

    private String mtlCategoryId;// 材料类型编码

    private String mtlCategoryName;// 材料类型名称

    private String mtlCategoryDesc;// 描述

    private String managementType; // 管理类型

    private String createdById;// 制单人编码

    private String createdByName;// 制单人名称

    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;// 制单时间

    private String createdByOrgId;// 制单人单位编码

    private String createdByOrgName;// 制单人单位名称

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMtlCategoryId() {
        return mtlCategoryId;
    }

    public void setMtlCategoryId(String mtlCategoryId) {
        this.mtlCategoryId = mtlCategoryId;
    }

    public String getMtlCategoryName() {
        return mtlCategoryName;
    }

    public void setMtlCategoryName(String mtlCategoryName) {
        this.mtlCategoryName = mtlCategoryName;
    }

    public String getMtlCategoryDesc() {
        return mtlCategoryDesc;
    }

    public void setMtlCategoryDesc(String mtlCategoryDesc) {
        this.mtlCategoryDesc = mtlCategoryDesc;
    }

    public String getManagementType() {
        return managementType;
    }

    public void setManagementType(String managementType) {
        this.managementType = managementType;
    }

    public String getCreatedById() {
        return createdById;
    }

    public void setCreatedById(String createdById) {
        this.createdById = createdById;
    }

    public String getCreatedByName() {
        return createdByName;
    }

    public void setCreatedByName(String createdByName) {
        this.createdByName = createdByName;
    }

    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }

    public String getCreatedByOrgId() {
        return createdByOrgId;
    }

    public void setCreatedByOrgId(String createdByOrgId) {
        this.createdByOrgId = createdByOrgId;
    }

    public String getCreatedByOrgName() {
        return createdByOrgName;
    }

    public void setCreatedByOrgName(String createdByOrgName) {
        this.createdByOrgName = createdByOrgName;
    }
}
