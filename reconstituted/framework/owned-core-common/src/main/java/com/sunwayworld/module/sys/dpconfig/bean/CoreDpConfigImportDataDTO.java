/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.dpconfig.bean;

public class CoreDpConfigImportDataDTO {
    private String tableName;
    private String data;
    private Long orderNo;
    private String children;

    public String getTableName() {
        return this.tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getData() {
        return this.data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Long getOrderNo() {
        return this.orderNo;
    }

    public void setOrderNo(Long orderNo) {
        this.orderNo = orderNo;
    }

    public String getChildren() {
        return this.children;
    }

    public void setChildren(String children) {
        this.children = children;
    }
}
