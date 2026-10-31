package com.sunwayworld.emc.module.erp.bean;

import java.io.Serializable;

/**
 * @author zhengxy@sunwayworld.com 2023年10月19日
 */
public class OrderReviewReq implements Serializable {

    private Long applyGbId;

    private Boolean reviewType;
    private Long operatorGbId;
    private String merchantId;
    /** 驳回原因 */
    private String remark;

    public Long getApplyGbId() {
        return applyGbId;
    }

    public void setApplyGbId(Long applyGbId) {
        this.applyGbId = applyGbId;
    }

    public Boolean getReviewType() {
        return reviewType;
    }

    public void setReviewType(Boolean reviewType) {
        this.reviewType = reviewType;
    }

    public Long getOperatorGbId() {
        return operatorGbId;
    }

    public void setOperatorGbId(Long operatorGbId) {
        this.operatorGbId = operatorGbId;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
