package com.sunwayworld.emc.module.mdm.bizmdm.indexcategory.bean;

import java.time.LocalDate;

import com.alibaba.fastjson.annotation.JSONField;
import org.springframework.format.annotation.DateTimeFormat;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.support.domain.AbstractInsertable;

/**
 * 指标预览
 *
 * @author guok@sunwayworld.com 2020/02/05
 *
 */

@Table("T_EMC_INDEX_PREVIEW")
public class EmcIndexPreviewBean extends AbstractInsertable<Long> {

    private static final long serialVersionUID = -6747447774514779613L;

    @Id
    private Long id; // 主键

    private String stdName; // 标准名称

    @JSONField(format = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate expireDate; // 过期日期

    private String index1; // 指标1

    private String index2; // 指标2

    private Long indexItemId; // 指标项目

    private String indexLowLimit; // 指标低限

    private String indexHighLimit; // 指标高限

    private String textIndexValue; // 文本指标值

    private String unit; // 单位

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStdName() {
        return stdName;
    }

    public void setStdName(String stdName) {
        this.stdName = stdName;
    }

    public LocalDate getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(LocalDate expireDate) {
        this.expireDate = expireDate;
    }

    public String getIndex1() {
        return index1;
    }

    public void setIndex1(String index1) {
        this.index1 = index1;
    }

    public String getIndex2() {
        return index2;
    }

    public void setIndex2(String index2) {
        this.index2 = index2;
    }

    public Long getIndexItemId() {
        return indexItemId;
    }

    public void setIndexItemId(Long indexItemId) {
        this.indexItemId = indexItemId;
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
}
