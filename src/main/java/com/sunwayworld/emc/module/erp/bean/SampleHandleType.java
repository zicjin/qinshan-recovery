package com.sunwayworld.emc.module.erp.bean;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 余样处理类型
 */
public enum SampleHandleType {
    ORG_PROCESSING(1, "服务方处理"),
    USER_PROCESSING(2, "用户自取"),
    EXPRESS(3, "邮寄退回");

    private int code;

    private String desc;


    SampleHandleType(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @JsonValue
    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
