package com.sunwayworld.emc.module.erp.bean.contract;

public class SaveContractOrderOtherFeeReq {

    /** 费用类型 */
    private Integer feeType;

    /** 费用数量 */
    private Integer feeCount;

    /** 费用单价 单价分 */
    private Long feeSinglePrice;

    /** 费用金额 单位分 */
    private Long amount;

    private String amountStr;

    /** id */
    private Integer id;

    /** 费用名称 */
    private String feeName;

    public Integer getFeeType() {
        return feeType;
    }

    public void setFeeType(Integer feeType) {
        this.feeType = feeType;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFeeName() {
        return feeName;
    }

    public void setFeeName(String feeName) {
        this.feeName = feeName;
    }

    public String getAmountStr() {
        return amountStr;
    }

    public void setAmountStr(String amountStr) {
        this.amountStr = amountStr;
    }

    public Integer getFeeCount() {
        return feeCount;
    }

    public void setFeeCount(Integer feeCount) {
        this.feeCount = feeCount;
    }

    public Long getFeeSinglePrice() {
        return feeSinglePrice;
    }

    public void setFeeSinglePrice(Long feeSinglePrice) {
        this.feeSinglePrice = feeSinglePrice;
    }

    @Override
    public String toString() {
        return "SaveContractOrderOtherFeeReq{" +
                "feeType=" + feeType +
                ", amount=" + amount +
                ", amountStr='" + amountStr + '\'' +
                ", id=" + id +
                ", feeName='" + feeName + '\'' +
                '}';
    }
}
