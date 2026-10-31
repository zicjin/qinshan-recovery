package com.sunwayworld.emc.module.erp.bean.contract;

import java.io.Serializable;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

/** 计划合同-服务方信息-VO */
public class MidPlanContractToServerVO implements Serializable {

    @JsonSerialize(using = ToStringSerializer.class)
    /** 服务方标识-更新时有值 */
    private Long serverId;

    @JsonSerialize(using = ToStringSerializer.class)
    /** 计划合同标识-更新时有值 */
    private Long planContractGbId;

    @JsonSerialize(using = ToStringSerializer.class)
    /** 所属客户标识 */
    private Long customerGbId;

    @JsonSerialize(using = ToStringSerializer.class)
    /** 所属产品经理 */
    private Long engineerGbId;

    /** 服务方联系人 */
    private String serverContact;

    /** 服务方联系电话 */
    private String serverPhone;

    /** 服务方地址 */
    private MidAddressDTO serverAddr;

    public Long getPlanContractGbId() {
        return planContractGbId;
    }

    public void setPlanContractGbId(Long planContractGbId) {
        this.planContractGbId = planContractGbId;
    }

    public Long getServerId() {
        return serverId;
    }

    public void setServerId(Long serverId) {
        this.serverId = serverId;
    }

    public Long getCustomerGbId() {
        return customerGbId;
    }

    public void setCustomerGbId(Long customerGbId) {
        this.customerGbId = customerGbId;
    }

    public Long getEngineerGbId() {
        return engineerGbId;
    }

    public void setEngineerGbId(Long engineerGbId) {
        this.engineerGbId = engineerGbId;
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
}
