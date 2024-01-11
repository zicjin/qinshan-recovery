package com.sunwayworld.emc.module.erp.bean;

import com.fasterxml.jackson.annotation.JsonValue;

public enum UserContractReviewAuditStatus {
    PASSED(0, "已通过"),
    REJECTED(1, "被驳回"),
    NOT_INIT(2, "未发起"),
    PENDING_EVALUATION(3, "待评估"),
    NO_EVALUATION_REQUIRED(4, "无需评估"),
    ABANDON(5, "废弃/删除"),
    ROLLBACK(6, "已撤回"),
    ;

    private int code;
    private String desc;

    UserContractReviewAuditStatus(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    @JsonValue
    public int getCode() {
        return code;
    }


}
