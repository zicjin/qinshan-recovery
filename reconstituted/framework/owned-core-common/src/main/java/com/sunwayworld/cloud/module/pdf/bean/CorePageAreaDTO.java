/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.pdf.bean;

import java.io.Serializable;

public class CorePageAreaDTO
implements Serializable {
    private static final long serialVersionUID = 8402154176144768776L;
    private Long id;
    private Double x;
    private Double y;
    private Double scale;
    private Double width;
    private Double height;
    private String comment;

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getX() {
        return this.x;
    }

    public void setX(Double x) {
        this.x = x;
    }

    public Double getY() {
        return this.y;
    }

    public void setY(Double y) {
        this.y = y;
    }

    public Double getScale() {
        return this.scale;
    }

    public void setScale(Double scale) {
        this.scale = scale;
    }

    public Double getWidth() {
        return this.width;
    }

    public void setWidth(Double width) {
        this.width = width;
    }

    public Double getHeight() {
        return this.height;
    }

    public void setHeight(Double height) {
        this.height = height;
    }

    public String getComment() {
        return this.comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
