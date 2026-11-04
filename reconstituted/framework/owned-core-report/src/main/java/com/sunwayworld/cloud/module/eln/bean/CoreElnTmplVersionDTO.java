/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.eln.bean;

import com.sunwayworld.framework.support.domain.AbstractBaseData;
import java.util.List;

public class CoreElnTmplVersionDTO
extends AbstractBaseData {
    private static final long serialVersionUID = -2840374069182772162L;
    private Long id;
    private Long elnTmplId;
    private String dataSource;
    private String elnTmplContent;
    private Long elnTmplFileId;
    private String elnTmplParam;
    private List<String> elnParamList;
    private String data;

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getElnTmplId() {
        return this.elnTmplId;
    }

    public void setElnTmplId(Long elnTmplId) {
        this.elnTmplId = elnTmplId;
    }

    public String getDataSource() {
        return this.dataSource;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    public String getElnTmplContent() {
        return this.elnTmplContent;
    }

    public void setElnTmplContent(String elnTmplContent) {
        this.elnTmplContent = elnTmplContent;
    }

    public String getElnTmplParam() {
        return this.elnTmplParam;
    }

    public void setElnTmplParam(String elnTmplParam) {
        this.elnTmplParam = elnTmplParam;
    }

    public List<String> getElnParamList() {
        return this.elnParamList;
    }

    public void setElnParamList(List<String> elnParamList) {
        this.elnParamList = elnParamList;
    }

    public String getData() {
        return this.data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Long getElnTmplFileId() {
        return this.elnTmplFileId;
    }

    public void setElnTmplFileId(Long elnTmplFileId) {
        this.elnTmplFileId = elnTmplFileId;
    }
}
