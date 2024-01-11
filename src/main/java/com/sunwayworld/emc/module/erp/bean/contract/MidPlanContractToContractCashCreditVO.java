package com.sunwayworld.emc.module.erp.bean.contract;

import java.io.Serializable;

/** 计划合同-拆分子单-担保支付-VO */
public class MidPlanContractToContractCashCreditVO implements Serializable
{

    /** 担保ID */
    private Long creditGbId;

    /** 担保金额 */
    private Long creditPrice = 0L;

    /** 剩余担保额度 */
    private Long residuePrice;

    /** 担保类型", dataType = "com.jiancetech.finance.model.enumModel.CreditType */
    private Integer creditType;

    public Long getCreditGbId() {
        return creditGbId;
    }

    public void setCreditGbId(Long creditGbId) {
        this.creditGbId = creditGbId;
    }

    public Long getCreditPrice() {
        return creditPrice;
    }

    public void setCreditPrice(Long creditPrice) {
        this.creditPrice = creditPrice;
    }

    public Long getResiduePrice() {
        return residuePrice;
    }

    public void setResiduePrice(Long residuePrice) {
        this.residuePrice = residuePrice;
    }

    public Integer getCreditType() {
        return creditType;
    }

    public void setCreditType(Integer creditType) {
        this.creditType = creditType;
    }
}
