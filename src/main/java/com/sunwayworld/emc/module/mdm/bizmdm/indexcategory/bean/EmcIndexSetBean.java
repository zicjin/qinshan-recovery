package com.sunwayworld.emc.module.mdm.bizmdm.indexcategory.bean;

import java.time.LocalDate;

import com.alibaba.fastjson.annotation.JSONField;
import org.springframework.format.annotation.DateTimeFormat;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractInsertable;
import com.sunwayworld.framework.support.domain.Insertable;

/**
 * 指标设置
 *
 * @author zhangsy@sunwayworld.com 2019/9/16
 *
 */
@Table("T_EMC_INDEX_SET")
public class EmcIndexSetBean extends AbstractInsertable<Long>implements Insertable<Long> {

    @Transient
    private static final long serialVersionUID = -605876556783520216L;

    @Id
    private Long id; // id<主键>

    private String indexId; // 样品指标id

    private String evalIndex; // 评价指标

    private Long indexItemId; // 指标项目

    private Long indexCategoryId; // 指标分类id

    private String indexLowLimit; // 指标低限

    private String indexHighLimit; // 指标高限

    private String textIndexValue; // 文本指标值

    private String unit; // 单位

    private String applyScope; // 适用范围

    private Long sortNo; // 排序码

    private String createdById; // 制单人Id

    private String createdByName; // 制单人名称

    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDate createDate; // 制单时间

    private String createdByOrgId; // 制单人单位编码

    private String createdByOrgName; // 制单人单位名称

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIndexId() {
        return indexId;
    }

    public void setIndexId(String indexId) {
        this.indexId = indexId;
    }

    public String getEvalIndex() {
        return evalIndex;
    }

    public void setEvalIndex(String evalIndex) {
        this.evalIndex = evalIndex;
    }

    public Long getIndexItemId() {
        return indexItemId;
    }

    public void setIndexItemId(Long indexItemId) {
        this.indexItemId = indexItemId;
    }

    public Long getIndexCategoryId() {
        return indexCategoryId;
    }

    public void setIndexCategoryId(Long indexCategoryId) {
        this.indexCategoryId = indexCategoryId;
    }

    public String getIndexLowLimit() {
        return indexLowLimit;
    }

    public void setIndexLowLimit(String indexLowLimit) {
        this.indexLowLimit = indexLowLimit;
    }

    public String getIndexHighLimit() {
        return indexHighLimit;
    }

    public void setIndexHighLimit(String indexHighLimit) {
        this.indexHighLimit = indexHighLimit;
    }

    public String getTextIndexValue() {
        return textIndexValue;
    }

    public void setTextIndexValue(String textIndexValue) {
        this.textIndexValue = textIndexValue;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getApplyScope() {
        return applyScope;
    }

    public void setApplyScope(String applyScope) {
        this.applyScope = applyScope;
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

    public LocalDate getCreateDate() {
        return createDate;
    }

    public void setCreateDate(LocalDate createDate) {
        this.createDate = createDate;
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

    public Long getSortNo() {
        return sortNo;
    }

    public void setSortNo(Long sortNo) {
        this.sortNo = sortNo;
    }

}
