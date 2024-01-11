package com.sunwayworld.emc.module.mdm.basemdm.unit.bean;

import java.time.LocalDateTime;

import com.alibaba.fastjson.annotation.JSONField;
import org.springframework.format.annotation.DateTimeFormat;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.support.domain.AbstractInsertable;
import com.sunwayworld.framework.support.domain.Insertable;

/**
 * 单位表
 *
 * @author yuanhh@sunwayworld.com 2019年9月9日
 */
@Table("T_EMC_UNIT")
public class EmcUnitBean extends AbstractInsertable<String> implements Insertable<String> {

    private static final long serialVersionUID = 1847260967138492882L;
    @Id
    private String id; // 单位编码<主键>
    private String unitName; // 单位名称
    private String unitFullName; // 单位全称
    private String unitShortName; // 单位简称
    private String unitCategory; // 单位类型
    private String standardUnit; // 是否标准单位
    private String basicUnit; // 是否基础度量单位
    private String calibrationUnit; // 是否仪器校准单位
    private Integer sortNum; // 排序号

    private String createdById;
    private String createdByName;
    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;
    private String createdByOrgId;
    private String createdByOrgName;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public String getUnitFullName() {
        return unitFullName;
    }

    public void setUnitFullName(String unitFullName) {
        this.unitFullName = unitFullName;
    }

    public String getUnitShortName() {
        return unitShortName;
    }

    public void setUnitShortName(String unitShortName) {
        this.unitShortName = unitShortName;
    }

    public String getStandardUnit() {
        return standardUnit;
    }

    public void setStandardUnit(String standardUnit) {
        this.standardUnit = standardUnit;
    }

    public String getBasicUnit() {
        return basicUnit;
    }

    public void setBasicUnit(String basicUnit) {
        this.basicUnit = basicUnit;
    }

    public String getCalibrationUnit() {
        return calibrationUnit;
    }

    public void setCalibrationUnit(String calibrationUnit) {
        this.calibrationUnit = calibrationUnit;
    }

    public Integer getSortNum() {
        return sortNum;
    }

    public void setSortNum(Integer sortNum) {
        this.sortNum = sortNum;
    }

    public String getCreatedById() {
        return createdById;
    }

    public void setCreatedById(String createdById) {
        this.createdById = createdById;
    }

    public String getCreatedByName() {
        return createdByName;
    }

    public void setCreatedByName(String createdByName) {
        this.createdByName = createdByName;
    }

    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }

    public String getCreatedByOrgId() {
        return createdByOrgId;
    }

    public void setCreatedByOrgId(String createdByOrgId) {
        this.createdByOrgId = createdByOrgId;
    }

    public String getCreatedByOrgName() {
        return createdByOrgName;
    }

    public void setCreatedByOrgName(String createdByOrgName) {
        this.createdByOrgName = createdByOrgName;
    }

    public String getUnitCategory() {
        return unitCategory;
    }

    public void setUnitCategory(String unitCategory) {
        this.unitCategory = unitCategory;
    }

}
