/*
 * Decompiled with CFR.
 */
package com.sunwayworld.emc.module.bp.project.projscheme.bean;

import com.alibaba.fastjson.annotation.JSONField;
import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractPersistable;
import com.sunwayworld.framework.support.domain.Insertable;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

@Table(value="T_EMC_PROJ_SCHEME")
public class EmcProjSchemeBean
extends AbstractPersistable<Long>
implements Insertable<Long> {
    @Transient
    private static final long serialVersionUID = 8013335846960248120L;
    @Id
    private Long id;
    private Long projId;
    private String folderNo;
    private String monitorPointCategoryId;
    private String monitorPointCategoryName;
    private Long monitorPointId;
    private String monitorPointNo;
    private String monitorPointName;
    private String monitorTmplId;
    private Long cycle;
    private Long frequency;
    private String sampleOrgId;
    private String remark;
    private String referFolder;
    private Long erpGbId;
    private Long sortNum;
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

    public Long getProjId() {
        return this.projId;
    }

    public void setProjId(Long projId) {
        this.projId = projId;
    }

    public String getFolderNo() {
        return this.folderNo;
    }

    public void setFolderNo(String folderNo) {
        this.folderNo = folderNo;
    }

    public String getMonitorPointCategoryId() {
        return this.monitorPointCategoryId;
    }

    public void setMonitorPointCategoryId(String monitorPointCategoryId) {
        this.monitorPointCategoryId = monitorPointCategoryId;
    }

    public String getMonitorPointCategoryName() {
        return this.monitorPointCategoryName;
    }

    public void setMonitorPointCategoryName(String monitorPointCategoryName) {
        this.monitorPointCategoryName = monitorPointCategoryName;
    }

    public Long getMonitorPointId() {
        return this.monitorPointId;
    }

    public void setMonitorPointId(Long monitorPointId) {
        this.monitorPointId = monitorPointId;
    }

    public String getMonitorPointNo() {
        return this.monitorPointNo;
    }

    public void setMonitorPointNo(String monitorPointNo) {
        this.monitorPointNo = monitorPointNo;
    }

    public String getMonitorPointName() {
        return this.monitorPointName;
    }

    public void setMonitorPointName(String monitorPointName) {
        this.monitorPointName = monitorPointName;
    }

    public String getMonitorTmplId() {
        return this.monitorTmplId;
    }

    public void setMonitorTmplId(String monitorTmplId) {
        this.monitorTmplId = monitorTmplId;
    }

    public Long getCycle() {
        return this.cycle;
    }

    public void setCycle(Long cycle) {
        this.cycle = cycle;
    }

    public Long getFrequency() {
        return this.frequency;
    }

    public void setFrequency(Long frequency) {
        this.frequency = frequency;
    }

    public String getSampleOrgId() {
        return this.sampleOrgId;
    }

    public void setSampleOrgId(String sampleOrgId) {
        this.sampleOrgId = sampleOrgId;
    }

    public String getRemark() {
        return this.remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getReferFolder() {
        return this.referFolder;
    }

    public void setReferFolder(String referFolder) {
        this.referFolder = referFolder;
    }

    public Long getErpGbId() {
        return this.erpGbId;
    }

    public void setErpGbId(Long erpGbId) {
        this.erpGbId = erpGbId;
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

    public Long getSortNum() {
        return this.sortNum;
    }

    public void setSortNum(Long sortNum) {
        this.sortNum = sortNum;
    }
}

