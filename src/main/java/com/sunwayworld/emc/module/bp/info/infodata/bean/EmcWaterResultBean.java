package com.sunwayworld.emc.module.bp.info.infodata.bean;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.alibaba.fastjson.annotation.JSONField;
import org.springframework.format.annotation.DateTimeFormat;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractPersistable;
import com.sunwayworld.framework.support.domain.Insertable;

@Table("T_EMC_WATER_RESULT")
public class EmcWaterResultBean extends AbstractPersistable<Long> implements Insertable<Long> {

    @Transient
    private static final long serialVersionUID = 5709798382959919368L;

    @Id
    private Long id;// 主键

    private Long infoConfigId; // 综合配置编号

    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate monitorDate; // 监测日期

    private String monitorPointName; // 监测点名称

    private String year; // 年

    private String month; // 月份

    private String day; // 日

    private String averageResult; // 均值结果

    private String waterQualityCategory; // 水质类别

    private String pullyteIndex; // 污染指数

    private String exceedStandard; // 是否超标

    private String overMulTime; // 超标倍数

    private String eutrophication; // 富营养指数

    private String itemAliasName; // 分析项别名

    private String type; // 均值类型

    private String createdById; // 制单人编码

    private String createdByName; // 制单人名称

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime; // 制单时间

    private String createdByOrgId; // 制单人单位编码

    private String createdByOrgName; // 制单人单位名称

    public Long getId() {
        return id;
    }

    public Long getInfoConfigId() {
        return infoConfigId;
    }

    public LocalDate getMonitorDate() {
        return monitorDate;
    }

    public String getMonitorPointName() {
        return monitorPointName;
    }

    public String getYear() {
        return year;
    }

    public String getMonth() {
        return month;
    }

    public String getDay() {
        return day;
    }

    public String getAverageResult() {
        return averageResult;
    }

    public String getWaterQualityCategory() {
        return waterQualityCategory;
    }

    public String getPullyteIndex() {
        return pullyteIndex;
    }

    public String getExceedStandard() {
        return exceedStandard;
    }

    public String getOverMulTime() {
        return overMulTime;
    }

    public String getEutrophication() {
        return eutrophication;
    }

    public String getItemAliasName() {
        return itemAliasName;
    }

    public String getType() {
        return type;
    }

    public String getCreatedById() {
        return createdById;
    }

    public String getCreatedByName() {
        return createdByName;
    }

    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    public String getCreatedByOrgId() {
        return createdByOrgId;
    }

    public String getCreatedByOrgName() {
        return createdByOrgName;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setInfoConfigId(Long infoConfigId) {
        this.infoConfigId = infoConfigId;
    }

    public void setMonitorDate(LocalDate monitorDate) {
        this.monitorDate = monitorDate;
    }

    public void setMonitorPointName(String monitorPointName) {
        this.monitorPointName = monitorPointName;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public void setAverageResult(String averageResult) {
        this.averageResult = averageResult;
    }

    public void setWaterQualityCategory(String waterQualityCategory) {
        this.waterQualityCategory = waterQualityCategory;
    }

    public void setPullyteIndex(String pullyteIndex) {
        this.pullyteIndex = pullyteIndex;
    }

    public void setExceedStandard(String exceedStandard) {
        this.exceedStandard = exceedStandard;
    }

    public void setOverMulTime(String overMulTime) {
        this.overMulTime = overMulTime;
    }

    public void setEutrophication(String eutrophication) {
        this.eutrophication = eutrophication;
    }

    public void setItemAliasName(String itemAliasName) {
        this.itemAliasName = itemAliasName;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setCreatedById(String createdById) {
        this.createdById = createdById;
    }

    public void setCreatedByName(String createdByName) {
        this.createdByName = createdByName;
    }

    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }

    public void setCreatedByOrgId(String createdByOrgId) {
        this.createdByOrgId = createdByOrgId;
    }

    public void setCreatedByOrgName(String createdByOrgName) {
        this.createdByOrgName = createdByOrgName;
    }

}
