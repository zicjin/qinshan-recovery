package com.sunwayworld.emc.module.erp.bean;

import com.alibaba.fastjson.annotation.JSONField;

public class EnvSkuSamplingCostModel {

    private Long costNo;
    private Integer id;
    @JSONField(name = "isDefault")
    private Boolean isDefault;
    private String merchantId;
    private Long samplingCost;
    private String samplingType;
    private Integer skuNo;

    public Long getCostNo() {
        return costNo;
    }

    public void setCostNo(Long costNo) {
        this.costNo = costNo;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Boolean getDefault() {
        return isDefault;
    }

    public void setDefault(Boolean aDefault) {
        isDefault = aDefault;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public Long getSamplingCost() {
        return samplingCost;
    }

    public void setSamplingCost(Long samplingCost) {
        this.samplingCost = samplingCost;
    }

    public String getSamplingType() {
        return samplingType;
    }

    public void setSamplingType(String samplingType) {
        this.samplingType = samplingType;
    }

    public Integer getSkuNo() {
        return skuNo;
    }

    public void setSkuNo(Integer skuNo) {
        this.skuNo = skuNo;
    }
}
