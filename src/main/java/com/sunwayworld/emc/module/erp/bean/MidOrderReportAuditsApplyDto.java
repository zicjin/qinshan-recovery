package com.sunwayworld.emc.module.erp.bean;

import java.io.Serializable;

/**
 * 提交报告审核申请dto
 */
public class MidOrderReportAuditsApplyDto implements Serializable {

    /** 审核人编号 */
    private String auditorJobNumber;

    /** 订单ID */
    private Long gbId;

    private String merchantId;

    private Long operatorGbId;

    public String getAuditorJobNumber() {
        return auditorJobNumber;
    }

    public void setAuditorJobNumber(String auditorJobNumber) {
        this.auditorJobNumber = auditorJobNumber;
    }

    public Long getGbId() {
        return gbId;
    }

    public void setGbId(Long gbId) {
        this.gbId = gbId;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public Long getOperatorGbId() {
        return operatorGbId;
    }

    public void setOperatorGbId(Long operatorGbId) {
        this.operatorGbId = operatorGbId;
    }
}
