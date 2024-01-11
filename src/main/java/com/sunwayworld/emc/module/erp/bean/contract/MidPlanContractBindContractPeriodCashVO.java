package com.sunwayworld.emc.module.erp.bean.contract;

import java.io.Serializable;
import java.util.List;

/** 计划合同-拆分子单-付款期数-VO */
public class MidPlanContractBindContractPeriodCashVO implements Serializable {
    /** 付款期数标识 */
    private Long cashGbId;

    /** 期数 */
    private Long period;

    /** 收款金额 */
    private Long price;

    /** 实际到款金额 */
    private Long actualPrice;

    /** 预收款时间 */
    private Long advancePaymentTime;

    /** 应收款时间 */
    private Long needReceiveTime;

    /** 预收库付款 */
    private List<MidPlanContractToContractCashWalletVO> wallets;

    /** 担保付款 */
    private MidPlanContractToContractCashCreditVO credit;

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

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public Long getActualPrice() {
        return actualPrice;
    }

    public void setActualPrice(Long actualPrice) {
        this.actualPrice = actualPrice;
    }

    public Long getAdvancePaymentTime() {
        return advancePaymentTime;
    }

    public void setAdvancePaymentTime(Long advancePaymentTime) {
        this.advancePaymentTime = advancePaymentTime;
    }

    public List<MidPlanContractToContractCashWalletVO> getWallets() {
        return wallets;
    }

    public void setWallets(List<MidPlanContractToContractCashWalletVO> wallets) {
        this.wallets = wallets;
    }

    public MidPlanContractToContractCashCreditVO getCredit() {
        return credit;
    }

    public void setCredit(MidPlanContractToContractCashCreditVO credit) {
        this.credit = credit;
    }

    public Long getNeedReceiveTime() {
        return needReceiveTime;
    }

    public void setNeedReceiveTime(Long needReceiveTime) {
        this.needReceiveTime = needReceiveTime;
    }
}
