/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.ai.template.bean;

import com.sunwayworld.cloud.module.ai.template.bean.CoreAiTemplateScanFieldDTO;
import com.sunwayworld.cloud.module.ai.template.bean.CoreAiTemplateScanTitleDTO;
import com.sunwayworld.framework.data.annotation.Transient;
import java.io.Serializable;
import java.util.List;

public class CoreAiTemplateDefineDTO
implements Serializable {
    @Transient
    private static final long serialVersionUID = 5967665980307653923L;
    private List<CoreAiTemplateScanTitleDTO> benchmarks;
    private List<CoreAiTemplateScanFieldDTO> fixFields;

    public List<CoreAiTemplateScanTitleDTO> getBenchmarks() {
        return this.benchmarks;
    }

    public void setBenchmarks(List<CoreAiTemplateScanTitleDTO> benchmarks) {
        this.benchmarks = benchmarks;
    }

    public List<CoreAiTemplateScanFieldDTO> getFixFields() {
        return this.fixFields;
    }

    public void setFixFields(List<CoreAiTemplateScanFieldDTO> fixFields) {
        this.fixFields = fixFields;
    }
}
