/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.pvconf.bean;

import com.sunwayworld.cloud.module.item.pvconf.bean.CoreProcessViewSubConfDTO;
import com.sunwayworld.framework.support.domain.AbstractBaseData;
import com.sunwayworld.framework.support.domain.BaseData;
import java.util.List;

public class CoreProcessViewMasterConfDTO
extends AbstractBaseData
implements BaseData {
    private static final long serialVersionUID = -5507996692692447350L;
    private Long id;
    private Long confId;
    private String masterConfNo;
    private String masterConfName;
    private String dataSearch;
    private String displayGridData;
    private String status;
    private Long totalCount;
    private Long doneCount;
    private Long undoneCount;
    private String donePercent;
    private String undonePercent;
    private Long orderNo;
    private List<CoreProcessViewSubConfDTO> subConfList;

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getConfId() {
        return this.confId;
    }

    public void setConfId(Long confId) {
        this.confId = confId;
    }

    public String getMasterConfNo() {
        return this.masterConfNo;
    }

    public void setMasterConfNo(String masterConfNo) {
        this.masterConfNo = masterConfNo;
    }

    public String getMasterConfName() {
        return this.masterConfName;
    }

    public void setMasterConfName(String masterConfName) {
        this.masterConfName = masterConfName;
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

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public List<CoreProcessViewSubConfDTO> getSubConfList() {
        return this.subConfList;
    }

    public void setSubConfList(List<CoreProcessViewSubConfDTO> subConfList) {
        this.subConfList = subConfList;
    }
}
