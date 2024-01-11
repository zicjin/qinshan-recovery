package com.sunwayworld.emc.module.erp.bean;

import java.io.Serializable;

public class MidUserContractVO implements Serializable {

    /** 合同gbId */
    private Long gbId;

    /** 合同编号 */
    private String userContractNumber;

    /** 客户gbId */
    private Long customerGbId;

    /** 服务方联系人 */
    private String serverContact;

    /** 服务方联系电话 */
    private String serverPhone;

    /** 服务方地址 */
    private MidAddressDTO serverAddr;

    /** 委托方 */
    private String delegateCompany;

    /** 委托方地址 */
    private String delegateCompanyAddr;

    /** 英文-委托方 */
    private String enDelegateCompany;

    /** 英文-委托方地址 */
    private String enDelegateCompanyAddr;

    /** 委托方地址id */
    private Long delegateCompanyAddrId;

    /** 联系人 */
    private String contactName;

    /** 联系人gbId */
    private Long contactGbId;

    /** 邮箱 */
    private String email;

    /** 传真 */
    private String fax;

    /** 电话 */
    private String phoneNum;

    /** 余样处理，com.sunwayworld.emc.module.erp.bean.SampleHandleType */
    private Integer sampleHandleType;

    /** 余样处理 */
    private String sampleHandle;

    /** 退样地址 */
    private MidAddressDTO backSampleAddress;

    /** 退样联系人 */
    private String backSampleContactName;

    /** 退样联系电话 */
    private String backSamplePhone;

    /** 中标机构 */
    private MidOrgVO org;

    /** 是否付款 */
    private boolean paid;

    /** 商机gbId */
    private Long businessOppGbId;

    /** 用户合同状态 */
    private Integer processingStatus;

    /** 子公司gbId */
    private Long companyGbId;

    /** 子公司名称 */
    private String companyName;

    /** 框架合同gbId */
    private Long frameworkContractGbId;

    /** 是否接受分包 */
    private Boolean bAcceptSubcontract;

    public Long getGbId() {
        return gbId;
    }

    public void setGbId(Long gbId) {
        this.gbId = gbId;
    }

    public Long getCustomerGbId() {
        return customerGbId;
    }

    public void setCustomerGbId(Long customerGbId) {
        this.customerGbId = customerGbId;
    }

    public String getServerContact() {
        return serverContact;
    }

    public void setServerContact(String serverContact) {
        this.serverContact = serverContact;
    }

    public String getServerPhone() {
        return serverPhone;
    }

    public void setServerPhone(String serverPhone) {
        this.serverPhone = serverPhone;
    }

    public MidAddressDTO getServerAddr() {
        return serverAddr;
    }

    public void setServerAddr(MidAddressDTO serverAddr) {
        this.serverAddr = serverAddr;
    }

    public String getDelegateCompany() {
        return delegateCompany;
    }

    public void setDelegateCompany(String delegateCompany) {
        this.delegateCompany = delegateCompany;
    }

    public String getDelegateCompanyAddr() {
        return delegateCompanyAddr;
    }

    public void setDelegateCompanyAddr(String delegateCompanyAddr) {
        this.delegateCompanyAddr = delegateCompanyAddr;
    }

    public String getEnDelegateCompany() {
        return enDelegateCompany;
    }

    public void setEnDelegateCompany(String enDelegateCompany) {
        this.enDelegateCompany = enDelegateCompany;
    }

    public String getEnDelegateCompanyAddr() {
        return enDelegateCompanyAddr;
    }

    public void setEnDelegateCompanyAddr(String enDelegateCompanyAddr) {
        this.enDelegateCompanyAddr = enDelegateCompanyAddr;
    }

    public Long getDelegateCompanyAddrId() {
        return delegateCompanyAddrId;
    }

    public void setDelegateCompanyAddrId(Long delegateCompanyAddrId) {
        this.delegateCompanyAddrId = delegateCompanyAddrId;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public Long getContactGbId() {
        return contactGbId;
    }

    public void setContactGbId(Long contactGbId) {
        this.contactGbId = contactGbId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public Integer getSampleHandleType() {
        return sampleHandleType;
    }

    public void setSampleHandleType(Integer sampleHandleType) {
        this.sampleHandleType = sampleHandleType;
    }

    public String getSampleHandle() {
        return sampleHandle;
    }

    public void setSampleHandle(String sampleHandle) {
        this.sampleHandle = sampleHandle;
    }

    public MidAddressDTO getBackSampleAddress() {
        return backSampleAddress;
    }

    public void setBackSampleAddress(MidAddressDTO backSampleAddress) {
        this.backSampleAddress = backSampleAddress;
    }

    public String getBackSampleContactName() {
        return backSampleContactName;
    }

    public void setBackSampleContactName(String backSampleContactName) {
        this.backSampleContactName = backSampleContactName;
    }

    public String getBackSamplePhone() {
        return backSamplePhone;
    }

    public void setBackSamplePhone(String backSamplePhone) {
        this.backSamplePhone = backSamplePhone;
    }

    public MidOrgVO getOrg() {
        return org;
    }

    public void setOrg(MidOrgVO org) {
        this.org = org;
    }

    public boolean isPaid() {
        return paid;
    }

    public void setPaid(boolean paid) {
        this.paid = paid;
    }

    public Long getBusinessOppGbId() {
        return businessOppGbId;
    }

    public void setBusinessOppGbId(Long businessOppGbId) {
        this.businessOppGbId = businessOppGbId;
    }

    public Integer getProcessingStatus() {
        return processingStatus;
    }

    public void setProcessingStatus(Integer processingStatus) {
        this.processingStatus = processingStatus;
    }

    public Long getCompanyGbId() {
        return companyGbId;
    }

    public void setCompanyGbId(Long companyGbId) {
        this.companyGbId = companyGbId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public Long getFrameworkContractGbId() {
        return frameworkContractGbId;
    }

    public void setFrameworkContractGbId(Long frameworkContractGbId) {
        this.frameworkContractGbId = frameworkContractGbId;
    }

    public Boolean getbAcceptSubcontract() {
        return bAcceptSubcontract;
    }

    public void setbAcceptSubcontract(Boolean bAcceptSubcontract) {
        this.bAcceptSubcontract = bAcceptSubcontract;
    }

    public String getUserContractNumber() {
        return userContractNumber;
    }

    public void setUserContractNumber(String userContractNumber) {
        this.userContractNumber = userContractNumber;
    }
}
