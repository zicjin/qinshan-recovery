package com.sunwayworld.emc.module.item.correction.bean;

import java.time.LocalDateTime;

import com.alibaba.fastjson.annotation.JSONField;
import org.springframework.format.annotation.DateTimeFormat;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractAuditable;
import com.sunwayworld.framework.support.domain.Insertable;

/**
 * 纠正措施计划
 * @author zhangft@sunwayworld.com
 * @date 2019-11-25
 */

@Table("T_EMC_CORRECTION")
public class EmcCorrectionBean extends AbstractAuditable<Long> implements Insertable<Long> {

    @Transient
    private static final long serialVersionUID = 6520945593839155831L;

    @Id
    private Long id;// 申请编码

    private String correctionCode;// 纠正措施编码

    private String source;// 来源

    private String sourceNo;// 来源编号

    private String factDescription;// 事实描述

    private String basis;// 依据

    private String responsibleOrgName;// 责任部门

    private String responsibleLeader;// 负责人

    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime discoverDate;// 发现时间

    private String discoverUser;// 发现人

    private String correction;// 纠正措施

    private String completeSituation;// 完成情况

    private String verifyResult;// 验证结果

    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime completeDate;// 预计完成时间

    private String verifyUser;// 验证人

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

    public String getCorrectionCode() {
        return correctionCode;
    }

    public void setCorrectionCode(String correctionCode) {
        this.correctionCode = correctionCode;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getSourceNo() {
        return sourceNo;
    }

    public void setSourceNo(String sourceNo) {
        this.sourceNo = sourceNo;
    }

    public String getFactDescription() {
        return factDescription;
    }

    public void setFactDescription(String factDescription) {
        this.factDescription = factDescription;
    }

    public String getBasis() {
        return basis;
    }

    public void setBasis(String basis) {
        this.basis = basis;
    }

    public String getCorrection() {
        return correction;
    }

    public void setCorrection(String correction) {
        this.correction = correction;
    }

    public String getResponsibleOrgName() {
        return responsibleOrgName;
    }

    public void setResponsibleOrgName(String responsibleOrgName) {
        this.responsibleOrgName = responsibleOrgName;
    }

    public String getResponsibleLeader() {
        return responsibleLeader;
    }

    public void setResponsibleLeader(String responsibleLeader) {
        this.responsibleLeader = responsibleLeader;
    }

    public LocalDateTime getDiscoverDate() {
        return discoverDate;
    }

    public void setDiscoverDate(LocalDateTime discoverDate) {
        this.discoverDate = discoverDate;
    }

    public String getDiscoverUser() {
        return discoverUser;
    }

    public void setDiscoverUser(String discoverUser) {
        this.discoverUser = discoverUser;
    }

    public String getPrecaution() {
        return correction;
    }

    public void setPrecaution(String correction) {
        this.correction = correction;
    }

    public String getCompleteSituation() {
        return completeSituation;
    }

    public void setCompleteSituation(String completeSituation) {
        this.completeSituation = completeSituation;
    }

    public String getVerifyResult() {
        return verifyResult;
    }

    public void setVerifyResult(String verifyResult) {
        this.verifyResult = verifyResult;
    }

    public LocalDateTime getCompleteDate() {
        return completeDate;
    }

    public void setCompleteDate(LocalDateTime completeDate) {
        this.completeDate = completeDate;
    }

    public String getVerifyUser() {
        return verifyUser;
    }

    public void setVerifyUser(String verifyUser) {
        this.verifyUser = verifyUser;
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
