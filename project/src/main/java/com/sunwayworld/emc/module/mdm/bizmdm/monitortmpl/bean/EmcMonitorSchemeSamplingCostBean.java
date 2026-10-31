package com.sunwayworld.emc.module.mdm.bizmdm.monitortmpl.bean;

import java.time.LocalDateTime;

import com.alibaba.fastjson.annotation.JSONField;
import org.springframework.format.annotation.DateTimeFormat;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.support.domain.AbstractPersistable;
import com.sunwayworld.framework.support.domain.Insertable;

/**
 * @author zhengxy@sunwayworld.com 2023年07月31日
 */
@Table("T_EMC_MONITOR_SCHEME_SAMPLING_COST")
public class EmcMonitorSchemeSamplingCostBean extends AbstractPersistable<Long> implements Insertable<Long> {

    @Id
    private Long id;// 主键

    private String monitorTmplId;
    private Long monitorSchemeId;
    private String samplingType;
    private Long samplingCost;
    private String defaultCost;

    private String createdById;// 制单人编码
    private String createdByName;// 制单人名称
    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;// 制单时间
    private String createdByOrgId;// 制单人单位编码
    private String createdByOrgName;// 制单人单位名称

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getMonitorTmplId() {
        return monitorTmplId;
    }

    public void setMonitorTmplId(String monitorTmplId) {
        this.monitorTmplId = monitorTmplId;
    }

    public Long getMonitorSchemeId() {
        return monitorSchemeId;
    }

    public void setMonitorSchemeId(Long monitorSchemeId) {
        this.monitorSchemeId = monitorSchemeId;
    }

    public String getSamplingType() {
        return samplingType;
    }

    public void setSamplingType(String samplingType) {
        this.samplingType = samplingType;
    }

    public Long getSamplingCost() {
        return samplingCost;
    }

    public void setSamplingCost(Long samplingCost) {
        this.samplingCost = samplingCost;
    }

    public String getDefaultCost() {
        return defaultCost;
    }

    public void setDefaultCost(String defaultCost) {
        this.defaultCost = defaultCost;
    }

    @Override
    public String getCreatedById() {
        return createdById;
    }

    @Override
    public void setCreatedById(String createdById) {
        this.createdById = createdById;
    }

    @Override
    public String getCreatedByName() {
        return createdByName;
    }

    @Override
    public void setCreatedByName(String createdByName) {
        this.createdByName = createdByName;
    }

    @Override
    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    @Override
    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }

    @Override
    public String getCreatedByOrgId() {
        return createdByOrgId;
    }

    @Override
    public void setCreatedByOrgId(String createdByOrgId) {
        this.createdByOrgId = createdByOrgId;
    }

    @Override
    public String getCreatedByOrgName() {
        return createdByOrgName;
    }

    @Override
    public void setCreatedByOrgName(String createdByOrgName) {
        this.createdByOrgName = createdByOrgName;
    }
}
