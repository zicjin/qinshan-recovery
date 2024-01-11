package com.sunwayworld.emc.module.erp.bean;

/**
 * 报告审核状态
 */
public enum ReportAuditStatus {
    WAIL_AUDIT(1, "等待审核"),
    SUCCESS(2, "成功"),
    FAIL(3, "失败"),
    INVALID(4, "已作废");

    private int code;
    private String desc;


    ReportAuditStatus(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    public int getCode() {
        return code;
    }


}
