package com.sunwayworld.emc.module.erp.bean.contract;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import java.io.Serializable;

/** 中台-计划合同-委托方信息-DTO */
public class MidPlanContractToDelegateCompanyDTO implements Serializable {
    @JsonSerialize(using = ToStringSerializer.class)
    /** 计划合同标识-更新时有值 */
    private Long planContractGbId;

    @JsonSerialize(using = ToStringSerializer.class)
    /** 委托方标识-更新时有值 */
    private Long delegateCompanyId;

    /** 委托方 */
    private String delegateCompany;

    /** 委托方地址 */
    private String delegateCompanyAddr;

    @JsonSerialize(using = ToStringSerializer.class)
    /** 联系人标识 */
    private Long contactGbId;

    /** 联系人 */
    private String contactName;

    /** 电话 */
    private String phoneNum;

    /** 邮箱 */
    private String email;

    public Long getPlanContractGbId() {
        return planContractGbId;
    }

    public void setPlanContractGbId(Long planContractGbId) {
        this.planContractGbId = planContractGbId;
    }

    public Long getDelegateCompanyId() {
        return delegateCompanyId;
    }

    public void setDelegateCompanyId(Long delegateCompanyId) {
        this.delegateCompanyId = delegateCompanyId;
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

    public Long getContactGbId() {
        return contactGbId;
    }

    public void setContactGbId(Long contactGbId) {
        this.contactGbId = contactGbId;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
