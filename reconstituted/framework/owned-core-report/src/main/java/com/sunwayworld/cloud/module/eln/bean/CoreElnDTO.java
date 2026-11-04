/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.eln.bean;

import com.sunwayworld.framework.support.domain.AbstractBaseData;

public class CoreElnDTO
extends AbstractBaseData {
    private static final long serialVersionUID = 4397112647350892469L;
    private Long id;
    private Long elnTmplId;
    private String elnParam;
    private String remark;
    private String dataSource;
    private String data;
    private String idData;
    private String elnContent;
    private String dirtyRecords;
    private String writeBackCategory;
    private String elnName;
    private String repeat;

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDataSource() {
        return this.dataSource;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    public String getData() {
        return this.data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getElnContent() {
        return this.elnContent;
    }

    public void setElnContent(String elnContent) {
        this.elnContent = elnContent;
    }

    public Long getElnTmplId() {
        return this.elnTmplId;
    }

    public void setElnTmplId(Long elnTmplId) {
        this.elnTmplId = elnTmplId;
    }

    public String getElnParam() {
        return this.elnParam;
    }

    public void setElnParam(String elnParam) {
        this.elnParam = elnParam;
    }

    public String getRemark() {
        return this.remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getDirtyRecords() {
        return this.dirtyRecords;
    }

    public void setDirtyRecords(String dirtyRecords) {
        this.dirtyRecords = dirtyRecords;
    }

    public String getIdData() {
        return this.idData;
    }

    public void setIdData(String idData) {
        this.idData = idData;
    }

    public String getWriteBackCategory() {
        return this.writeBackCategory;
    }

    public void setWriteBackCategory(String writeBackCategory) {
        this.writeBackCategory = writeBackCategory;
    }

    public String getElnName() {
        return this.elnName;
    }

    public void setElnName(String elnName) {
        this.elnName = elnName;
    }

    public String getRepeat() {
        return this.repeat;
    }

    public void setRepeat(String repeat) {
        this.repeat = repeat;
    }
}
