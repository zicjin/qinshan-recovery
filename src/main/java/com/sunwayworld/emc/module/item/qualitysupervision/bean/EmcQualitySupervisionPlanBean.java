package com.sunwayworld.emc.module.item.qualitysupervision.bean;

import java.time.LocalDateTime;

import com.alibaba.fastjson.annotation.JSONField;
import org.springframework.format.annotation.DateTimeFormat;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractPersistable;
import com.sunwayworld.framework.support.domain.Insertable;

/**
 * 质量监督计划
 * @author zhangft@sunwayworld.com
 * @date 2019-12-10
 */
@Table("T_EMC_QUALITY_SUPERVISION_PLAN")
public class EmcQualitySupervisionPlanBean extends AbstractPersistable<Long> implements Insertable<Long> {

    @Transient
    private static final long serialVersionUID = -4142715016069433823L;

    @Id
    private Long id;// 主键

    private Long supervisionId;// 监督编号

    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime planTime;// 拟监督时间

    private String planProject;// 具体监督项目

    private String supervisorName;// 监督员

    private String supervisedOrgName;// 被监督科室

    private String supervisedUserName;// 被监督人

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

    public Long getSupervisionId() {
        return supervisionId;
    }

    public void setSupervisionId(Long supervisionId) {
        this.supervisionId = supervisionId;
    }

    public LocalDateTime getPlanTime() {
        return planTime;
    }

    public void setPlanTime(LocalDateTime planTime) {
        this.planTime = planTime;
    }

    public String getPlanProject() {
        return planProject;
    }

    public void setPlanProject(String planProject) {
        this.planProject = planProject;
    }

    public String getSupervisorName() {
        return supervisorName;
    }

    public void setSupervisorName(String supervisorName) {
        this.supervisorName = supervisorName;
    }

    public String getSupervisedOrgName() {
        return supervisedOrgName;
    }

    public void setSupervisedOrgName(String supervisedOrgName) {
        this.supervisedOrgName = supervisedOrgName;
    }

    public String getSupervisedUserName() {
        return supervisedUserName;
    }

    public void setSupervisedUserName(String supervisedUserName) {
        this.supervisedUserName = supervisedUserName;
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
