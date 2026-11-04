/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.ai.template.bean;

import com.sunwayworld.framework.data.annotation.Transient;
import java.io.Serializable;

public class CoreAiTemplateLocationDTO
implements Serializable {
    @Transient
    private static final long serialVersionUID = 6885665073240821632L;
    private Long top;
    private Long left;
    private Long width;
    private Long height;

    public Long getTop() {
        return this.top;
    }

    public void setTop(Long top) {
        this.top = top;
    }

    public Long getLeft() {
        return this.left;
    }

    public void setLeft(Long left) {
        this.left = left;
    }

    public Long getWidth() {
        return this.width;
    }

    public void setWidth(Long width) {
        this.width = width;
    }

    public Long getHeight() {
        return this.height;
    }

    public void setHeight(Long height) {
        this.height = height;
    }
}
