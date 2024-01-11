package com.sunwayworld.emc.module.erp.bean.contract;

import java.io.Serializable;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

/** 中台-计划合同拆分合同详情-VO */
public class MidPlanContractToUserContractVO implements Serializable {

    @JsonSerialize(using = ToStringSerializer.class)
    /** 计划合同标识 */
    private Long planContractGbId;

    @JsonSerialize(using = ToStringSerializer.class)
    /** 拆分标识 */
    private Long contractGbId;

    @JsonSerialize(using = ToStringSerializer.class)
    /** 合同标识 */
    private Long userContractGbId;

    @JsonSerialize(using = ToStringSerializer.class)
    /** 客户gbId */
    private Long customerGbId;

    /** 客户名称 */
    private String customerName;

    @JsonSerialize(using = ToStringSerializer.class)
    /** 合同模板标识 */
    private Long customTemplateGbId;

    /** 状态 */
    private Integer status;

    /** 付款信息 */
    private MidPlanContractToContractCashVO pay;

    /** 报价信息 */
    private MidPlanContractToContractPkgVO pkg;

    //====== 二开 ======

    /** 订单 */
    private MidOrderVOV2Ext order;


    public Long getPlanContractGbId() {
        return planContractGbId;
    }

    public void setPlanContractGbId(Long planContractGbId) {
        this.planContractGbId = planContractGbId;
    }

    public Long getContractGbId() {
        return contractGbId;
    }

    public void setContractGbId(Long contractGbId) {
        this.contractGbId = contractGbId;
    }

    public Long getUserContractGbId() {
        return userContractGbId;
    }

    public void setUserContractGbId(Long userContractGbId) {
        this.userContractGbId = userContractGbId;
    }

    public Long getCustomerGbId() {
        return customerGbId;
    }

    public void setCustomerGbId(Long customerGbId) {
        this.customerGbId = customerGbId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public Long getCustomTemplateGbId() {
        return customTemplateGbId;
    }

    public void setCustomTemplateGbId(Long customTemplateGbId) {
        this.customTemplateGbId = customTemplateGbId;
    }

    public MidOrderVOV2Ext getOrder() {
        return order;
    }

    public void setOrder(MidOrderVOV2Ext order) {
        this.order = order;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public MidPlanContractToContractCashVO getPay() {
        return pay;
    }

    public void setPay(MidPlanContractToContractCashVO pay) {
        this.pay = pay;
    }

    public MidPlanContractToContractPkgVO getPkg() {
        return pkg;
    }

    public void setPkg(MidPlanContractToContractPkgVO pkg) {
        this.pkg = pkg;
    }
}
