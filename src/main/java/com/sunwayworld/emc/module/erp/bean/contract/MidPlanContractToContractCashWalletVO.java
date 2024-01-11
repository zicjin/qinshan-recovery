package com.sunwayworld.emc.module.erp.bean.contract;

import java.io.Serializable;

/** 计划合同-拆分子单-付款钱包-VO */
public class MidPlanContractToContractCashWalletVO implements Serializable
{

    /** 客户标识标识 */
    private Long customerGbId;

    /** 客户付款方标识 */
    private Long customerPayerGbId;

    /** 客户付款方名称 */
    private String customerPayerName;

    /** 客户付款方钱包标识 */
    private Long customerPayWalletGbId;

    /** 流水标识 */
    private Long traceId;

    /** 认款时间 */
    private Long receiveTime;

    /** 总金额 */
    private Long totalPrice;

    /** 剩余金额 */
    private Long residuePrice;

    /** 认款金额 */
    private Long receivePrice;

    public Long getCustomerGbId() {
        return customerGbId;
    }

    public void setCustomerGbId(Long customerGbId) {
        this.customerGbId = customerGbId;
    }

    public Long getCustomerPayerGbId() {
        return customerPayerGbId;
    }

    public void setCustomerPayerGbId(Long customerPayerGbId) {
        this.customerPayerGbId = customerPayerGbId;
    }

    public Long getCustomerPayWalletGbId() {
        return customerPayWalletGbId;
    }

    public void setCustomerPayWalletGbId(Long customerPayWalletGbId) {
        this.customerPayWalletGbId = customerPayWalletGbId;
    }

    public Long getTraceId() {
        return traceId;
    }

    public void setTraceId(Long traceId) {
        this.traceId = traceId;
    }

    public Long getReceivePrice() {
        return receivePrice;
    }

    public void setReceivePrice(Long receivePrice) {
        this.receivePrice = receivePrice;
    }

    public String getCustomerPayerName() {
        return customerPayerName;
    }

    public void setCustomerPayerName(String customerPayerName) {
        this.customerPayerName = customerPayerName;
    }

    public Long getReceiveTime() {
        return receiveTime;
    }

    public void setReceiveTime(Long receiveTime) {
        this.receiveTime = receiveTime;
    }

    public Long getResiduePrice() {
        return residuePrice;
    }

    public void setResiduePrice(Long residuePrice) {
        this.residuePrice = residuePrice;
    }

    public Long getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Long totalPrice) {
        this.totalPrice = totalPrice;
    }
}
