package com.sunwayworld.emc.module.bp.report.report.bean;

import java.time.LocalDateTime;

import com.alibaba.fastjson.annotation.JSONField;
import org.springframework.format.annotation.DateTimeFormat;

import com.sunwayworld.emc.framework.constant.EmcBizConstant;
import com.sunwayworld.framework.constant.Constant;
import com.sunwayworld.framework.data.annotation.Clob;
import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.NotNull;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractAuditable;
import com.sunwayworld.framework.support.domain.Auditable;

/**
 * 报告表
 *
 * @author yuanhh@sunwayworld.com
 * @date 2019-10-18
 */

@Table("T_EMC_REPORT")
public class EmcReportBean extends AbstractAuditable<Long> implements Auditable<Long> {

    @Transient
    private static final long serialVersionUID = 8599630570047743766L;
    @Id
    private Long id;// 主键
    private String reportNo;// 报告号
    private Long projId;// 项目编码
    private String monitorTmplId;// 监测模板ID
    private String orderCategory;// 样品类型
    private Long reportConfigLineId;// 报表配置编码
    @Clob
    private String monitorInfo;// 监测信息
    @Clob
    private String testConclusion;// 检验结论
    @Clob
    private String workCondition;// 工况
    @Clob
    private String remark;// 备注
    @NotNull(defaultValue = EmcBizConstant.REPORT_STATUS_DRAFT)
    private String status;// 状态
    @NotNull(defaultValue = Constant.YES)
    private String ifStatistics;// 是否生成统计值

    private String preface; // 前言
    private String installSituation; // 运行安装情况

    private Long rsReportConfigLineId; // RS报表配置明细ID
    private Long dataSheetId; // 数据表模板ID
    private String stampCategory; // 盖章类型(广分所项目)

    private String ndDisplayMode; // 未检出表示方式

    private String createdById;// 制单人编码
    private String createdByName;// 制单人名称
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;// 制单时间
    private String createdByOrgId;// 制单人单位编码
    private String createdByOrgName;// 制单人单位名称

    private String reportName; // 报告名称

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReportNo() {
        return reportNo;
    }

    public void setReportNo(String reportNo) {
        this.reportNo = reportNo;
    }

    public Long getProjId() {
        return projId;
    }

    public void setProjId(Long projId) {
        this.projId = projId;
    }

    public String getMonitorTmplId() {
        return monitorTmplId;
    }

    public void setMonitorTmplId(String monitorTmplId) {
        this.monitorTmplId = monitorTmplId;
    }

    public String getOrderCategory() {
        return orderCategory;
    }

    public void setOrderCategory(String orderCategory) {
        this.orderCategory = orderCategory;
    }

    public Long getReportConfigLineId() {
        return reportConfigLineId;
    }

    public void setReportConfigLineId(Long reportConfigLineId) {
        this.reportConfigLineId = reportConfigLineId;
    }

    public String getMonitorInfo() {
        return monitorInfo;
    }

    public void setMonitorInfo(String monitorInfo) {
        this.monitorInfo = monitorInfo;
    }

    public String getTestConclusion() {
        return testConclusion;
    }

    public void setTestConclusion(String testConclusion) {
        this.testConclusion = testConclusion;
    }

    public String getWorkCondition() {
        return workCondition;
    }

    public void setWorkCondition(String workCondition) {
        this.workCondition = workCondition;
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

    public String getIfStatistics() {
        return ifStatistics;
    }

    public void setIfStatistics(String ifStatistics) {
        this.ifStatistics = ifStatistics;
    }

    public String getPreface() {
        return preface;
    }

    public void setPreface(String preface) {
        this.preface = preface;
    }

    public String getInstallSituation() {
        return installSituation;
    }

    public void setInstallSituation(String installSituation) {
        this.installSituation = installSituation;
    }

    public Long getRsReportConfigLineId() {
        return rsReportConfigLineId;
    }

    public void setRsReportConfigLineId(Long rsReportConfigLineId) {
        this.rsReportConfigLineId = rsReportConfigLineId;
    }

    public Long getDataSheetId() {
        return dataSheetId;
    }

    public void setDataSheetId(Long dataSheetId) {
        this.dataSheetId = dataSheetId;
    }

    public String getStampCategory() {
        return stampCategory;
    }

    public void setStampCategory(String stampCategory) {
        this.stampCategory = stampCategory;
    }

    public String getNdDisplayMode() {
        return ndDisplayMode;
    }

    public void setNdDisplayMode(String ndDisplayMode) {
        this.ndDisplayMode = ndDisplayMode;
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

    public String getReportName() {
        return reportName;
    }

    public void setReportName(String reportName) {
        this.reportName = reportName;
    }
}
