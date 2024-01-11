package com.sunwayworld.emc.module.mdm.crm.bean;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractInsertable;

/**
 * 商机支付表
 * @author zhengxy@sunwayworld.com 2021年08月26日
 */
@Table("T_CRM_OPPORTUNITY_PAYMENT")
public class CrmOpportunityPaymentBean extends AbstractInsertable<String> {

    @Transient
    private static final long serialVersionUID = -7808261270201893136L;

    @Id
    private String id;// 主键
    private String contractId;// 合同id
    private String paymentType;// 付款方式
    private String planProportion;// 计划比例
    private Double planAmount;// 计划金额
    private String remark;// 备注

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    public String getContractId() {
        return contractId;
    }

    public void setContractId(String contractId) {
        this.contractId = contractId;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public String getPlanProportion() {
        return planProportion;
    }

    public void setPlanProportion(String planProportion) {
        this.planProportion = planProportion;
    }

    public Double getPlanAmount() {
        return planAmount;
    }

    public void setPlanAmount(Double planAmount) {
        this.planAmount = planAmount;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
