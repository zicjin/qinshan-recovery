/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.lims.reporttemplate.support.data;

import com.sunwayworld.cloud.lims.reporttemplate.support.data.AbstractMergeTableRenderData;
import com.sunwayworld.cloud.lims.reporttemplate.support.data.LimsTemplateDataList;
import com.sunwayworld.cloud.lims.reporttemplate.support.data.LimsTemplateDataMap;

public class LimsExpandHVGroupTableRenderData
extends AbstractMergeTableRenderData {
    private String groupField;
    private int columnExpandNum;
    private String mergeRowRole;

    public LimsExpandHVGroupTableRenderData(LimsTemplateDataList<LimsTemplateDataMap> data, String groupField, int columnExpandNum, String mergeRowRole) {
        super(data);
        this.groupField = groupField;
        this.columnExpandNum = columnExpandNum;
        this.mergeRowRole = mergeRowRole;
    }

    public String getGroupField() {
        return this.groupField;
    }

    public void setGroupField(String groupField) {
        this.groupField = groupField;
    }

    public int getColumnExpandNum() {
        return this.columnExpandNum;
    }

    public void setColumnExpandNum(int columnExpandNum) {
        this.columnExpandNum = columnExpandNum;
    }

    public String getMergeRowRole() {
        return this.mergeRowRole;
    }

    public void setMergeRowRole(String mergeRowRole) {
        this.mergeRowRole = mergeRowRole;
    }
}
