package com.sunwayworld.emc.module.mdm.equipt.equiptacqscript.bean;

import java.time.LocalDateTime;

import com.alibaba.fastjson.annotation.JSONField;
import org.springframework.format.annotation.DateTimeFormat;

import com.sunwayworld.emc.framework.constant.EmcConstant;
import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.NotNull;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractPersistable;
import com.sunwayworld.framework.support.domain.Insertable;

/**
 * 设备台账-仪器采集脚本配置(T_EMC_EQUIPT_ACQ_SCRIPT)
 *
 */
@Table("T_EMC_EQUIPT_ACQ_SCRIPT")
public class EmcEquiptAcqScriptBean extends AbstractPersistable<Long> implements Insertable<Long> {

    @Transient
    private static final long serialVersionUID = 7455032350715272800L;

    @Id
    private Long id; // 主键

    private String scriptDesc; // 配置描述

    private String dataFormat; // 数据格式 下拉列表equiptAcqDataFormat

    private String orderNoFormat; // 样品编号格式 下拉列表equiptAcqOrderNoFormat

    private String resultFormat; // 仪器采购结果格式 下拉列表equiptAcqResultFormat

    private String testFormat; // 分析项名称是否手动配置（no根据配置的列号取，yes直接取结果编码名称） 下拉列表yesorno

    private String rowDataSeparator; // 行数据分隔符

    @NotNull(defaultValue = EmcConstant.ANALYSIS_WAY_PRIOR_SEQUENCE)
    private String analysisWay; // 解析方式 下拉列表analysisWay

    private String source; // 采集来源

    private String createdById; // 制单人编码

    private String createdByName; // 制单人名称

    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime; // 制单时间

    private String createdByOrgId; // 制单人单位编码

    private String createdByOrgName; // 制单人单位名称

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getScriptDesc() {
        return scriptDesc;
    }

    public void setScriptDesc(String scriptDesc) {
        this.scriptDesc = scriptDesc;
    }

    public String getDataFormat() {
        return dataFormat;
    }

    public void setDataFormat(String dataFormat) {
        this.dataFormat = dataFormat;
    }

    public String getOrderNoFormat() {
        return orderNoFormat;
    }

    public void setOrderNoFormat(String orderNoFormat) {
        this.orderNoFormat = orderNoFormat;
    }

    public String getResultFormat() {
        return resultFormat;
    }

    public String getTestFormat() {
        return testFormat;
    }

    public void setTestFormat(String testFormat) {
        this.testFormat = testFormat;
    }

    public void setResultFormat(String resultFormat) {
        this.resultFormat = resultFormat;
    }

    public String getRowDataSeparator() {
        return rowDataSeparator;
    }

    public void setRowDataSeparator(String rowDataSeparator) {
        this.rowDataSeparator = rowDataSeparator;
    }

    public String getAnalysisWay() {
        return analysisWay;
    }

    public void setAnalysisWay(String analysisWay) {
        this.analysisWay = analysisWay;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    @Override
    public String getCreatedById() {
        return createdById;
    }

    @Override
    public void setCreatedById(String createdById) {
        this.createdById = createdById;
    }

    @Override
    public String getCreatedByName() {
        return createdByName;
    }

    @Override
    public void setCreatedByName(String createdByName) {
        this.createdByName = createdByName;
    }

    @Override
    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    @Override
    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }

    @Override
    public String getCreatedByOrgId() {
        return createdByOrgId;
    }

    @Override
    public void setCreatedByOrgId(String createdByOrgId) {
        this.createdByOrgId = createdByOrgId;
    }

    @Override
    public String getCreatedByOrgName() {
        return createdByOrgName;
    }

    @Override
    public void setCreatedByOrgName(String createdByOrgName) {
        this.createdByOrgName = createdByOrgName;
    }
}
