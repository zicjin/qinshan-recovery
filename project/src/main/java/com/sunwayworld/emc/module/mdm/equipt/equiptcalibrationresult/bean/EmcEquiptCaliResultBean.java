package com.sunwayworld.emc.module.mdm.equipt.equiptcalibrationresult.bean;

import java.time.LocalDateTime;

import com.alibaba.fastjson.annotation.JSONField;
import org.springframework.format.annotation.DateTimeFormat;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractAuditable;
import com.sunwayworld.framework.support.domain.Insertable;

/**
 * 校准结果确认记录
 *
 * @author cd@sunwayworld.com
 * @date 2020-09-01
 */
@Table("T_EMC_EQUIPT_CALI_RESULT")
public class EmcEquiptCaliResultBean extends AbstractAuditable<Long> implements Insertable<Long> {

    @Transient
    private static final long serialVersionUID = -82566196618711043L;

    @Id
    private Long id;// 校准结果确认记录id
    private Long equiptId;// 仪器Id
    private String equiptNo;// 站内编号
    private String equiptName;// 仪器名称
    private String specification;// 规格型号
    private String cycle;// 校准周期
    private String unit;// 校准单位
    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime validTime;// 有效期
    private String reportCategory;// 证书报告性质
    private String certNo;// 证书报告编号
    private String caliReason;// 校准依据
    private String caliResult;// 校准结果
    private String claim;// 是否满足要求
    private String uncertainty;// 校准结果不确定度
    private String contentResult;// 根据证书、报告内容确认
    private String reviewerId;// 审核人Id
    private String reviewerName;//审核人名称
    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime reviewDate;// 审核日期
    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime maintDate;//校准日期
    private String remark;//备注

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public Long getEquiptId() {
        return equiptId;
    }

    public void setEquiptId(Long equiptId) {
        this.equiptId = equiptId;
    }

    public String getEquiptNo() {
        return equiptNo;
    }

    public void setEquiptNo(String equiptNo) {
        this.equiptNo = equiptNo;
    }

    public String getEquiptName() {
        return equiptName;
    }

    public void setEquiptName(String equiptName) {
        this.equiptName = equiptName;
    }

    public String getSpecification() {
        return specification;
    }

    public void setSpecification(String specification) {
        this.specification = specification;
    }

    public String getCycle() {
        return cycle;
    }

    public void setCycle(String cycle) {
        this.cycle = cycle;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public LocalDateTime getValidTime() {
        return validTime;
    }

    public void setValidTime(LocalDateTime validTime) {
        this.validTime = validTime;
    }

    public String getReportCategory() {
        return reportCategory;
    }

    public void setReportCategory(String reportCategory) {
        this.reportCategory = reportCategory;
    }

    public String getCertNo() {
        return certNo;
    }

    public void setCertNo(String certNo) {
        this.certNo = certNo;
    }

    public String getCaliReason() {
        return caliReason;
    }

    public void setCaliReason(String caliReason) {
        this.caliReason = caliReason;
    }

    public String getCaliResult() {
        return caliResult;
    }

    public void setCaliResult(String caliResult) {
        this.caliResult = caliResult;
    }

    public String getClaim() {
        return claim;
    }

    public void setClaim(String claim) {
        this.claim = claim;
    }

    public String getUncertainty() {
        return uncertainty;
    }

    public void setUncertainty(String uncertainty) {
        this.uncertainty = uncertainty;
    }

    public String getContentResult() {
        return contentResult;
    }

    public void setContentResult(String contentResult) {
        this.contentResult = contentResult;
    }

    public String getReviewerId() {
        return reviewerId;
    }

    public void setReviewerId(String reviewerId) {
        this.reviewerId = reviewerId;
    }

    public String getReviewerName() {
        return reviewerName;
    }

    public void setReviewerName(String reviewerName) {
        this.reviewerName = reviewerName;
    }

    public LocalDateTime getReviewDate() {
        return reviewDate;
    }

    public void setReviewDate(LocalDateTime reviewDate) {
        this.reviewDate = reviewDate;
    }

    public LocalDateTime getMaintDate() {
        return maintDate;
    }

    public void setMaintDate(LocalDateTime maintDate) {
        this.maintDate = maintDate;
    }

    @Override
    public String getCreatedById() {
        return null;
    }

    @Override
    public void setCreatedById(String createdById) {

    }

    @Override
    public String getCreatedByName() {
        return null;
    }

    @Override
    public void setCreatedByName(String createdByName) {

    }

    @Override
    public LocalDateTime getCreatedTime() {
        return null;
    }

    @Override
    public void setCreatedTime(LocalDateTime createdTime) {

    }

    @Override
    public String getCreatedByOrgId() {
        return null;
    }

    @Override
    public void setCreatedByOrgId(String createdByOrgId) {

    }

    @Override
    public String getCreatedByOrgName() {
        return null;
    }

    @Override
    public void setCreatedByOrgName(String createdByOrgName) {

    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
