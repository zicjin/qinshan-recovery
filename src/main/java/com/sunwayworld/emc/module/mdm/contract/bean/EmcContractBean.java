package com.sunwayworld.emc.module.mdm.contract.bean;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.alibaba.fastjson.annotation.JSONField;
import org.springframework.format.annotation.DateTimeFormat;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.NotNull;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractAuditable;
import com.sunwayworld.framework.support.domain.Auditable;

/**
 * @author jinx@sunwayworld.com
 * @date 2020-04-03
 */
@Table("T_EMC_CONTRACT")
public class EmcContractBean extends AbstractAuditable<Long> implements Auditable<Long> {

    @Transient
    private static final long serialVersionUID = -8707994197308217260L;

    @Id
    private Long id;// 主键

    private String entrustOrg;// 委托单位

    private String contractName;// 合同名称

    private String contractNo; // 合同编号

    private String totalPrice;// 总价

    private String contraceSource;// 合同来源

    private String monitorPurpose;// 监测目的

    private String monitorOrder;// 监测样品

    private String cmaf;// 资质认定

    private String contractReport;// 报告

    private String otherInfo;// 其他

    private String reviewer;// 评审人员

    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate reviewDate;// 评审日期

    private String reviewAddress;// 评审地点

    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate beginDate;// 合同开始日期

    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;// 合同结束日期

    private String reviewOpinion;// 评审组意见

    private String year;// 合同年份

    private String reviewReportLineId; // 评审记录模板ID

    private String quotationId;//报价单id

    private String createdById;// 制单人编码

    private String createdByName;// 制单人名称

    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;// 制单时间

    private String createdByOrgId;// 制单人单位编码

    private String createdByOrgName;// 制单人单位名称

    @NotNull(defaultValue = "draft")
    private String processStatus;// 流程状态

    //------------------------------------------------------上海水文添加字段记录审核步骤的意见和日期
    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime qualityTime;// 质量审核日期
    private String qualityUser;// 质量审核人编码
    private String qualityUsername;// 质量审核人
    private String qualityComment;// 质量审核意见

    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime technologyTime;// 技术审核日期
    private String technologyUser;//技术审核人编码
    private String technologyUsername;//技术审核人
    private String technologyComment;//技术审核意见

    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime laboratoryTime;// （分）中心(实验室)负责人审核日期
    private String laboratoryUser;// （分）中心(实验室)负责人审核人编码
    private String laboratoryUsername;// （分）中心(实验室)负责人审核人
    private String laboratoryComment;// （分）中心(实验室)负责人审核意见
    //------------------------------------------------------上海水文添加字段记录审核步骤的意见和日期

    private String achieveCategory;//业绩分类(江苏新锐)

    private Long erpGbId; // ERP合同ID
    private String salerName; // 销售名称
    private String salerPhone; // 销售电话

    private String status; // 状态

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getEntrustOrg() {
        return entrustOrg;
    }

    public void setEntrustOrg(String entrustOrg) {
        this.entrustOrg = entrustOrg;
    }

    public String getContractName() {
        return contractName;
    }

    public void setContractName(String contractName) {
        this.contractName = contractName;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getContraceSource() {
        return contraceSource;
    }

    public void setContraceSource(String contraceSource) {
        this.contraceSource = contraceSource;
    }

    public String getMonitorPurpose() {
        return monitorPurpose;
    }

    public void setMonitorPurpose(String monitorPurpose) {
        this.monitorPurpose = monitorPurpose;
    }

    public String getMonitorOrder() {
        return monitorOrder;
    }

    public void setMonitorOrder(String monitorOrder) {
        this.monitorOrder = monitorOrder;
    }

    public String getCmaf() {
        return cmaf;
    }

    public void setCmaf(String cmaf) {
        this.cmaf = cmaf;
    }

    public String getContractReport() {
        return contractReport;
    }

    public void setContractReport(String contractReport) {
        this.contractReport = contractReport;
    }

    public String getOtherInfo() {
        return otherInfo;
    }

    public void setOtherInfo(String otherInfo) {
        this.otherInfo = otherInfo;
    }

    public String getReviewer() {
        return reviewer;
    }

    public void setReviewer(String reviewer) {
        this.reviewer = reviewer;
    }

    public LocalDate getReviewDate() {
        return reviewDate;
    }

    public void setReviewDate(LocalDate reviewDate) {
        this.reviewDate = reviewDate;
    }

    public String getReviewAddress() {
        return reviewAddress;
    }

    public void setReviewAddress(String reviewAddress) {
        this.reviewAddress = reviewAddress;
    }

    public LocalDate getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(LocalDate beginDate) {
        this.beginDate = beginDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public String getReviewOpinion() {
        return reviewOpinion;
    }

    public void setReviewOpinion(String reviewOpinion) {
        this.reviewOpinion = reviewOpinion;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getReviewReportLineId() {
        return reviewReportLineId;
    }

    public void setReviewReportLineId(String reviewReportLineId) {
        this.reviewReportLineId = reviewReportLineId;
    }

    public String getQuotationId() {
        return quotationId;
    }

    public void setQuotationId(String quotationId) {
        this.quotationId = quotationId;
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

    public String getProcessStatus() {
        return processStatus;
    }

    public void setProcessStatus(String processStatus) {
        this.processStatus = processStatus;
    }

    public String getContractNo() {
        return contractNo;
    }

    public void setContractNo(String contractNo) {
        this.contractNo = contractNo;
    }

	public LocalDateTime getQualityTime() {
		return qualityTime;
	}

	public void setQualityTime(LocalDateTime qualityTime) {
		this.qualityTime = qualityTime;
	}

	public String getQualityComment() {
		return qualityComment;
	}

	public void setQualityComment(String qualityComment) {
		this.qualityComment = qualityComment;
	}

	public String getTechnologyComment() {
		return technologyComment;
	}

	public void setTechnologyComment(String technologyComment) {
		this.technologyComment = technologyComment;
	}

	public String getLaboratoryComment() {
		return laboratoryComment;
	}

	public void setLaboratoryComment(String laboratoryComment) {
		this.laboratoryComment = laboratoryComment;
	}

	public LocalDateTime getTechnologyTime() {
		return technologyTime;
	}

	public void setTechnologyTime(LocalDateTime technologyTime) {
		this.technologyTime = technologyTime;
	}

	public LocalDateTime getLaboratoryTime() {
		return laboratoryTime;
	}

	public void setLaboratoryTime(LocalDateTime laboratoryTime) {
		this.laboratoryTime = laboratoryTime;
	}

	public String getQualityUser() {
		return qualityUser;
	}

	public void setQualityUser(String qualityUser) {
		this.qualityUser = qualityUser;
	}

	public String getQualityUsername() {
		return qualityUsername;
	}

	public void setQualityUsername(String qualityUsername) {
		this.qualityUsername = qualityUsername;
	}

	public String getTechnologyUser() {
		return technologyUser;
	}

	public void setTechnologyUser(String technologyUser) {
		this.technologyUser = technologyUser;
	}

	public String getTechnologyUsername() {
		return technologyUsername;
	}

	public void setTechnologyUsername(String technologyUsername) {
		this.technologyUsername = technologyUsername;
	}

	public String getLaboratoryUser() {
		return laboratoryUser;
	}

	public void setLaboratoryUser(String laboratoryUser) {
		this.laboratoryUser = laboratoryUser;
	}

	public String getLaboratoryUsername() {
		return laboratoryUsername;
	}

	public void setLaboratoryUsername(String laboratoryUsername) {
		this.laboratoryUsername = laboratoryUsername;
	}

    public String getAchieveCategory() {
        return achieveCategory;
    }

    public void setAchieveCategory(String achieveCategory) {
        this.achieveCategory = achieveCategory;
    }

    public Long getErpGbId() {
        return erpGbId;
    }

    public void setErpGbId(Long erpGbId) {
        this.erpGbId = erpGbId;
    }

    public String getSalerName() {
        return salerName;
    }

    public void setSalerName(String salerName) {
        this.salerName = salerName;
    }

    public String getSalerPhone() {
        return salerPhone;
    }

    public void setSalerPhone(String salerPhone) {
        this.salerPhone = salerPhone;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
