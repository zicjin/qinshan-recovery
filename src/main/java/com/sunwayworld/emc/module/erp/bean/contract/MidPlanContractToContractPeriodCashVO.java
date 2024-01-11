package com.sunwayworld.emc.module.erp.bean.contract;

import java.io.Serializable;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

/** 中台-计划合同-拆分子单-付款期数 */
public class MidPlanContractToContractPeriodCashVO implements Serializable {

    @JsonSerialize(using = ToStringSerializer.class)
    /** 付款标识 */
    private Long cashGbId;

    @JsonSerialize(using = ToStringSerializer.class)
    /** 期数 */
    private Long period;

    @JsonSerialize(using = ToStringSerializer.class)
    /** 应收款金额 */
    private Long advancePrice;

    @JsonSerialize(using = ToStringSerializer.class)
    /** 收款金额 */
    private Long receivePrice;

    @JsonSerialize(using = ToStringSerializer.class)
    /** 预计收款时间 */
    private Long advancePaymentTime;

    @JsonSerialize(using = ToStringSerializer.class)
    /** 担保金额 */
    private Long creditPrice;

    public Long getCashGbId() {
        return cashGbId;
    }

    public void setCashGbId(Long cashGbId) {
        this.cashGbId = cashGbId;
    }

    public Long getPeriod() {
        return period;
    }

    public void setPeriod(Long period) {
        this.period = period;
    }

    public Long getAdvancePrice() {
        return advancePrice;
    }

    public void setAdvancePrice(Long advancePrice) {
        this.advancePrice = advancePrice;
    }

    public Long getReceivePrice() {
        return receivePrice;
    }

    public void setReceivePrice(Long receivePrice) {
        this.receivePrice = receivePrice;
    }

    public Long getAdvancePaymentTime() {
        return advancePaymentTime;
    }

    public void setAdvancePaymentTime(Long advancePaymentTime) {
        this.advancePaymentTime = advancePaymentTime;
    }

    public Long getCreditPrice() {
        return creditPrice;
    }

    public void setCreditPrice(Long creditPrice) {
        this.creditPrice = creditPrice;
    }
}
