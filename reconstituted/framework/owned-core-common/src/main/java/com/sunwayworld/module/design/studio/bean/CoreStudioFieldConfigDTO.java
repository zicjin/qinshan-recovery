/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.design.studio.bean;

import java.io.Serializable;
import javax.annotation.Nullable;

public class CoreStudioFieldConfigDTO
implements Serializable {
    private static final long serialVersionUID = 4593353814459070322L;
    private String field;
    private String type;
    @Nullable
    private Integer length;
    @Nullable
    private Integer precision;
    @Nullable
    private Integer scale;

    public String getField() {
        return this.field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getLength() {
        return this.length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

    public Integer getPrecision() {
        return this.precision;
    }

    public void setPrecision(Integer precision) {
        this.precision = precision;
    }

    public Integer getScale() {
        return this.scale;
    }

    public void setScale(Integer scale) {
        this.scale = scale;
    }
}
