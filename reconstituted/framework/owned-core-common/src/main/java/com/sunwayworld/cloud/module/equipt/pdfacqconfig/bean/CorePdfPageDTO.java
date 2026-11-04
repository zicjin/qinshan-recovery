/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.equipt.pdfacqconfig.bean;

import com.sunwayworld.cloud.module.equipt.pdfacqconfig.bean.CorePdfAcqAreaDTO;
import com.sunwayworld.framework.data.annotation.Transient;
import java.io.Serializable;
import java.util.List;

public class CorePdfPageDTO
implements Serializable {
    @Transient
    private static final long serialVersionUID = 2387162104837812727L;
    private Long id;
    private Double width;
    private Double height;
    private Integer pageNumber;
    private Integer rotation;
    private String imageUrl;
    private List<CorePdfAcqAreaDTO> areas;

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public List<CorePdfAcqAreaDTO> getAreas() {
        return this.areas;
    }

    public void setAreas(List<CorePdfAcqAreaDTO> areas) {
        this.areas = areas;
    }
}
