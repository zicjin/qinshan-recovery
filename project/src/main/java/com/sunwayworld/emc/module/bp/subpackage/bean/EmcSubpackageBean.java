package com.sunwayworld.emc.module.bp.subpackage.bean;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.alibaba.fastjson.annotation.JSONField;
import org.springframework.format.annotation.DateTimeFormat;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractInsertable;

/**
 * 分包单测试
 * @author zhengxy@sunwayworld.com 2022年09月05日
 */
@Table("T_EMC_SUBPACKAGE")
public class EmcSubpackageBean extends AbstractInsertable<Long> {

    @Transient
    private static final long serialVersionUID = -3187736394026058677L;

    @Id
    private Long id;  // 主键
    private String subpackageNo;  // 分包单号
    private Long projId;  // 项目ID
    private String projNo;  // 项目编号
    private String contractNo;  // 合同编号
    private String subpackageType; // 分包单创建类型（委托分包单、被委托分包单）
    private String sampling; // 是否采样
    private String orgId; // 所属单位ID
    private String status; // 状态
    private Long relatedSubpackageId; // 关联分包单
    private String entrustOrgId; // 委托单位
    private String entrustOrgName; // 委托单位名称
    private String contact; //联系人
    private String contactPhone; // 联系人电话
    private String contactEmail; // 联系人邮箱
    private String subpackageTotalPrice; // 分包总价
    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate completeDate; // 完成时间
    private String siteContact; // 现场联系人
    private String sitePhone; // 现场电话
    private String haveContract; // 是否已经创建合同
    private Long subcontractorId; // 外部分包商ID
    private String innerSubcontractorId; // 内部分包商ID
    private String innerSubcontractorName; // 内部分包商
    private String remark; // 备注

    private String subpackageCategory; // 分包单分类（采样前分包、样品分包）

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

    public String getSubpackageNo() {
        return subpackageNo;
    }

    public void setSubpackageNo(String subpackageNo) {
        this.subpackageNo = subpackageNo;
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

    public String getContractNo() {
        return contractNo;
    }

    public void setContractNo(String contractNo) {
        this.contractNo = contractNo;
    }

    public String getSubpackageType() {
        return subpackageType;
    }

    public void setSubpackageType(String subpackageType) {
        this.subpackageType = subpackageType;
    }

    public String getSampling() {
        return sampling;
    }

    public void setSampling(String sampling) {
        this.sampling = sampling;
    }

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getRelatedSubpackageId() {
        return relatedSubpackageId;
    }

    public void setRelatedSubpackageId(Long relatedSubpackageId) {
        this.relatedSubpackageId = relatedSubpackageId;
    }

    public String getEntrustOrgId() {
        return entrustOrgId;
    }

    public void setEntrustOrgId(String entrustOrgId) {
        this.entrustOrgId = entrustOrgId;
    }

    public String getEntrustOrgName() {
        return entrustOrgName;
    }

    public void setEntrustOrgName(String entrustOrgName) {
        this.entrustOrgName = entrustOrgName;
    }

    public String getContact() { return contact; }

    public void setContact(String contact) { this.contact = contact; }

    public String getContactPhone() { return contactPhone; }

    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }

    public String getContactEmail() { return contactEmail; }

    public void setContactEmail(String contactEmail) { this.contactEmail = contactEmail; }

    public String getSubpackageTotalPrice() { return subpackageTotalPrice; }

    public void setSubpackageTotalPrice(String subpackageTotalPrice) { this.subpackageTotalPrice = subpackageTotalPrice; }

    public LocalDate getCompleteDate() { return completeDate; }

    public void setCompleteDate(LocalDate completeDate) { this.completeDate = completeDate; }

    public String getSiteContact() { return siteContact; }

    public void setSiteContact(String siteContact) { this.siteContact = siteContact; }

    public String getSitePhone() { return sitePhone; }

    public void setSitePhone(String sitePhone) { this.sitePhone = sitePhone; }

    public String getHaveContract() {
        return haveContract;
    }

    public void setHaveContract(String haveContract) {
        this.haveContract = haveContract;
    }

    public Long getSubcontractorId() {
        return subcontractorId;
    }

    public void setSubcontractorId(Long subcontractorId) {
        this.subcontractorId = subcontractorId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
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

    public String getInnerSubcontractorId() {
        return innerSubcontractorId;
    }

    public void setInnerSubcontractorId(String innerSubcontractorId) {
        this.innerSubcontractorId = innerSubcontractorId;
    }

    public String getInnerSubcontractorName() {
        return innerSubcontractorName;
    }

    public void setInnerSubcontractorName(String innerSubcontractorName) {
        this.innerSubcontractorName = innerSubcontractorName;
    }

    public String getSubpackageCategory() {
        return subpackageCategory;
    }

    public void setSubpackageCategory(String subpackageCategory) {
        this.subpackageCategory = subpackageCategory;
    }
}
