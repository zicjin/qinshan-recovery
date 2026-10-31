package com.sunwayworld.emc.module.erp.bean;

import java.util.List;

/**
 * @author zhengxy@sunwayworld.com 2023年10月08日
 */
public class MidReportDTO {

    private String merchantId;

    private Boolean needOverride;

    private Boolean needSend;

    private Long operatorGbId;

    private List<MidReportDetailDTO> reports;

    /** 开始实验时间，取lims最早的接样时间 */
    private Long startExperimentTime;

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public Boolean getNeedOverride() {
        return needOverride;
    }

    public void setNeedOverride(Boolean needOverride) {
        this.needOverride = needOverride;
    }

    public Boolean getNeedSend() {
        return needSend;
    }

    public void setNeedSend(Boolean needSend) {
        this.needSend = needSend;
    }

    public Long getOperatorGbId() {
        return operatorGbId;
    }

    public void setOperatorGbId(Long operatorGbId) {
        this.operatorGbId = operatorGbId;
    }

    public List<MidReportDetailDTO> getReports() {
        return reports;
    }

    public void setReports(List<MidReportDetailDTO> reports) {
        this.reports = reports;
    }

    public Long getStartExperimentTime() {
        return startExperimentTime;
    }

    public void setStartExperimentTime(Long startExperimentTime) {
        this.startExperimentTime = startExperimentTime;
    }
}
