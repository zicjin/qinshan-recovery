package com.sunwayworld.emc.module.erp.bean;

import java.io.Serializable;

/**
 * 报告审核dto
 */
public class MidOrderReportAuditsDto implements Serializable {

    private Long gbId;

    private Long operatorGbId;

    private String merchantId;

    /** 状态，com.sunwayworld.emc.module.erp.bean.ReportAuditStatus */
    private Integer state;

    /** 驳回原因 */
    private String failReason;

    public Long getGbId() {
        return gbId;
    }

    public void setGbId(Long gbId) {
        this.gbId = gbId;
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
