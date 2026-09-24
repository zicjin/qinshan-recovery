package com.sunwayworld.emc.module.mdm.bizmdm.monitortmpl.bean;

import java.time.LocalDateTime;

import com.alibaba.fastjson.annotation.JSONField;
import org.springframework.format.annotation.DateTimeFormat;

import com.sunwayworld.framework.constant.Constant;
import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.NotNull;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractInsertable;
import com.sunwayworld.framework.support.domain.Activatable;

/**
 * 监测模板
 *
 * @author magh@sunwayworld.com
 * @date 2019-09-19
 */

@Table("T_EMC_MONITOR_TMPL")
public class EmcMonitorTmplBean extends AbstractInsertable<String> implements Activatable<String> {

    @Transient
    private static final long serialVersionUID = 6464276273115972730L;

    @Id
    private String id;// 监测模板编码<主键>

    private String monitorTmplName;// 监测模板名称

    private Long monitorTmplGroupId;// 监测模板组编码

    private Long cycle;// 周期

    private Long frequency;// 频次

    @NotNull(defaultValue = "1")
    private Long orderNo;// 排序码

    private Long reportConfigLineId;// 默认采样单模板ID

    private String monitorBasis;// 监测依据

    private String monitorInfo;// 默认监测信息

    private Long propertyTmplId;// 属性模板编码

    private String receiveReportLineId; // 默认交接单报表模板ID

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

    private LocalDateTime activatedTime;// 启用时间

    private String monitorPointCategoryId;//监测点类型

    private Long elnTmplId;//ELN模板Id

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMonitorTmplName() {
        return monitorTmplName;
    }

    public void setMonitorTmplName(String monitorTmplName) {
        this.monitorTmplName = monitorTmplName;
    }

    public Long getMonitorTmplGroupId() {
        return monitorTmplGroupId;
    }

    public void setMonitorTmplGroupId(Long monitorTmplGroupId) {
        this.monitorTmplGroupId = monitorTmplGroupId;
    }

    public Long getCycle() {
        return cycle;
    }

    public void setCycle(Long cycle) {
        this.cycle = cycle;
    }

    public Long getFrequency() {
        return frequency;
    }

    public void setFrequency(Long frequency) {
        this.frequency = frequency;
    }

    public Long getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(Long orderNo) {
        this.orderNo = orderNo;
    }

    public Long getReportConfigLineId() {
        return reportConfigLineId;
    }

    public void setReportConfigLineId(Long reportConfigLineId) {
        this.reportConfigLineId = reportConfigLineId;
    }

    public String getMonitorBasis() {
        return monitorBasis;
    }

    public void setMonitorBasis(String monitorBasis) {
        this.monitorBasis = monitorBasis;
    }

    public String getMonitorInfo() {
        return monitorInfo;
    }

    public void setMonitorInfo(String monitorInfo) {
        this.monitorInfo = monitorInfo;
    }

    public Long getPropertyTmplId() {
        return propertyTmplId;
    }

    public void setPropertyTmplId(Long propertyTmplId) {
        this.propertyTmplId = propertyTmplId;
    }

    public String getReceiveReportLineId() {
        return receiveReportLineId;
    }

    public void setReceiveReportLineId(String receiveReportLineId) {
        this.receiveReportLineId = receiveReportLineId;
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

    public String getMonitorPointCategoryId() {
        return monitorPointCategoryId;
    }

    public void setMonitorPointCategoryId(String monitorPointCategoryId) {
        this.monitorPointCategoryId = monitorPointCategoryId;
    }

    public Long getElnTmplId() {
        return elnTmplId;
    }

    public void setElnTmplId(Long elnTmplId) {
        this.elnTmplId = elnTmplId;
    }
}
