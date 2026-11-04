/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.lims.reporttemplate.support.constant;

public enum MergeDocxPageBreakEnum {
    FIRST(4, "\u5934\u90e8"),
    MIDDLE(2, "\u4e2d\u90e8"),
    LAST(1, "\u5c3e\u90e8");

    private int index;
    private String desc;

    private MergeDocxPageBreakEnum(int index, String desc) {
        this.index = index;
        this.desc = desc;
    }

    public static boolean isAdd(int i, MergeDocxPageBreakEnum checkEnum) {
        return (i & checkEnum.index) == checkEnum.index;
    }
}
