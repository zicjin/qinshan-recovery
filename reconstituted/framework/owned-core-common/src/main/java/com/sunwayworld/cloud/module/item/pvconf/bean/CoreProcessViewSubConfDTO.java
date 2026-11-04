/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.pvconf.bean;

import com.sunwayworld.framework.support.domain.AbstractBaseData;
import com.sunwayworld.framework.support.domain.BaseData;

public class CoreProcessViewSubConfDTO
extends AbstractBaseData
implements BaseData {
    private static final long serialVersionUID = -3607164889325365263L;
    private Long id;
    private Long masterConfId;
    private String subConfNo;
    private String subConfName;
    private String dataSearch;
    private String displayGridData;
    private Long totalCount;
    private Long doneCount;
    private Long undoneCount;
    private String donePercent;
    private String undonePercent;
    private Long orderNo;

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMasterConfId() {
        return this.masterConfId;
    }

    public void setMasterConfId(Long masterConfId) {
        this.masterConfId = masterConfId;
    }

    public String getSubConfNo() {
        return this.subConfNo;
    }

    public void setSubConfNo(String subConfNo) {
        this.subConfNo = subConfNo;
    }

    public String getSubConfName() {
        return this.subConfName;
    }

    public void setSubConfName(String subConfName) {
        this.subConfName = subConfName;
    }

    public String getDataSearch() {
        return this.dataSearch;
    }

    public void setDataSearch(String dataSearch) {
        this.dataSearch = dataSearch;
    }

    public String getDisplayGridData() {
        return this.displayGridData;
    }

    public void setDisplayGridData(String displayGridData) {
        this.displayGridData = displayGridData;
    }

    public Long getTotalCount() {
        return this.totalCount;
    }

    public void setTotalCount(Long totalCount) {
        this.totalCount = totalCount;
    }

    public Long getDoneCount() {
        return this.doneCount;
    }

    public void setDoneCount(Long doneCount) {
        this.doneCount = doneCount;
    }

    public Long getUndoneCount() {
        return this.undoneCount;
    }

    public void setUndoneCount(Long undoneCount) {
        this.undoneCount = undoneCount;
    }

    public String getDonePercent() {
        return this.donePercent;
    }

    public void setDonePercent(String donePercent) {
        this.donePercent = donePercent;
    }

    public String getUndonePercent() {
        return this.undonePercent;
    }

    public void setUndonePercent(String undonePercent) {
        this.undonePercent = undonePercent;
    }

    public Long getOrderNo() {
        return this.orderNo;
    }

    public void setOrderNo(Long orderNo) {
        this.orderNo = orderNo;
    }
}
