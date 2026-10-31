package com.sunwayworld.emc.module.item.complaint.bean;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.alibaba.fastjson.annotation.JSONField;
import org.springframework.format.annotation.DateTimeFormat;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractAuditable;

/**
 * @author guok@sunwayworld.com
 * @date 2019-12-13
 */

@Table("T_EMC_COMPLAINT")
public class EmcComplaintBean extends AbstractAuditable<Long> {
    @Transient
    private static final long serialVersionUID = -1128765651644222308L;

    @Id
    private Long id;// 主键
    private String qualityControlNo;// 质控编号
    private String complaintOrg;// 投诉单位
    private String complaintUser;// 投诉人
    private String complaintMethod;// 投诉方式
    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate complaintTime;// 投诉时间
    private String orderNo;// 样品编号
    private String classification;// 分类情况
    private String complaintContent;// 投诉内容
    private String solveProblem;// 要解决的问题
    private String otherData;// 其他资料
    private String responsibleLeader;// 负责人
    private String inputUser;// 录入人
    private String surveyMember;// 调查组成员
    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate surveyStartTime;// 调查开始时间
    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate surveyEndTime;// 调查结束时间
    private String surveyMethod;// 调查方式
    private String surveyResult;// 调查结果
    private String processResult;// 处理结果
    private String remark;// 备注
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

    public String getQualityControlNo() {
        return qualityControlNo;
    }

    public void setQualityControlNo(String qualityControlNo) {
        this.qualityControlNo = qualityControlNo;
    }

    public String getComplaintOrg() {
        return complaintOrg;
    }

    public void setComplaintOrg(String complaintOrg) {
        this.complaintOrg = complaintOrg;
    }

    public String getComplaintUser() {
        return complaintUser;
    }

    public void setComplaintUser(String complaintUser) {
        this.complaintUser = complaintUser;
    }

    public String getComplaintMethod() {
        return complaintMethod;
    }

    public void setComplaintMethod(String complaintMethod) {
        this.complaintMethod = complaintMethod;
    }

    public LocalDate getComplaintTime() {
        return complaintTime;
    }

    public void setComplaintTime(LocalDate complaintTime) {
        this.complaintTime = complaintTime;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getClassification() {
        return classification;
    }

    public void setClassification(String classification) {
        this.classification = classification;
    }

    public String getComplaintContent() {
        return complaintContent;
    }

    public void setComplaintContent(String complaintContent) {
        this.complaintContent = complaintContent;
    }

    public String getSolveProblem() {
        return solveProblem;
    }

    public void setSolveProblem(String solveProblem) {
        this.solveProblem = solveProblem;
    }

    public String getOtherData() {
        return otherData;
    }

    public void setOtherData(String otherData) {
        this.otherData = otherData;
    }

    public String getResponsibleLeader() {
        return responsibleLeader;
    }

    public void setResponsibleLeader(String responsibleLeader) {
        this.responsibleLeader = responsibleLeader;
    }

    public String getInputUser() {
        return inputUser;
    }

    public void setInputUser(String inputUser) {
        this.inputUser = inputUser;
    }

    public String getSurveyMember() {
        return surveyMember;
    }

    public void setSurveyMember(String surveyMember) {
        this.surveyMember = surveyMember;
    }

    public LocalDate getSurveyStartTime() {
        return surveyStartTime;
    }

    public void setSurveyStartTime(LocalDate surveyStartTime) {
        this.surveyStartTime = surveyStartTime;
    }

    public LocalDate getSurveyEndTime() {
        return surveyEndTime;
    }

    public void setSurveyEndTime(LocalDate surveyEndTime) {
        this.surveyEndTime = surveyEndTime;
    }

    public String getSurveyMethod() {
        return surveyMethod;
    }

    public void setSurveyMethod(String surveyMethod) {
        this.surveyMethod = surveyMethod;
    }

    public String getSurveyResult() {
        return surveyResult;
    }

    public void setSurveyResult(String surveyResult) {
        this.surveyResult = surveyResult;
    }

    public String getProcessResult() {
        return processResult;
    }

    public void setProcessResult(String processResult) {
        this.processResult = processResult;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
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
