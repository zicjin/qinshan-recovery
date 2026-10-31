package com.sunwayworld.emc.module.erp.bean;

import java.io.Serializable;

/**
 * 转接报告审核dto
 */
public class MidTransferOrderReportAuditorDto implements Serializable {

    /** 审核人编号 */
    private String auditorJobNumber;

    public String getAuditorJobNumber() {
        return auditorJobNumber;
    }

    public void setAuditorJobNumber(String auditorJobNumber) {
        this.auditorJobNumber = auditorJobNumber;
    }
}
