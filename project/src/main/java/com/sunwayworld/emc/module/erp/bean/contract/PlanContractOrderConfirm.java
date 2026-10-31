package com.sunwayworld.emc.module.erp.bean.contract;

import java.io.Serializable;

/**
 * @author zhengxy@sunwayworld.com 2023年11月13日
 */
public class PlanContractOrderConfirm implements Serializable {

    private String orderGbId;
    private Long operatorGbId;

    public String getOrderGbId() {
        return orderGbId;
    }

    public void setOrderGbId(String orderGbId) {
        this.orderGbId = orderGbId;
    }

    public Long getOperatorGbId() {
        return operatorGbId;
    }

    public void setOperatorGbId(Long operatorGbId) {
        this.operatorGbId = operatorGbId;
    }
}
