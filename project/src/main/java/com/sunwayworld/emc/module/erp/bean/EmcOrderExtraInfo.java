package com.sunwayworld.emc.module.erp.bean;

import java.io.Serializable;

/**
 * @author zhengxy@sunwayworld.com 2023年09月18日
 */
public class EmcOrderExtraInfo implements Serializable {

    private static final long serialVersionUID = -8493161234341817474L;

    private Boolean delFlag;
    private Long id;
    private Long contractGbId;
    private Long orderGbId;
    private Long relateOut;
    private Integer samplingType;
    private Integer samplingStatus;
    private Integer hasReceiveFinishRecord;
    private Integer reportType;
    private String reportFee;
    private Integer securityLevel;
    private String parentIndustryCode;
    private String parentIndustryName;
    private Long parentIndustryGbId;
    private String currentIndustryCode;
    private String currentIndustryName;
    private Long currentIndustryGbId;
    private String childIndustryCode;
    private String childIndustryName;
    private Long childIndustryGbId;
    private String labNumber;
    private Integer workPoint;
    private String laboratory;
    private Integer merchantId;

    public Boolean getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(Boolean delFlag) {
        this.delFlag = delFlag;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getContractGbId() {
        return contractGbId;
    }

    public void setContractGbId(Long contractGbId) {
        this.contractGbId = contractGbId;
    }

    public Long getOrderGbId() {
        return orderGbId;
    }

    public void setOrderGbId(Long orderGbId) {
        this.orderGbId = orderGbId;
    }

    public Long getRelateOut() {
        return relateOut;
    }

    public void setRelateOut(Long relateOut) {
        this.relateOut = relateOut;
    }

    public Integer getSamplingType() {
        return samplingType;
    }

    public void setSamplingType(Integer samplingType) {
        this.samplingType = samplingType;
    }

    public Integer getSamplingStatus() {
        return samplingStatus;
    }

    public void setSamplingStatus(Integer samplingStatus) {
        this.samplingStatus = samplingStatus;
    }

    public Integer getHasReceiveFinishRecord() {
        return hasReceiveFinishRecord;
    }

    public void setHasReceiveFinishRecord(Integer hasReceiveFinishRecord) {
        this.hasReceiveFinishRecord = hasReceiveFinishRecord;
    }

    public Integer getReportType() {
        return reportType;
    }

    public void setReportType(Integer reportType) {
        this.reportType = reportType;
    }

    public String getReportFee() {
        return reportFee;
    }

    public void setReportFee(String reportFee) {
        this.reportFee = reportFee;
    }

    public Integer getSecurityLevel() {
        return securityLevel;
    }

    public void setSecurityLevel(Integer securityLevel) {
        this.securityLevel = securityLevel;
    }

    public String getParentIndustryCode() {
        return parentIndustryCode;
    }

    public void setParentIndustryCode(String parentIndustryCode) {
        this.parentIndustryCode = parentIndustryCode;
    }

    public String getParentIndustryName() {
        return parentIndustryName;
    }

    public void setParentIndustryName(String parentIndustryName) {
        this.parentIndustryName = parentIndustryName;
    }

    public Long getParentIndustryGbId() {
        return parentIndustryGbId;
    }

    public void setParentIndustryGbId(Long parentIndustryGbId) {
        this.parentIndustryGbId = parentIndustryGbId;
    }

    public String getCurrentIndustryCode() {
        return currentIndustryCode;
    }

    public void setCurrentIndustryCode(String currentIndustryCode) {
        this.currentIndustryCode = currentIndustryCode;
    }

    public String getCurrentIndustryName() {
        return currentIndustryName;
    }

    public void setCurrentIndustryName(String currentIndustryName) {
        this.currentIndustryName = currentIndustryName;
    }

    public Long getCurrentIndustryGbId() {
        return currentIndustryGbId;
    }

    public void setCurrentIndustryGbId(Long currentIndustryGbId) {
        this.currentIndustryGbId = currentIndustryGbId;
    }

    public String getChildIndustryCode() {
        return childIndustryCode;
    }

    public void setChildIndustryCode(String childIndustryCode) {
        this.childIndustryCode = childIndustryCode;
    }

    public String getChildIndustryName() {
        return childIndustryName;
    }

    public void setChildIndustryName(String childIndustryName) {
        this.childIndustryName = childIndustryName;
    }

    public Long getChildIndustryGbId() {
        return childIndustryGbId;
    }

    public void setChildIndustryGbId(Long childIndustryGbId) {
        this.childIndustryGbId = childIndustryGbId;
    }

    public String getLabNumber() {
        return labNumber;
    }

    public void setLabNumber(String labNumber) {
        this.labNumber = labNumber;
    }

    public Integer getWorkPoint() {
        return workPoint;
    }

    public void setWorkPoint(Integer workPoint) {
        this.workPoint = workPoint;
    }

    public String getLaboratory() {
        return laboratory;
    }

    public void setLaboratory(String laboratory) {
        this.laboratory = laboratory;
    }

    public Integer getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(Integer merchantId) {
        this.merchantId = merchantId;
    }
}
