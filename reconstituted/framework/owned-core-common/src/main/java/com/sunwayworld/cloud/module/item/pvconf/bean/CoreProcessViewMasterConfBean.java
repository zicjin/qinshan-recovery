/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.pvconf.bean;

import com.alibaba.fastjson.annotation.JSONField;
import com.sunwayworld.framework.data.annotation.Clob;
import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractPersistable;
import com.sunwayworld.framework.support.domain.Insertable;
import com.sunwayworld.framework.support.domain.Orderable;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

@Table(value="T_CORE_PV_MASTER_CONF")
public class CoreProcessViewMasterConfBean
extends AbstractPersistable<Long>
implements Insertable<Long>,
Orderable<Long> {
    @Transient
    private static final long serialVersionUID = -5507996692692447350L;
    @Id
    private Long id;
    private Long confId;
    private String masterConfNo;
    private String masterConfName;
    private String dataSearch;
    @Clob
    private String totalSql;
    @Clob
    private String doneSql;
    private String subFilter;
    @Clob
    private String subFilterConf;
    private String displayGridData;
    @Clob
    private String gridSql;
    private Long orderNo;
    private String statusSql;
    private String createdById;
    private String createdByName;
    @JSONField(format="yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;
    private String createdByOrgId;
    private String createdByOrgName;

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
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

    public String getTotalSql() {
        return this.totalSql;
    }

    public void setTotalSql(String totalSql) {
        this.totalSql = totalSql;
    }

    public String getDoneSql() {
        return this.doneSql;
    }

    public void setDoneSql(String doneSql) {
        this.doneSql = doneSql;
    }

    public String getSubFilter() {
        return this.subFilter;
    }

    public void setSubFilter(String subFilter) {
        this.subFilter = subFilter;
    }

    public String getSubFilterConf() {
        return this.subFilterConf;
    }

    public void setSubFilterConf(String subFilterConf) {
        this.subFilterConf = subFilterConf;
    }

    public String getDisplayGridData() {
        return this.displayGridData;
    }

    public void setDisplayGridData(String displayGridData) {
        this.displayGridData = displayGridData;
    }

    public String getGridSql() {
        return this.gridSql;
    }

    public void setGridSql(String gridSql) {
        this.gridSql = gridSql;
    }

    @Override
    public Long getOrderNo() {
        return this.orderNo;
    }

    @Override
    public void setOrderNo(Long orderNo) {
        this.orderNo = orderNo;
    }

    public String getStatusSql() {
        return this.statusSql;
    }

    public void setStatusSql(String statusSql) {
        this.statusSql = statusSql;
    }

    @Override
    public String getCreatedById() {
        return this.createdById;
    }

    @Override
    public void setCreatedById(String createdById) {
        this.createdById = createdById;
    }

    @Override
    public String getCreatedByName() {
        return this.createdByName;
    }

    @Override
    public void setCreatedByName(String createdByName) {
        this.createdByName = createdByName;
    }

    @Override
    public LocalDateTime getCreatedTime() {
        return this.createdTime;
    }

    @Override
    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }

    @Override
    public String getCreatedByOrgId() {
        return this.createdByOrgId;
    }

    @Override
    public void setCreatedByOrgId(String createdByOrgId) {
        this.createdByOrgId = createdByOrgId;
    }

    @Override
    public String getCreatedByOrgName() {
        return this.createdByOrgName;
    }

    @Override
    public void setCreatedByOrgName(String createdByOrgName) {
        this.createdByOrgName = createdByOrgName;
    }
}
