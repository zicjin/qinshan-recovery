/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.lims.reporttemplate.support.constant;

import com.sunwayworld.framework.utils.StringUtils;

public enum LimsSourceDataTypeEnum {
    DETAIL("detail", "\u8be6\u60c5"),
    LIST("list", "\u5217\u8868"),
    FILE("file", "\u6587\u4ef6"),
    FILE_LIST("fileList", "\u6587\u4ef6\u5217\u8868"),
    SUB_TEMPLATE("subTemp", "\u5b50\u6a21\u677f"),
    SUB_TEMPLATE_LIST("subTempList", "\u5b50\u6a21\u677f\u5217\u8868");

    private String type;
    private String desc;

    private LimsSourceDataTypeEnum(String type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public static LimsSourceDataTypeEnum getTypeEnum(String type) {
        for (LimsSourceDataTypeEnum sourceType : LimsSourceDataTypeEnum.values()) {
            if (!StringUtils.equalsIgnoreCase(sourceType.type, type)) continue;
            return sourceType;
        }
        return DETAIL;
    }
}
