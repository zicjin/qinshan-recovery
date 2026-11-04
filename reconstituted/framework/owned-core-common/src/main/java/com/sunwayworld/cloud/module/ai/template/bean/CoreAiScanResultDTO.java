/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.ai.template.bean;

import com.sunwayworld.cloud.module.ai.template.bean.CoreAiTemplateFieldDTO;
import com.sunwayworld.framework.data.annotation.Transient;
import java.io.Serializable;
import java.util.List;

public class CoreAiScanResultDTO
implements Serializable {
    @Transient
    private static final long serialVersionUID = -6159118558657375048L;
    private List<CoreAiTemplateFieldDTO> result;

    public List<CoreAiTemplateFieldDTO> getResult() {
        return this.result;
    }

    public void setResult(List<CoreAiTemplateFieldDTO> result) {
        this.result = result;
    }
}
