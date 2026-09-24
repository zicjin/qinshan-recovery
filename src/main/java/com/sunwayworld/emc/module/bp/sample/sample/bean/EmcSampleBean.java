package com.sunwayworld.emc.module.bp.sample.sample.bean;

import java.time.LocalDateTime;

import com.alibaba.fastjson.annotation.JSONField;
import org.springframework.format.annotation.DateTimeFormat;

import com.sunwayworld.emc.framework.constant.EmcBizConstant;
import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.NotNull;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractAuditable;
import com.sunwayworld.framework.support.domain.Auditable;

/**
 * @author zhangsy@sunwayworld.com
 * @date 2019-10-14
 */

@Table("T_EMC_SAMPLE")
    public class EmcSampleBean extends AbstractAuditable<Long>implements Auditable<Long> {

    @Transient
    private static final long serialVersionUID = 2345464087581210775L;

    @Id
    private Long id;// 主键

    private String sampleNo;// 采样单号

    private Long projId;// 项目编码

    private String projNo;// 项目编号

    private String monitorTmplId;// 监测模板ID

    private String orderCategory;// 样品类型

    private String source;// 来源

    @NotNull(defaultValue = EmcBizConstant.SAMPLE_STATUS_DRAFT)
    private String status;// 状态：（未接收、已接收）

    @NotNull(defaultValue = EmcBizConstant.SAMPLE_STATUS_DRAFT)
    private String handStatus;//交接状态

    private String remark;// 备注

    private Long propertyTmplId;// 属性模板编码

    private String reportConfigLineId; // 报表配置 id

    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime sepSampleTime; // 分样时间

    private String acceptedUserId; // 接收人编码

    private String accepteUserName; // 接收人

    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime acceptedDate; // 接收日期

    private String acceptedTime; // 接收时间

    private String mobileId; // 移动端创建的采样单ID

    private String sampleCheck; // 样品检查
    private String usedContainer; // 样品使用容器符合要求
    private String storageValid; // 样品保存温度符合要求
    private String exchangeValid; // 样品在有效期内交换
    private String samplingValid; // 样品采样量符合要求
    private String markValid; // 标识符合要求
    private String containerValid; // 采样容器无破损
    private String qcUserId; // 质控人编码
    private String qcUserName; // 质控人名称
    private String calibrationId; // 仪器校准ID
    private String caliReportLineId; // 仪器校准报表配置ID
    private String receiveReportLineId; // 交接单报表配置ID
    private String printFlag; // 打印标记

    private String createdById;// 制单人编码

    private String createdByName;// 制单人名称

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;// 制单时间

    private String createdByOrgId;// 制单人单位编码

    private String createdByOrgName;// 制单人单位名称

    private String orgId;// 所属单位编码

    private String orgName;// 所属单位名称

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAcceptedTime() {
        return acceptedTime;
    }

    public void setAcceptedTime(String acceptedTime) {
        this.acceptedTime = acceptedTime;
    }

    public String getSampleNo() {
        return sampleNo;
    }

    public void setSampleNo(String sampleNo) {
        this.sampleNo = sampleNo;
    }

    public Long getProjId() {
        return projId;
    }

    public void setProjId(Long projId) {
        this.projId = projId;
    }

    public String getProjNo() {
        return projNo;
    }

    public void setProjNo(String projNo) {
        this.projNo = projNo;
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

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Long getPropertyTmplId() {
        return propertyTmplId;
    }

    public void setPropertyTmplId(Long propertyTmplId) {
        this.propertyTmplId = propertyTmplId;
    }

    public String getReportConfigLineId() {
        return reportConfigLineId;
    }

    public void setReportConfigLineId(String reportConfigLineId) {
        this.reportConfigLineId = reportConfigLineId;
    }

    public LocalDateTime getSepSampleTime() {
        return sepSampleTime;
    }

    public void setSepSampleTime(LocalDateTime sepSampleTime) {
        this.sepSampleTime = sepSampleTime;
    }

    public String getAcceptedUserId() {
        return acceptedUserId;
    }

    public void setAcceptedUserId(String acceptedUserId) {
        this.acceptedUserId = acceptedUserId;
    }

    public String getAccepteUserName() {
        return accepteUserName;
    }

    public void setAccepteUserName(String accepteUserName) {
        this.accepteUserName = accepteUserName;
    }

    public LocalDateTime getAcceptedDate() {
        return acceptedDate;
    }

    public void setAcceptedDate(LocalDateTime acceptedDate) {
        this.acceptedDate = acceptedDate;
    }

    public String getMobileId() {
        return mobileId;
    }

    public void setMobileId(String mobileId) {
        this.mobileId = mobileId;
    }

    public String getSampleCheck() {
        return sampleCheck;
    }

    public void setSampleCheck(String sampleCheck) {
        this.sampleCheck = sampleCheck;
    }

    public String getUsedContainer() {
        return usedContainer;
    }

    public void setUsedContainer(String usedContainer) {
        this.usedContainer = usedContainer;
    }

    public String getStorageValid() {
        return storageValid;
    }

    public void setStorageValid(String storageValid) {
        this.storageValid = storageValid;
    }

    public String getExchangeValid() {
        return exchangeValid;
    }

    public void setExchangeValid(String exchangeValid) {
        this.exchangeValid = exchangeValid;
    }

    public String getSamplingValid() {
        return samplingValid;
    }

    public void setSamplingValid(String samplingValid) {
        this.samplingValid = samplingValid;
    }

    public String getMarkValid() {
        return markValid;
    }

    public void setMarkValid(String markValid) {
        this.markValid = markValid;
    }

    public String getContainerValid() {
        return containerValid;
    }

    public void setContainerValid(String containerValid) {
        this.containerValid = containerValid;
    }

    public String getQcUserId() {
        return qcUserId;
    }

    public void setQcUserId(String qcUserId) {
        this.qcUserId = qcUserId;
    }

    public String getQcUserName() {
        return qcUserName;
    }

    public void setQcUserName(String qcUserName) {
        this.qcUserName = qcUserName;
    }

    public String getCalibrationId() {
        return calibrationId;
    }

    public void setCalibrationId(String calibrationId) {
        this.calibrationId = calibrationId;
    }

    public String getCaliReportLineId() {
        return caliReportLineId;
    }

    public void setCaliReportLineId(String caliReportLineId) {
        this.caliReportLineId = caliReportLineId;
    }

    public String getReceiveReportLineId() {
        return receiveReportLineId;
    }

    public void setReceiveReportLineId(String receiveReportLineId) {
        this.receiveReportLineId = receiveReportLineId;
    }

    public String getPrintFlag() {
        return printFlag;
    }

    public void setPrintFlag(String printFlag) {
        this.printFlag = printFlag;
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

	public String getOrgId() {
		return orgId;
	}

	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}

	public String getOrgName() {
		return orgName;
	}

	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}

    public String getHandStatus() {
        return handStatus;
    }

    public void setHandStatus(String handStatus) {
        this.handStatus = handStatus;
    }


    @NotNull(defaultValue="0")
    private String acceptStatus;
    @NotNull(defaultValue="0")
    private String assignStatus;
    private String undoReason;
    private String undoFlag;
    private String sampleSceneUserId;
    private String sampleSceneUserName;

    public String getUndoReason() {
        return this.undoReason;
    }
    public void setUndoReason(String undoReason) {
        this.undoReason = undoReason;
    }
    public String getUndoFlag() {
        return this.undoFlag;
    }
    public void setUndoFlag(String undoFlag) {
        this.undoFlag = undoFlag;
    }
    public String getAcceptStatus() {
        return this.acceptStatus;
    }
    public void setAcceptStatus(String acceptStatus) {
        this.acceptStatus = acceptStatus;
    }
    public String getAssignStatus() {
        return this.assignStatus;
    }
    public void setAssignStatus(String assignStatus) {
        this.assignStatus = assignStatus;
    }
    public String getSampleSceneUserId() {
        return this.sampleSceneUserId;
    }
    public void setSampleSceneUserId(String sampleSceneUserId) {
        this.sampleSceneUserId = sampleSceneUserId;
    }
    public String getSampleSceneUserName() {
        return this.sampleSceneUserName;
    }
    public void setSampleSceneUserName(String sampleSceneUserName) {
        this.sampleSceneUserName = sampleSceneUserName;
    }
}
