/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.ai.template.bean;

import com.sunwayworld.cloud.module.ai.template.bean.CoreAiTemplateLocationDTO;
import com.sunwayworld.framework.data.annotation.Transient;
import java.io.Serializable;

public class CoreAiTemplateScanFieldDTO
implements Serializable {
    @Transient
    private static final long serialVersionUID = -2032898651869791944L;
    private String id;
    private String key;
    private String type;
    private CoreAiTemplateLocationDTO rect;
    private String fieldResult;

    public String getKey() {
        return this.key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public CoreAiTemplateLocationDTO getRect() {
        return this.rect;
    }

    public void setRect(CoreAiTemplateLocationDTO rect) {
        this.rect = rect;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFieldResult() {
        return this.fieldResult;
    }

    public void setFieldResult(String fieldResult) {
        this.fieldResult = fieldResult;
    }
}
