/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.lims.reporttemplate.support.data;

import com.sunwayworld.cloud.lims.reporttemplate.support.data.AbstractMergeTableRenderData;
import com.sunwayworld.cloud.lims.reporttemplate.support.data.LimsTemplateDataList;
import com.sunwayworld.cloud.lims.reporttemplate.support.data.LimsTemplateDataMap;
import java.util.List;

public class LimsMergeVTableRenderData
extends AbstractMergeTableRenderData {
    private int[] mergeCellIndexArray;
    private List<Integer> notMergeByFiledIndexArray;
    private String mergeByField;
    private boolean blankValueMergeFlag = true;

    public LimsMergeVTableRenderData(LimsTemplateDataList<LimsTemplateDataMap> data) {
        super(data);
    }

    public LimsMergeVTableRenderData(LimsTemplateDataList<LimsTemplateDataMap> data, int[] mergeCellIndexArray) {
        super(data);
        this.mergeCellIndexArray = mergeCellIndexArray;
    }

    public LimsMergeVTableRenderData(LimsTemplateDataList<LimsTemplateDataMap> data, boolean blankValueMergeFlag, int[] mergeCellIndexArray) {
        super(data);
        this.mergeCellIndexArray = mergeCellIndexArray;
        this.blankValueMergeFlag = blankValueMergeFlag;
    }

    public int[] getMergeCellIndexArray() {
        return this.mergeCellIndexArray;
    }

    public void setMergeCellIndexArray(int[] mergeCellIndexArray) {
        this.mergeCellIndexArray = mergeCellIndexArray;
    }

    public String getMergeByField() {
        return this.mergeByField;
    }

    public void setMergeByField(String mergeByField) {
        this.mergeByField = mergeByField;
    }

    public boolean isBlankValueMergeFlag() {
        return this.blankValueMergeFlag;
    }

    public void setBlankValueMergeFlag(boolean blankValueMergeFlag) {
        this.blankValueMergeFlag = blankValueMergeFlag;
    }

    public List<Integer> getNotMergeByFiledIndexArray() {
        return this.notMergeByFiledIndexArray;
    }

    public void setNotMergeByFiledIndexArray(List<Integer> notMergeByFiledIndexArray) {
        this.notMergeByFiledIndexArray = notMergeByFiledIndexArray;
    }
}
