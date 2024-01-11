package com.sunwayworld.emc.module.erp.bean;

public class MidContractReviewAuditsDto {

    private Long reviewId;

    private Long operatorGbId;

    private String merchantId;

    /** 状态, com.sunwayworld.emc.module.erp.bean.UserContractReviewAuditStatus */
    private Integer state;

    /** 驳回原因 */
    private String failReason;

    public Long getReviewId() {
        return reviewId;
    }

    public void setReviewId(Long reviewId) {
        this.reviewId = reviewId;
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

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public String getFailReason() {
        return failReason;
    }

    public void setFailReason(String failReason) {
        this.failReason = failReason;
    }
}
