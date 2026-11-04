/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.equipt.equipt.bean;

import com.alibaba.fastjson.annotation.JSONField;
import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractInsertable;
import com.sunwayworld.framework.support.domain.Insertable;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

@Table(value="T_CORE_EQUIPT")
public class CoreEquiptBean
extends AbstractInsertable<Long>
implements Insertable<Long> {
    @Transient
    private static final long serialVersionUID = -6380925415678637970L;
    @Id
    private Long id;
    private String equiptNo;
    private String equiptName;
    private String equiptCategory;
    private String equiptIcon;
    private String keyEquipt;
    private String location;
    private String equiptManagerId;
    private String equiptManagerName;
    private String monitorCode;
    private String ip;
    private Long port;
    private Double threshold;
    private String switchStatus;
    private Double planUseTime;
    private Double cost;
    private String createdById;
    private String createdByName;
    @JSONField(format="yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;
    private String createdByOrgId;
    private String createdByOrgName;

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getEquiptNo() {
        return this.equiptNo;
    }

    public void setEquiptNo(String equiptNo) {
        this.equiptNo = equiptNo;
    }

    public String getEquiptName() {
        return this.equiptName;
    }

    public void setEquiptName(String equiptName) {
        this.equiptName = equiptName;
    }

    public String getEquiptCategory() {
        return this.equiptCategory;
    }

    public void setEquiptCategory(String equiptCategory) {
        this.equiptCategory = equiptCategory;
    }

    public String getEquiptIcon() {
        return this.equiptIcon;
    }

    public void setEquiptIcon(String equiptIcon) {
        this.equiptIcon = equiptIcon;
    }

    public String getKeyEquipt() {
        return this.keyEquipt;
    }

    public void setKeyEquipt(String keyEquipt) {
        this.keyEquipt = keyEquipt;
    }

    public String getLocation() {
        return this.location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getEquiptManagerId() {
        return this.equiptManagerId;
    }

    public void setEquiptManagerId(String equiptManagerId) {
        this.equiptManagerId = equiptManagerId;
    }

    public String getEquiptManagerName() {
        return this.equiptManagerName;
    }

    public void setEquiptManagerName(String equiptManagerName) {
        this.equiptManagerName = equiptManagerName;
    }

    public String getMonitorCode() {
        return this.monitorCode;
    }

    public void setMonitorCode(String monitorCode) {
        this.monitorCode = monitorCode;
    }

    public String getIp() {
        return this.ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Long getPort() {
        return this.port;
    }

    public void setPort(Long port) {
        this.port = port;
    }

    public Double getThreshold() {
        return this.threshold;
    }

    public void setThreshold(Double threshold) {
        this.threshold = threshold;
    }

    public String getSwitchStatus() {
        return this.switchStatus;
    }

    public void setSwitchStatus(String switchStatus) {
        this.switchStatus = switchStatus;
    }

    public Double getPlanUseTime() {
        return this.planUseTime;
    }

    public void setPlanUseTime(Double planUseTime) {
        this.planUseTime = planUseTime;
    }

    public Double getCost() {
        return this.cost;
    }

    public void setCost(Double cost) {
        this.cost = cost;
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
}
