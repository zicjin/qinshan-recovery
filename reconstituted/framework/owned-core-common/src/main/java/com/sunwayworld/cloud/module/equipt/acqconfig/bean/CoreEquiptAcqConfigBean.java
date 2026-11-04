/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.equipt.acqconfig.bean;

import com.alibaba.fastjson.annotation.JSONField;
import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractInsertable;
import com.sunwayworld.framework.support.domain.Insertable;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

@Table(value="T_CORE_EQUIPT_ACQ_CONFIG")
public class CoreEquiptAcqConfigBean
extends AbstractInsertable<String>
implements Insertable<String> {
    @Transient
    private static final long serialVersionUID = 545385372591443623L;
    @Id
    private String id;
    private String acqCategory;
    private String baudRate;
    private String terminator;
    private String dataBit;
    private String parityCheck;
    private String com;
    private String stopBit;
    private String beginSymbol;
    private String endSymbol;
    private String analysisFormula;
    private String endFlag;
    private String ip;
    private String port;
    private String createdById;
    private String createdByName;
    @JSONField(format="yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;
    private String createdByOrgId;
    private String createdByOrgName;

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    public String getAcqCategory() {
        return this.acqCategory;
    }

    public void setAcqCategory(String acqCategory) {
        this.acqCategory = acqCategory;
    }

    public String getBaudRate() {
        return this.baudRate;
    }

    public void setBaudRate(String baudRate) {
        this.baudRate = baudRate;
    }

    public String getTerminator() {
        return this.terminator;
    }

    public void setTerminator(String terminator) {
        this.terminator = terminator;
    }

    public String getDataBit() {
        return this.dataBit;
    }

    public void setDataBit(String dataBit) {
        this.dataBit = dataBit;
    }

    public String getParityCheck() {
        return this.parityCheck;
    }

    public void setParityCheck(String parityCheck) {
        this.parityCheck = parityCheck;
    }

    public String getCom() {
        return this.com;
    }

    public void setCom(String com) {
        this.com = com;
    }

    public String getStopBit() {
        return this.stopBit;
    }

    public void setStopBit(String stopBit) {
        this.stopBit = stopBit;
    }

    public String getBeginSymbol() {
        return this.beginSymbol;
    }

    public void setBeginSymbol(String beginSymbol) {
        this.beginSymbol = beginSymbol;
    }

    public String getEndSymbol() {
        return this.endSymbol;
    }

    public void setEndSymbol(String endSymbol) {
        this.endSymbol = endSymbol;
    }

    public String getAnalysisFormula() {
        return this.analysisFormula;
    }

    public void setAnalysisFormula(String analysisFormula) {
        this.analysisFormula = analysisFormula;
    }

    public String getEndFlag() {
        return this.endFlag;
    }

    public void setEndFlag(String endFlag) {
        this.endFlag = endFlag;
    }

    public String getIp() {
        return this.ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getPort() {
        return this.port;
    }

    public void setPort(String port) {
        this.port = port;
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
