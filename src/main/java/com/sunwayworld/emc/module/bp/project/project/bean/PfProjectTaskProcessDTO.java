package com.sunwayworld.emc.module.bp.project.project.bean;

import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import com.alibaba.fastjson.annotation.JSONField;

public class PfProjectTaskProcessDTO {
    private String projNo;// 项目编号

    private String projName;// 项目名称

    private String biddername;// 销售

    private String orderCategory;// 样品类型
    
    private String detectionMetrics;//检测指标
    
    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;// 开案日期
    
    private String sampleAssign;// 采样指派

    private String sampleBegin;// 采样开始
    
    private String sampleEnd;// 采样结束
    
    private String sampleAudit;// 采样审核
    
    private String sampleHandover;// 样品交接
    
    private String taskAssign;// 任务分配

    private String physAudit;// 理化审核

    private String metalAudit;// 金属审核
    
    private String organicAudit;// 有机审核

    private String diohyingAudit;// 二噁英审核

    private String reportEdit;// 报告编制
    
    private String analyzeAudit;// 分析审核


	public String getProjNo() {
		return projNo;
	}

	public void setProjNo(String projNo) {
		this.projNo = projNo;
	}

	public String getProjName() {
		return projName;
	}

	public void setProjName(String projName) {
		this.projName = projName;
	}

	public String getBiddername() {
		return biddername;
	}

	public void setBiddername(String biddername) {
		this.biddername = biddername;
	}

	public String getOrderCategory() {
		return orderCategory;
	}

	public void setOrderCategory(String orderCategory) {
		this.orderCategory = orderCategory;
	}

	public String getDetectionMetrics() {
		return detectionMetrics;
	}

	public void setDetectionMetrics(String detectionMetrics) {
		this.detectionMetrics = detectionMetrics;
	}

	public String getSampleAssign() {
		return sampleAssign;
	}

	public void setSampleAssign(String sampleAssign) {
		this.sampleAssign = sampleAssign;
	}

	public String getSampleBegin() {
		return sampleBegin;
	}

	public void setSampleBegin(String sampleBegin) {
		this.sampleBegin = sampleBegin;
	}

	public String getSampleEnd() {
		return sampleEnd;
	}

	public void setSampleEnd(String sampleEnd) {
		this.sampleEnd = sampleEnd;
	}

	public String getSampleAudit() {
		return sampleAudit;
	}

	public void setSampleAudit(String sampleAudit) {
		this.sampleAudit = sampleAudit;
	}

	public String getSampleHandover() {
		return sampleHandover;
	}

	public void setSampleHandover(String sampleHandover) {
		this.sampleHandover = sampleHandover;
	}

	public String getTaskAssign() {
		return taskAssign;
	}

	public void setTaskAssign(String taskAssign) {
		this.taskAssign = taskAssign;
	}

	public String getPhysAudit() {
		return physAudit;
	}

	public void setPhysAudit(String physAudit) {
		this.physAudit = physAudit;
	}

	public String getMetalAudit() {
		return metalAudit;
	}

	public void setMetalAudit(String metalAudit) {
		this.metalAudit = metalAudit;
	}

	public String getOrganicAudit() {
		return organicAudit;
	}

	public void setOrganicAudit(String organicAudit) {
		this.organicAudit = organicAudit;
	}

	public String getDiohyingAudit() {
		return diohyingAudit;
	}

	public void setDiohyingAudit(String diohyingAudit) {
		this.diohyingAudit = diohyingAudit;
	}

	public String getReportEdit() {
		return reportEdit;
	}

	public void setReportEdit(String reportEdit) {
		this.reportEdit = reportEdit;
	}

    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }

	public String getAnalyzeAudit() {
		return analyzeAudit;
	}

	public void setAnalyzeAudit(String analyzeAudit) {
		this.analyzeAudit = analyzeAudit;
	}
    
}
