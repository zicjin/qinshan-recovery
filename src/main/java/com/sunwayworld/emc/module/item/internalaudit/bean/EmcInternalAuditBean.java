package com.sunwayworld.emc.module.item.internalaudit.bean;

import java.time.LocalDateTime;

import com.alibaba.fastjson.annotation.JSONField;
import org.springframework.format.annotation.DateTimeFormat;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractAuditable;
import com.sunwayworld.framework.support.domain.Insertable;

/**
 * 内审计划表
 *
 * @author cuikx@sunwayworld.com
 * @date 2019-11-12
 */

@Table("T_EMC_INTERNAL_AUDIT")
public class EmcInternalAuditBean extends AbstractAuditable<Long> implements Insertable<Long> {

    @Transient
    private static final long serialVersionUID = 9106967888613106199L;

    @Id
    private Long id;// 主键

    private String qualityControlNo;// 质控编号

    private String auditPurpose;// 审核目的

    private String auditBasis;// 审核依据

    private String auditOrg;// 被审核部门

    private String auditGroupUser;// 内审组成员

    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime auditTime;// 拟计划审核时间

    private String auditContent;// 拟计划审核内容

    private String userTrain;// 内审人员培训（必要时）

    private String recordTable;// 记录表格

    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;// 内审开始时间

    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;// 内审结束时间

    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime auditDate;// 审核日期

    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastAuditDate;// 上次审核日期

    private String division;// 审核成员分工及审核部门

    private String auditSituation;// 审核情况综述

    private String inconformity;// 不符合项目归纳分类及分布项

    private String manageSystem;// 受审方的管理体系执行情况的整体综合评价

    private String correct;// 纠正措施或预防措施执行情况

    private String auditRequest;// 审核后续活动要求

    private String auditReview;// 审核综述及审核结论

    private String sendScope;// 发送范围

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

    public String getQualityControlNo() {
        return qualityControlNo;
    }

    public void setQualityControlNo(String qualityControlNo) {
        this.qualityControlNo = qualityControlNo;
    }

    public String getAuditPurpose() {
        return auditPurpose;
    }

    public void setAuditPurpose(String auditPurpose) {
        this.auditPurpose = auditPurpose;
    }

    public String getAuditBasis() {
        return auditBasis;
    }

    public void setAuditBasis(String auditBasis) {
        this.auditBasis = auditBasis;
    }

    public String getAuditOrg() {
        return auditOrg;
    }

    public void setAuditOrg(String auditOrg) {
        this.auditOrg = auditOrg;
    }

    public String getAuditGroupUser() {
        return auditGroupUser;
    }

    public void setAuditGroupUser(String auditGroupUser) {
        this.auditGroupUser = auditGroupUser;
    }

    public LocalDateTime getAuditTime() {
        return auditTime;
    }

    public void setAuditTime(LocalDateTime auditTime) {
        this.auditTime = auditTime;
    }

    public String getAuditContent() {
        return auditContent;
    }

    public void setAuditContent(String auditContent) {
        this.auditContent = auditContent;
    }

    public String getUserTrain() {
        return userTrain;
    }

    public void setUserTrain(String userTrain) {
        this.userTrain = userTrain;
    }

    public String getRecordTable() {
        return recordTable;
    }

    public void setRecordTable(String recordTable) {
        this.recordTable = recordTable;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public LocalDateTime getAuditDate() {
        return auditDate;
    }

    public void setAuditDate(LocalDateTime auditDate) {
        this.auditDate = auditDate;
    }

    public LocalDateTime getLastAuditDate() {
        return lastAuditDate;
    }

    public void setLastAuditDate(LocalDateTime lastAuditDate) {
        this.lastAuditDate = lastAuditDate;
    }

    public String getDivision() {
        return division;
    }

    public void setDivision(String division) {
        this.division = division;
    }

    public String getAuditSituation() {
        return auditSituation;
    }

    public void setAuditSituation(String auditSituation) {
        this.auditSituation = auditSituation;
    }

    public String getInconformity() {
        return inconformity;
    }

    public void setInconformity(String inconformity) {
        this.inconformity = inconformity;
    }

    public String getManageSystem() {
        return manageSystem;
    }

    public void setManageSystem(String manageSystem) {
        this.manageSystem = manageSystem;
    }

    public String getCorrect() {
        return correct;
    }

    public void setCorrect(String correct) {
        this.correct = correct;
    }

    public String getAuditRequest() {
        return auditRequest;
    }

    public void setAuditRequest(String auditRequest) {
        this.auditRequest = auditRequest;
    }

    public String getAuditReview() {
        return auditReview;
    }

    public void setAuditReview(String auditReview) {
        this.auditReview = auditReview;
    }

    public String getSendScope() {
        return sendScope;
    }

    public void setSendScope(String sendScope) {
        this.sendScope = sendScope;
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
