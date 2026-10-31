package com.sunwayworld.emc.module.mdm.bizmdm.monitortmpl.bean;

import com.alibaba.fastjson.annotation.JSONField;
import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractPersistable;
import com.sunwayworld.framework.support.domain.Insertable;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Table("T_EMC_MONITOR_SCHEME_COST")
public class EmcMonitorSchemeCostBean extends AbstractPersistable<Long> implements Insertable<Long> {

    @Transient
    private static final long serialVersionUID = 4010848845702991409L;

    @Id
    private Long id;// 主键

    private String monitorTmplId;// 监测模板ID
    private String schemeId;// 监测方案ID
    private String schemeName;// 监测方案名称
    private Double cost;// 监测费用

    private Long sortNum; // 排序码

    private Integer period; // 周期
    private Long techPrice; // 技术指导价
    private Long salePrice; // 销售指导价
    private String multipleFactor;  // 是否为多因子套餐
    private String samplingFeeType; // 采样费类型
    private String analyzeFeeType; // 分析费类型
    private String analyzeFeeFactor; // 分析费系数
    private String analyzeFeeMaximum; // 最高收取
    private String skuGbId; // SKUID
    private String skuQuoteGbId; // 报价ID

    private String createdById;// 制单人编码
    private String createdByName;// 制单人名称
    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;// 制单时间
    private String createdByOrgId;// 制单人单位编码
    private String createdByOrgName;// 制单人单位名称

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getMonitorTmplId() {
        return monitorTmplId;
    }

    public void setMonitorTmplId(String monitorTmplId) {
        this.monitorTmplId = monitorTmplId;
    }

    public String getSchemeId() {
        return schemeId;
    }

    public void setSchemeId(String schemeId) {
        this.schemeId = schemeId;
    }

    public String getSchemeName() {
        return schemeName;
    }

    public void setSchemeName(String schemeName) {
        this.schemeName = schemeName;
    }

    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }

    public Long getSortNum() {
        return sortNum;
    }

    public void setSortNum(Long sortNum) {
        this.sortNum = sortNum;
    }

    public Integer getPeriod() {
        return period;
    }

    public void setPeriod(Integer period) {
        this.period = period;
    }

    public Long getTechPrice() {
        return techPrice;
    }

    public void setTechPrice(Long techPrice) {
        this.techPrice = techPrice;
    }

    public Long getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(Long salePrice) {
        this.salePrice = salePrice;
    }

    public String getMultipleFactor() {
        return multipleFactor;
    }

    public void setMultipleFactor(String multipleFactor) {
        this.multipleFactor = multipleFactor;
    }

    public String getSamplingFeeType() {
        return samplingFeeType;
    }

    public void setSamplingFeeType(String samplingFeeType) {
        this.samplingFeeType = samplingFeeType;
    }

    public String getAnalyzeFeeType() {
        return analyzeFeeType;
    }

    public void setAnalyzeFeeType(String analyzeFeeType) {
        this.analyzeFeeType = analyzeFeeType;
    }

    public String getAnalyzeFeeFactor() {
        return analyzeFeeFactor;
    }

    public void setAnalyzeFeeFactor(String analyzeFeeFactor) {
        this.analyzeFeeFactor = analyzeFeeFactor;
    }

    public String getAnalyzeFeeMaximum() {
        return analyzeFeeMaximum;
    }

    public void setAnalyzeFeeMaximum(String analyzeFeeMaximum) {
        this.analyzeFeeMaximum = analyzeFeeMaximum;
    }

    public String getSkuGbId() {
        return skuGbId;
    }

    public void setSkuGbId(String skuGbId) {
        this.skuGbId = skuGbId;
    }

    public String getSkuQuoteGbId() {
        return skuQuoteGbId;
    }

    public void setSkuQuoteGbId(String skuQuoteGbId) {
        this.skuQuoteGbId = skuQuoteGbId;
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
