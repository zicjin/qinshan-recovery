package com.sunwayworld.emc.module.erp.bean;

import java.io.Serializable;

/**
 * @author zhengxy@sunwayworld.com 2023年10月16日
 */
public class PaymentInfoDto implements Serializable {

    private String creditFee;
    private String creditTotalFee;
    private int creditType;
    private String orderPrice;

    public String getCreditFee() {
        return creditFee;
    }

    public void setCreditFee(String creditFee) {
        this.creditFee = creditFee;
    }

    public String getCreditTotalFee() {
        return creditTotalFee;
    }

    public void setCreditTotalFee(String creditTotalFee) {
        this.creditTotalFee = creditTotalFee;
    }

    public int getCreditType() {
        return creditType;
    }

    public void setCreditType(int creditType) {
        this.creditType = creditType;
    }

    public String getOrderPrice() {
        return orderPrice;
    }

    public void setOrderPrice(String orderPrice) {
        this.orderPrice = orderPrice;
    }
}
