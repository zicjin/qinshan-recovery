package com.sunwayworld.emc.module.erp.bean.contract;

import java.io.Serializable;

public class MidOrderAdditionalReportDTO implements Serializable {

    /**
     * 额外报告唯一标识
     */
    private Long gbId;

    /**
     * 订单标识
     */
    private Long orderGbId;

    /**
     * 报告类型
     * com.jiancetech.business.model.enumModel.ReportType
     */
    private Integer reportType;

    /**
     * 是否需要电子版
     * com.jiancetech.business.model.enumModel.ReportIsNeedElectronicType
     */
    private Integer needElectronicType;

    /**
     * 纸质报告份数
     */
    private Integer paperReportCnt;

    public Long getGbId() {
        return gbId;
    }

    public void setGbId(Long gbId) {
        this.gbId = gbId;
    }

    public Long getOrderGbId() {
        return orderGbId;
    }

    public void setOrderGbId(Long orderGbId) {
        this.orderGbId = orderGbId;
    }

    public Integer getReportType() {
        return reportType;
    }

    public void setReportType(Integer reportType) {
        this.reportType = reportType;
    }

    public Integer getNeedElectronicType() {
        return needElectronicType;
    }

    public void setNeedElectronicType(Integer needElectronicType) {
        this.needElectronicType = needElectronicType;
    }

    public Integer getPaperReportCnt() {
        return paperReportCnt;
    }

    public void setPaperReportCnt(Integer paperReportCnt) {
        this.paperReportCnt = paperReportCnt;
    }
}
