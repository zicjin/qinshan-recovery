/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.lims.reporttemplate.support.data;

import com.sunwayworld.cloud.lims.reporttemplate.support.data.LimsTemplateDataList;
import com.sunwayworld.cloud.lims.reporttemplate.support.data.LimsTemplateDataMap;

public abstract class AbstractMergeTableRenderData {
    private Boolean needHead = true;
    private LimsTemplateDataList<LimsTemplateDataMap> data;

    public AbstractMergeTableRenderData(LimsTemplateDataList<LimsTemplateDataMap> data) {
        this.data = data;
    }

    public LimsTemplateDataList<LimsTemplateDataMap> getData() {
        return this.data;
    }

    public void setData(LimsTemplateDataList<LimsTemplateDataMap> data) {
        this.data = data;
    }

    public Boolean getNeedHead() {
        return this.needHead;
    }

    public void setNeedHead(Boolean needHead) {
        this.needHead = needHead;
    }
}
