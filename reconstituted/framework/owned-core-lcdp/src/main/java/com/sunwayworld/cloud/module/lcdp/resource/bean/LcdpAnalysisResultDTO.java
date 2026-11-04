/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.resource.bean;

import com.sunwayworld.framework.support.domain.AbstractBaseData;
import java.util.List;

public class LcdpAnalysisResultDTO
extends AbstractBaseData {
    private static final long serialVersionUID = 23885414354209415L;
    private Boolean enable;
    private List<String> analysisResultList;

    public Boolean getEnable() {
        return this.enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    public List<String> getAnalysisResultList() {
        return this.analysisResultList;
    }

    public void setAnalysisResultList(List<String> analysisResultList) {
        this.analysisResultList = analysisResultList;
    }
}
