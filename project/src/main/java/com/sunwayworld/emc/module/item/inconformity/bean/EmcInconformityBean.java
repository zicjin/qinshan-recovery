package com.sunwayworld.emc.module.item.inconformity.bean;

import java.time.LocalDateTime;

import com.alibaba.fastjson.annotation.JSONField;
import org.springframework.format.annotation.DateTimeFormat;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractAuditable;
import com.sunwayworld.framework.support.domain.Insertable;

/**
 * 不符合项登记表
 *
 * @author cuikx@sunwayworld.com
 * @date 2019-12-6
 */

@Table("T_EMC_INCONFORMITY")
public class EmcInconformityBean extends AbstractAuditable<Long> implements Insertable<Long> {

    @Transient
    private static final long serialVersionUID = 1537160845884407626L;


    @Id
    private Long id;// 主键

    private String qualityControlNo;// 质控编号

    private String source;// 来源
    private String sourceNo;// 来源编号
    private String factDesc;// 不符合项事实描述
    private String inconformityBasis;// 不符合项依据
    private String correctActivity;// 纠正活动
    private String workCategory;// 不符合工作类型
    private String evaluation;// 不符合项评价
    private String inconformityOrg;// 不符合工作发生部门
    private String findUser;// 发现人

    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime findTime;// 发现时间

    private String orderNo;// 样品编号
    private String correctAction;// 拟采取的纠正措施

    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime completeTime;// 拟完成时间

    private String responsibleUser;// 负责人

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

    public String getFactDesc() {
        return factDesc;
    }

    public void setFactDesc(String factDesc) {
        this.factDesc = factDesc;
    }

    public String getInconformityBasis() {
        return inconformityBasis;
    }

    public void setInconformityBasis(String inconformityBasis) {
        this.inconformityBasis = inconformityBasis;
    }

    public String getCorrectActivity() {
        return correctActivity;
    }

    public void setCorrectActivity(String correctActivity) {
        this.correctActivity = correctActivity;
    }

    public String getWorkCategory() {
        return workCategory;
    }

    public void setWorkCategory(String workCategory) {
        this.workCategory = workCategory;
    }

    public String getEvaluation() {
        return evaluation;
    }

    public void setEvaluation(String evaluation) {
        this.evaluation = evaluation;
    }

    public String getInconformityOrg() {
        return inconformityOrg;
    }

    public void setInconformityOrg(String inconformityOrg) {
        this.inconformityOrg = inconformityOrg;
    }

    public String getFindUser() {
        return findUser;
    }

    public void setFindUser(String findUser) {
        this.findUser = findUser;
    }

    public LocalDateTime getFindTime() {
        return findTime;
    }

    public void setFindTime(LocalDateTime findTime) {
        this.findTime = findTime;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getCorrectAction() {
        return correctAction;
    }

    public void setCorrectAction(String correctAction) {
        this.correctAction = correctAction;
    }

    public LocalDateTime getCompleteTime() {
        return completeTime;
    }

    public void setCompleteTime(LocalDateTime completeTime) {
        this.completeTime = completeTime;
    }

    public String getResponsibleUser() {
        return responsibleUser;
    }

    public void setResponsibleUser(String responsibleUser) {
        this.responsibleUser = responsibleUser;
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
