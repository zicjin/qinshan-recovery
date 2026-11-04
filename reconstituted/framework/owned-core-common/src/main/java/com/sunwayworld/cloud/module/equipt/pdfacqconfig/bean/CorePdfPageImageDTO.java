/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.equipt.pdfacqconfig.bean;

import java.io.Serializable;

public class CorePdfPageImageDTO
implements Serializable {
    private static final long serialVersionUID = 2539338967903031002L;
    private Double imageWidth;
    private Double imageHeight;
    private String imageName;

    public Double getImageWidth() {
        return this.imageWidth;
    }

    public void setImageWidth(Double imageWidth) {
        this.imageWidth = imageWidth;
    }

    public Double getImageHeight() {
        return this.imageHeight;
    }

    public void setImageHeight(Double imageHeight) {
        this.imageHeight = imageHeight;
    }

    public String getImageName() {
        return this.imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }
}
