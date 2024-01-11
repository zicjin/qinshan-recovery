package com.sunwayworld.emc.module.item.quotation.quotationregister.bean;

import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import com.alibaba.fastjson.annotation.JSONField;
import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractAuditable;
import com.sunwayworld.framework.support.domain.Auditable;
import com.sunwayworld.framework.support.domain.Insertable;

/**
 * 报价单登记表
 * 
 * @author xiongjie@sunwayworld.com@sunwayworld.com
 * @date 2021-03-25
 */
@Table("T_EMC_QUOTATION_REGISTER")
public class EmcQuotationregisterBean extends AbstractAuditable<Long> implements Insertable<Long>, Auditable<Long> {

    @Transient
    private static final long serialVersionUID = -82566196618711043L;

    @Id
    private Long id;// 主键
    private String status;// 状态
    private String quotationNo;// 报价单号
    private String quotationName;// 报价单名称
    private String contractCategory;// 合同类型
    private String detectCategory;// 检测类别
    private String consultCategory;// 咨询类型
    private String totalPrice;// 报价总计
    private String totalDiscount;// 总折扣率
    private String monitorTmplName;// 样品类型
    private String bizCategoryName;// 业务类型
    private String corePersonCode;// 负责人编码
    private String corePersonName;// 负责人名称
    private String customerCode;// 客户编码
    private String customerName;// 客户名称
    private String customerAddress;// 地址
    private String customerPhone;// 电话
    private String contactsCode;// 联系人编码
    private String contactsName;// 联系人名称
    private String contactsPhone;// 联系人电话
    private String contactsFax;// 联系人传真
    private String companyName;// 公司名称
    private String companyAddress;// 公司地址
    private String companyFax;// 公司传真
    private String bidderId;//报价人ID(江苏新锐)
    private String bidderName;//报价人(江苏新锐)
    private String bidderMobile;//报价人电话(江苏新锐)
    private String remark;// 备注
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getQuotationNo() {
        return quotationNo;
    }

    public void setQuotationNo(String quotationNo) {
        this.quotationNo = quotationNo;
    }

    public String getQuotationName() {
        return quotationName;
    }

    public void setQuotationName(String quotationName) {
        this.quotationName = quotationName;
    }

    public String getCorePersonCode() {
        return corePersonCode;
    }

    public void setCorePersonCode(String corePersonCode) {
        this.corePersonCode = corePersonCode;
    }

    public String getCorePersonName() {
        return corePersonName;
    }

    public void setCorePersonName(String corePersonName) {
        this.corePersonName = corePersonName;
    }

    public String getCustomerCode() {
        return customerCode;
    }

    public void setCustomerCode(String customerCode) {
        this.customerCode = customerCode;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerAddress() {
        return customerAddress;
    }

    public void setCustomerAddress(String customerAddress) {
        this.customerAddress = customerAddress;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public String getContactsCode() {
        return contactsCode;
    }

    public void setContactsCode(String contactsCode) {
        this.contactsCode = contactsCode;
    }

    public String getContactsName() {
        return contactsName;
    }

    public void setContactsName(String contactsName) {
        this.contactsName = contactsName;
    }

    public String getContactsPhone() {
        return contactsPhone;
    }

    public void setContactsPhone(String contactsPhone) {
        this.contactsPhone = contactsPhone;
    }

    public String getContactsFax() {
        return contactsFax;
    }

    public void setContactsFax(String contactsFax) {
        this.contactsFax = contactsFax;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyAddress() {
        return companyAddress;
    }

    public void setCompanyAddress(String companyAddress) {
        this.companyAddress = companyAddress;
    }

    public String getCompanyFax() {
        return companyFax;
    }

    public void setCompanyFax(String companyFax) {
        this.companyFax = companyFax;
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

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getTotalDiscount() {
        return totalDiscount;
    }

    public void setTotalDiscount(String totalDiscount) {
        this.totalDiscount = totalDiscount;
    }

    public String getMonitorTmplName() {
        return monitorTmplName;
    }

    public void setMonitorTmplName(String monitorTmplName) {
        this.monitorTmplName = monitorTmplName;
    }

    public String getBizCategoryName() {
        return bizCategoryName;
    }

    public void setBizCategoryName(String bizCategoryName) {
        this.bizCategoryName = bizCategoryName;
    }

    public String getContractCategory() {
        return contractCategory;
    }

    public void setContractCategory(String contractCategory) {
        this.contractCategory = contractCategory;
    }

    public String getDetectCategory() {
        return detectCategory;
    }

    public void setDetectCategory(String detectCategory) {
        this.detectCategory = detectCategory;
    }

    public String getConsultCategory() {
        return consultCategory;
    }

    public void setConsultCategory(String consultCategory) {
        this.consultCategory = consultCategory;
    }

    public String getBidderId() {
        return bidderId;
    }

    public void setBidderId(String bidderId) {
        this.bidderId = bidderId;
    }

    public String getBidderName() {
        return bidderName;
    }

    public void setBidderName(String bidderName) {
        this.bidderName = bidderName;
    }

    public String getBidderMobile() {
        return bidderMobile;
    }

    public void setBidderMobile(String bidderMobile) {
        this.bidderMobile = bidderMobile;
    }
}
