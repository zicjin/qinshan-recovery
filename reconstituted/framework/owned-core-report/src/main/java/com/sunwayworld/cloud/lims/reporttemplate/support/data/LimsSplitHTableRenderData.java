/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.lims.reporttemplate.support.data;

import com.sunwayworld.cloud.lims.reporttemplate.support.data.AbstractMergeTableRenderData;
import com.sunwayworld.cloud.lims.reporttemplate.support.data.LimsTemplateDataList;
import com.sunwayworld.cloud.lims.reporttemplate.support.data.LimsTemplateDataMap;

public class LimsSplitHTableRenderData
extends AbstractMergeTableRenderData {
    private Integer maxCellCount;
    private String subFiled;
    private String pivotField;
    private String titleIdField;
    private String titleField;
    private String valueField;
    private int[] mergeCellIndexArray;

    public LimsSplitHTableRenderData(LimsTemplateDataList<LimsTemplateDataMap> data) {
        super(data);
    }

    public Integer getMaxCellCount() {
        return this.maxCellCount;
    }

    public void setMaxCellCount(Integer maxCellCount) {
        this.maxCellCount = maxCellCount;
    }

    public String getPivotField() {
        return this.pivotField;
    }

    public String getTitleIdField() {
        return this.titleIdField;
    }

    public void setTitleIdField(String titleIdField) {
        this.titleIdField = titleIdField;
    }

    public void setPivotField(String pivotField) {
        this.pivotField = pivotField;
    }

    public String getSubFiled() {
        return this.subFiled;
    }

    public void setSubFiled(String subFiled) {
        this.subFiled = subFiled;
    }

    public String getTitleField() {
        return this.titleField;
    }

    public void setTitleField(String titleField) {
        this.titleField = titleField;
    }

    public String getValueField() {
        return this.valueField;
    }

    public void setValueField(String valueField) {
        this.valueField = valueField;
    }

    public int[] getMergeCellIndexArray() {
        return this.mergeCellIndexArray;
    }

    public void setMergeCellIndexArray(int[] mergeCellIndexArray) {
        this.mergeCellIndexArray = mergeCellIndexArray;
    }
}
