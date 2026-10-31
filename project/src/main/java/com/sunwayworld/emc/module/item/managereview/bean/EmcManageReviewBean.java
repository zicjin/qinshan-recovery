package com.sunwayworld.emc.module.item.managereview.bean;

import java.time.LocalDateTime;

import com.alibaba.fastjson.annotation.JSONField;
import org.springframework.format.annotation.DateTimeFormat;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractAuditable;
import com.sunwayworld.framework.support.domain.Auditable;

/**
 * 管理评审计划表
 *
 * @author qianc@sunwayworld.com 2019年12月3日
 */
@Table("T_EMC_MANAGE_REVIEW")
public class EmcManageReviewBean extends AbstractAuditable<Long>implements Auditable<Long> {

    @Transient
    private static final long serialVersionUID = 4327397559639222304L;

    @Id
    private Long id;// 主键

    private String qualityControlNo;// 质控编号

    private String reviewPurpose;// 评审目的

    private String reviewScope;// 评审范围

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime reviewTime;// 拟计划评审时间

    private String partakeUser;// 参与人员

    private String reviewAddress;// 评审地点

    private String reviewWay;// 评审方式

    private String reviewContent;// 评审内容

    private String prepareWork;// 准备工作

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime reviewStartTime;// 评审开始时间

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime reviewEndTime;// 评审结束时间

    private String reviewBasis;// 评审依据

    private String reviewDesc;// 评审情况描述

    private String advise;// 整改措施及具体建议

    private String reviewOpinion;// 评审意见

    private String yearTarget;// 下一年度的目标和活动计划

    private String reviewGroupUser;// 评审组成员

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

    public String getReviewPurpose() {
        return reviewPurpose;
    }

    public void setReviewPurpose(String reviewPurpose) {
        this.reviewPurpose = reviewPurpose;
    }

    public String getReviewScope() {
        return reviewScope;
    }

    public void setReviewScope(String reviewScope) {
        this.reviewScope = reviewScope;
    }

    public LocalDateTime getReviewTime() {
        return reviewTime;
    }

    public void setReviewTime(LocalDateTime reviewTime) {
        this.reviewTime = reviewTime;
    }

    public String getPartakeUser() {
        return partakeUser;
    }

    public void setPartakeUser(String partakeUser) {
        this.partakeUser = partakeUser;
    }

    public String getReviewAddress() {
        return reviewAddress;
    }

    public void setReviewAddress(String reviewAddress) {
        this.reviewAddress = reviewAddress;
    }

    public String getReviewWay() {
        return reviewWay;
    }

    public void setReviewWay(String reviewWay) {
        this.reviewWay = reviewWay;
    }

    public String getReviewContent() {
        return reviewContent;
    }

    public void setReviewContent(String reviewContent) {
        this.reviewContent = reviewContent;
    }

    public String getPrepareWork() {
        return prepareWork;
    }

    public void setPrepareWork(String prepareWork) {
        this.prepareWork = prepareWork;
    }

    public LocalDateTime getReviewStartTime() {
        return reviewStartTime;
    }

    public void setReviewStartTime(LocalDateTime reviewStartTime) {
        this.reviewStartTime = reviewStartTime;
    }

    public LocalDateTime getReviewEndTime() {
        return reviewEndTime;
    }

    public void setReviewEndTime(LocalDateTime reviewEndTime) {
        this.reviewEndTime = reviewEndTime;
    }

    public String getReviewBasis() {
        return reviewBasis;
    }

    public void setReviewBasis(String reviewBasis) {
        this.reviewBasis = reviewBasis;
    }

    public String getReviewDesc() {
        return reviewDesc;
    }

    public void setReviewDesc(String reviewDesc) {
        this.reviewDesc = reviewDesc;
    }

    public String getAdvise() {
        return advise;
    }

    public void setAdvise(String advise) {
        this.advise = advise;
    }

    public String getReviewOpinion() {
        return reviewOpinion;
    }

    public void setReviewOpinion(String reviewOpinion) {
        this.reviewOpinion = reviewOpinion;
    }

    public String getYearTarget() {
        return yearTarget;
    }

    public void setYearTarget(String yearTarget) {
        this.yearTarget = yearTarget;
    }

    public String getReviewGroupUser() {
        return reviewGroupUser;
    }

    public void setReviewGroupUser(String reviewGroupUser) {
        this.reviewGroupUser = reviewGroupUser;
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
