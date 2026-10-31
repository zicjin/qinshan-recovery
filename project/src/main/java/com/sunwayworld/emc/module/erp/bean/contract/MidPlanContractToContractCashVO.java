package com.sunwayworld.emc.module.erp.bean.contract;

import java.io.Serializable;
import java.util.List;

/** 中台-计划合同-拆分子单-付款 */
public class MidPlanContractToContractCashVO implements Serializable {
    /** 付款类型 */
    private Integer cashType;

    /** 分期付款记录 */
    private List<MidPlanContractToContractPeriodCashVO> periodCash;

    /** 分期付款记录详情 */
    private List<MidPlanContractBindContractPeriodCashVO> periodCashDetail;


    public Integer getCashType() {
        return cashType;
    }

    public void setCashType(Integer cashType) {
        this.cashType = cashType;
    }

    public List<MidPlanContractToContractPeriodCashVO> getPeriodCash() {
        return periodCash;
    }

    public void setPeriodCash(List<MidPlanContractToContractPeriodCashVO> periodCash) {
        this.periodCash = periodCash;
    }

    public List<MidPlanContractBindContractPeriodCashVO> getPeriodCashDetail() {
        return periodCashDetail;
    }

    public void setPeriodCashDetail(List<MidPlanContractBindContractPeriodCashVO> periodCashDetail) {
        this.periodCashDetail = periodCashDetail;
    }
}
