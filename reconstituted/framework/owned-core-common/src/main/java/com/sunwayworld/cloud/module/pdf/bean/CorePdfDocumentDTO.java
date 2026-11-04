/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.pdf.bean;

import com.sunwayworld.cloud.module.pdf.bean.CorePageAreaDTO;
import java.io.Serializable;
import java.util.List;

public class CorePdfDocumentDTO
implements Serializable {
    private static final long serialVersionUID = 4183200672360935974L;
    private Long Id;
    private Double width;
    private Double height;
    private Integer pageNumber;
    private Integer rotation;
    private String imageUrl;
    private List<CorePageAreaDTO> areas;

    public Long getId() {
        return this.Id;
    }

    public void setId(Long id) {
        this.Id = id;
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

    public Integer getPageNumber() {
        return this.pageNumber;
    }

    public void setPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
    }

    public Integer getRotation() {
        return this.rotation;
    }

    public void setRotation(Integer rotation) {
        this.rotation = rotation;
    }

    public String getImageUrl() {
        return this.imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public List<CorePageAreaDTO> getAreas() {
        return this.areas;
    }

    public void setAreas(List<CorePageAreaDTO> areas) {
        this.areas = areas;
    }
}
