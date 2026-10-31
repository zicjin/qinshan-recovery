package com.sunwayworld.emc.module.erp.bean;

import java.io.Serializable;

/**
 * @author zhengxy@sunwayworld.com 2023年10月16日
 */
public class SaveContractOrderOtherFeeReq implements Serializable {

    private int feeType;
    private int feeCount;
    private String feeSinglePrice;
    private String amount;
    private int id;
    private String feeName;

    public int getFeeType() {
        return feeType;
    }

    public void setFeeType(int feeType) {
        this.feeType = feeType;
    }

    public int getFeeCount() {
        return feeCount;
    }

    public void setFeeCount(int feeCount) {
        this.feeCount = feeCount;
    }

    public String getFeeSinglePrice() {
        return feeSinglePrice;
    }

    public void setFeeSinglePrice(String feeSinglePrice) {
        this.feeSinglePrice = feeSinglePrice;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFeeName() {
        return feeName;
    }

    public void setFeeName(String feeName) {
        this.feeName = feeName;
    }
}
