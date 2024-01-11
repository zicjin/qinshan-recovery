package com.sunwayworld.emc.module.erp.bean;

import java.io.Serializable;

/**
 * @author zhengxy@sunwayworld.com 2023年10月08日
 */
public class MidReportDetailDTO implements Serializable {

    private Long orderGbId;

    private String reportName;

    private String reportUrl;

    public Long getOrderGbId() {
        return orderGbId;
    }

    public void setOrderGbId(Long orderGbId) {
        this.orderGbId = orderGbId;
    }

    public String getReportName() {
        return reportName;
    }

    public void setReportName(String reportName) {
        this.reportName = reportName;
    }

    public String getReportUrl() {
        return reportUrl;
    }

    public void setReportUrl(String reportUrl) {
        this.reportUrl = reportUrl;
    }
}
