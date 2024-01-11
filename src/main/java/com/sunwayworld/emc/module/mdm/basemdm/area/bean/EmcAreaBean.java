package com.sunwayworld.emc.module.mdm.basemdm.area.bean;

import java.time.LocalDateTime;

import com.alibaba.fastjson.annotation.JSONField;
import org.springframework.format.annotation.DateTimeFormat;

import com.sunwayworld.framework.constant.Constant;
import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.NotNull;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractPersistable;
import com.sunwayworld.framework.support.domain.Activatable;

/**
 * 区域表
 *
 * @author yuanhh@sunwayworld.com 2019年9月16日
 */

@Table("T_EMC_AREA")
public class EmcAreaBean extends AbstractPersistable<Long> implements Activatable<Long> {

    @Transient
    private static final long serialVersionUID = 8488677533499880889L;

    @Id
    private Long id;// 主键
    private String areaId;// 区域编码
    private String areaName;// 区域名称
    private String areaLevel;// 区域级别
    private Long parentId;// 区域父编码
    @NotNull(defaultValue = "1")
    private Long orderNo;// 排序码
    private String remark;// 备注
    private String createdById;// 制单人编码
    private String createdByName;// 制单人名称
    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;// 制单时间
    private String createdByOrgId;// 制单人单位编码
    private String createdByOrgName;// 制单人单位名称
    @NotNull(defaultValue = Constant.ACTIVATED_STATUS_YES)
    private String activatedFlag;// 启用标志
    private String activatedById;// 启用人编码
    private String activatedByName;// 启用人名称
    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime activatedTime;// 启用时间

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAreaId() {
        return areaId;
    }

    public void setAreaId(String areaId) {
        this.areaId = areaId;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public String getAreaLevel() {
        return areaLevel;
    }

    public void setAreaLevel(String areaLevel) {
        this.areaLevel = areaLevel;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public Long getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(Long orderNo) {
        this.orderNo = orderNo;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getActivatedFlag() {
        return activatedFlag;
    }

    public void setActivatedFlag(String activatedFlag) {
        this.activatedFlag = activatedFlag;
    }

    public String getActivatedById() {
        return activatedById;
    }

    public void setActivatedById(String activatedById) {
        this.activatedById = activatedById;
    }

    public String getActivatedByName() {
        return activatedByName;
    }

    public void setActivatedByName(String activatedByName) {
        this.activatedByName = activatedByName;
    }

    public LocalDateTime getActivatedTime() {
        return activatedTime;
    }

    public void setActivatedTime(LocalDateTime activatedTime) {
        this.activatedTime = activatedTime;
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
