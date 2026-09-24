package com.sunwayworld.emc.module.bp.report.advisory.bean;

import java.time.LocalDateTime;

import com.alibaba.fastjson.annotation.JSONField;
import org.springframework.format.annotation.DateTimeFormat;

import com.sunwayworld.framework.constant.Constant;
import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.NotNull;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractAuditable;
import com.sunwayworld.framework.support.domain.Auditable;

/**
 * 咨询报告表
 * @author zhengxy@sunwayworld.com 2022年08月29日
 */
@Table("T_EMC_ADVISORY_REPORT")
public class EmcAdvisoryReportBean extends AbstractAuditable<Long> implements Auditable<Long> {

    @Transient
    private static final long serialVersionUID = -5649634725224620919L;

    @Id
    private Long id;  // 主键
    private Long projId;  // 项目ID
    private Long monitorTmplId;  // 监测模板ID
    private String orderCategory;  // 样品类型
    private String remark;  // 备注
    private String status;  // 状态
    @NotNull(defaultValue = Constant.NO)
    private String discuss; // 是否上会
    @NotNull(defaultValue = Constant.NO)
    private String revise; // 是否修订

    private String createdById;// 制单人编码
    private String createdByName;// 制单人名称
    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;// 制单时间
    private String createdByOrgId;// 制单人单位编码
    private String createdByOrgName;// 制单人单位名称
    private String reportNo;// 报告编号
    private String projType;// 项目类型
    private String projNo;// 项目编号
    private Long bizCategoryId;// 业务类别ID
    private String bizClassId;// 业务分类ID

    public String getBizClassId() {
        return bizClassId;
    }

    public void setBizClassId(String bizClassId) {
        this.bizClassId = bizClassId;
    }

    public Long getBizCategoryId() {
        return bizCategoryId;
    }

    public void setBizCategoryId(Long bizCategoryId) {
        this.bizCategoryId = bizCategoryId;
    }

    public String getReportNo() {
        return reportNo;
    }

    public void setReportNo(String reportNo) {
        this.reportNo = reportNo;
    }

    public String getProjType() {
        return projType;
    }

    public void setProjType(String projType) {
        this.projType = projType;
    }

    public String getProjNo() {
        return projNo;
    }

    public void setProjNo(String projNo) {
        this.projNo = projNo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProjId() {
        return projId;
    }

    public void setProjId(Long projId) {
        this.projId = projId;
    }

    public Long getMonitorTmplId() {
        return monitorTmplId;
    }

    public void setMonitorTmplId(Long monitorTmplId) {
        this.monitorTmplId = monitorTmplId;
    }

    public String getOrderCategory() {
        return orderCategory;
    }

    public void setOrderCategory(String orderCategory) {
        this.orderCategory = orderCategory;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDiscuss() {
        return discuss;
    }

    public void setDiscuss(String discuss) {
        this.discuss = discuss;
    }

    public String getRevise() {
        return revise;
    }

    public void setRevise(String revise) {
        this.revise = revise;
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
