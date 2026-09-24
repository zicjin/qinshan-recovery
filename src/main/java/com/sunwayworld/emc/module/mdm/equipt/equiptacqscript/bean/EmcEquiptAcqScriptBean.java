/*
 * Decompiled with CFR.
 */
package com.sunwayworld.emc.module.mdm.equipt.equiptacqscript.bean;

import com.alibaba.fastjson.annotation.JSONField;
import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.NotNull;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractPersistable;
import com.sunwayworld.framework.support.domain.Insertable;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

@Table(value="T_EMC_EQUIPT_ACQ_SCRIPT")
public class EmcEquiptAcqScriptBean
extends AbstractPersistable<Long>
implements Insertable<Long> {
    @Transient
    private static final long serialVersionUID = 7455032350715272800L;
    @Id
    private Long id;
    private String scriptDesc;
    private String dataFormat;
    private String orderNoFormat;
    private String resultFormat;
    private String testFormat;
    private String rowDataSeparator;
    @NotNull(defaultValue="sequence")
    private String analysisWay;
    private String source;
    private String serialAcq;
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

    public String getScriptDesc() {
        return this.scriptDesc;
    }

    public void setScriptDesc(String scriptDesc) {
        this.scriptDesc = scriptDesc;
    }

    public String getDataFormat() {
        return this.dataFormat;
    }

    public void setDataFormat(String dataFormat) {
        this.dataFormat = dataFormat;
    }

    public String getOrderNoFormat() {
        return this.orderNoFormat;
    }

    public void setOrderNoFormat(String orderNoFormat) {
        this.orderNoFormat = orderNoFormat;
    }

    public String getResultFormat() {
        return this.resultFormat;
    }

    public String getTestFormat() {
        return this.testFormat;
    }

    public void setTestFormat(String testFormat) {
        this.testFormat = testFormat;
    }

    public void setResultFormat(String resultFormat) {
        this.resultFormat = resultFormat;
    }

    public String getRowDataSeparator() {
        return this.rowDataSeparator;
    }

    public void setRowDataSeparator(String rowDataSeparator) {
        this.rowDataSeparator = rowDataSeparator;
    }

    public String getAnalysisWay() {
        return this.analysisWay;
    }

    public void setAnalysisWay(String analysisWay) {
        this.analysisWay = analysisWay;
    }

    public String getSource() {
        return this.source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getSerialAcq() {
        return this.serialAcq;
    }

    public void setSerialAcq(String serialAcq) {
        this.serialAcq = serialAcq;
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

