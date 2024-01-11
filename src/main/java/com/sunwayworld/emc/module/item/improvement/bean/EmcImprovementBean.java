package com.sunwayworld.emc.module.item.improvement.bean;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.alibaba.fastjson.annotation.JSONField;
import org.springframework.format.annotation.DateTimeFormat;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractAuditable;
import com.sunwayworld.framework.support.domain.Auditable;
import com.sunwayworld.framework.support.domain.Insertable;

/**
 * @author guok@sunwayworld.com
 * @date 2019-12-09
 */

@Table("T_EMC_IMPROVEMENT")
public class EmcImprovementBean extends AbstractAuditable<Long> implements Insertable<Long>, Auditable<Long> {
    @Transient
    private static final long serialVersionUID = 5959526305049338728L;

    @Id
    private Long id;
    private String qualityControlNo;// 质控编号
    private String source;// 来源
    private String sourceNo;// 来源编号
    private String responsibleLeader;// 责任人
    private String partakeUser;// 参加人
    private String startReason;// 启动原因
    private String purpose;// 目的
    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startTime;// 拟开始时间
    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endTime;// 拟结束时间
    private String evaluateUser;// 评价人
    private String improveMethod;// 改进方式
    private String improveContent;// 改进内容
    private String actualPartakeUser;// 实际参与人
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

    public String getResponsibleLeader() {
        return responsibleLeader;
    }

    public void setResponsibleLeader(String responsibleLeader) {
        this.responsibleLeader = responsibleLeader;
    }

    public String getPartakeUser() {
        return partakeUser;
    }

    public void setPartakeUser(String partakeUser) {
        this.partakeUser = partakeUser;
    }

    public String getStartReason() {
        return startReason;
    }

    public void setStartReason(String startReason) {
        this.startReason = startReason;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public LocalDate getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDate startTime) {
        this.startTime = startTime;
    }

    public LocalDate getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDate endTime) {
        this.endTime = endTime;
    }

    public String getEvaluateUser() {
        return evaluateUser;
    }

    public void setEvaluateUser(String evaluateUser) {
        this.evaluateUser = evaluateUser;
    }

    public String getImproveMethod() {
        return improveMethod;
    }

    public void setImproveMethod(String improveMethod) {
        this.improveMethod = improveMethod;
    }

    public String getImproveContent() {
        return improveContent;
    }

    public void setImproveContent(String improveContent) {
        this.improveContent = improveContent;
    }

    public String getActualPartakeUser() {
        return actualPartakeUser;
    }

    public void setActualPartakeUser(String actualPartakeUser) {
        this.actualPartakeUser = actualPartakeUser;
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
