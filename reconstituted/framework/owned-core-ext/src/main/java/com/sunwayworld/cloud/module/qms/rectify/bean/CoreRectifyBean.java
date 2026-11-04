/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.rectify.bean;

import com.alibaba.fastjson.annotation.JSONField;
import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.NotNull;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractAuditable;
import com.sunwayworld.framework.support.domain.Auditable;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

@Table(value="T_CORE_RECTIFY")
public class CoreRectifyBean
extends AbstractAuditable<Long>
implements Auditable<Long> {
    @Transient
    private static final long serialVersionUID = -82566196618711043L;
    @Id
    private Long id;
    @NotNull(defaultValue="draft")
    private String processStatus;
    private String createdById;
    private String createdByName;
    @JSONField(format="yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;
    private String createdByOrgId;
    private String createdByOrgName;
    private String rectifyNo;
    private String discoveryOrgId;
    private String discoveryOrgName;
    private String qualityUserId;
    private String qualityUserName;
    private String technicalUserId;
    private String technicalUserName;
    private String leaderUserId;
    private String leaderUserName;
    private String carryUserId;
    private String carryUserName;
    private String remark;

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getProcessStatus() {
        return this.processStatus;
    }

    @Override
    public void setProcessStatus(String processStatus) {
        this.processStatus = processStatus;
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

    public String getRectifyNo() {
        return this.rectifyNo;
    }

    public void setRectifyNo(String rectifyNo) {
        this.rectifyNo = rectifyNo;
    }

    public String getDiscoveryOrgId() {
        return this.discoveryOrgId;
    }

    public void setDiscoveryOrgId(String discoveryOrgId) {
        this.discoveryOrgId = discoveryOrgId;
    }

    public String getDiscoveryOrgName() {
        return this.discoveryOrgName;
    }

    public void setDiscoveryOrgName(String discoveryOrgName) {
        this.discoveryOrgName = discoveryOrgName;
    }

    public String getQualityUserId() {
        return this.qualityUserId;
    }

    public void setQualityUserId(String qualityUserId) {
        this.qualityUserId = qualityUserId;
    }

    public String getQualityUserName() {
        return this.qualityUserName;
    }

    public void setQualityUserName(String qualityUserName) {
        this.qualityUserName = qualityUserName;
    }

    public String getTechnicalUserId() {
        return this.technicalUserId;
    }

    public void setTechnicalUserId(String technicalUserId) {
        this.technicalUserId = technicalUserId;
    }

    public String getTechnicalUserName() {
        return this.technicalUserName;
    }

    public void setTechnicalUserName(String technicalUserName) {
        this.technicalUserName = technicalUserName;
    }

    public String getLeaderUserId() {
        return this.leaderUserId;
    }

    public void setLeaderUserId(String leaderUserId) {
        this.leaderUserId = leaderUserId;
    }

    public String getLeaderUserName() {
        return this.leaderUserName;
    }

    public void setLeaderUserName(String leaderUserName) {
        this.leaderUserName = leaderUserName;
    }

    public String getCarryUserId() {
        return this.carryUserId;
    }

    public void setCarryUserId(String carryUserId) {
        this.carryUserId = carryUserId;
    }

    public String getCarryUserName() {
        return this.carryUserName;
    }

    public void setCarryUserName(String carryUserName) {
        this.carryUserName = carryUserName;
    }

    public String getRemark() {
        return this.remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
