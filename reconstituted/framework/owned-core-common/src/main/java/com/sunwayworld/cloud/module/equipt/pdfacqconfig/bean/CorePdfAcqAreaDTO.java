/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.equipt.pdfacqconfig.bean;

import com.sunwayworld.cloud.module.equipt.pdfacqconfig.bean.CorePdfAcqAreaResultParamDTO;
import com.sunwayworld.framework.data.annotation.Transient;
import java.io.Serializable;
import java.util.List;

public class CorePdfAcqAreaDTO
implements Serializable {
    @Transient
    private static final long serialVersionUID = 5791861865380043969L;
    private Long id;
    private Integer pageNumber;
    private String extractMethod;
    private Double topXAxis;
    private Double bottomXAxis;
    private Double topYAxis;
    private Double bottomYAxis;
    private Integer specIndex;
    private Double x;
    private Double y;
    private Double scale;
    private Double width;
    private Double height;
    private String parseType;
    private String split;
    private Boolean underlay;
    private Long group;
    private Long tableHeaderRowNum;
    private String tableDataDisplayWay;
    private List<CorePdfAcqAreaResultParamDTO> result;
    private List<List<String>> data;

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getPageNumber() {
        return this.pageNumber;
    }

    public void setPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
    }

    public String getExtractMethod() {
        return this.extractMethod;
    }

    public void setExtractMethod(String extractMethod) {
        this.extractMethod = extractMethod;
    }

    public Double getTopXAxis() {
        return this.topXAxis;
    }

    public void setTopXAxis(Double topXAxis) {
        this.topXAxis = topXAxis;
    }

    public Double getBottomXAxis() {
        return this.bottomXAxis;
    }

    public void setBottomXAxis(Double bottomXAxis) {
        this.bottomXAxis = bottomXAxis;
    }

    public Double getTopYAxis() {
        return this.topYAxis;
    }

    public void setTopYAxis(Double topYAxis) {
        this.topYAxis = topYAxis;
    }

    public Double getBottomYAxis() {
        return this.bottomYAxis;
    }

    public void setBottomYAxis(Double bottomYAxis) {
        this.bottomYAxis = bottomYAxis;
    }

    public Integer getSpecIndex() {
        return this.specIndex;
    }

    public void setSpecIndex(Integer specIndex) {
        this.specIndex = specIndex;
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

    public String getParseType() {
        return this.parseType;
    }

    public void setParseType(String parseType) {
        this.parseType = parseType;
    }

    public String getSplit() {
        return this.split;
    }

    public void setSplit(String split) {
        this.split = split;
    }

    public Boolean getUnderlay() {
        return this.underlay;
    }

    public void setUnderlay(Boolean underlay) {
        this.underlay = underlay;
    }

    public Long getGroup() {
        return this.group;
    }

    public void setGroup(Long group) {
        this.group = group;
    }

    public Long getTableHeaderRowNum() {
        return this.tableHeaderRowNum;
    }

    public void setTableHeaderRowNum(Long tableHeaderRowNum) {
        this.tableHeaderRowNum = tableHeaderRowNum;
    }

    public String getTableDataDisplayWay() {
        return this.tableDataDisplayWay;
    }

    public void setTableDataDisplayWay(String tableDataDisplayWay) {
        this.tableDataDisplayWay = tableDataDisplayWay;
    }

    public List<CorePdfAcqAreaResultParamDTO> getResult() {
        return this.result;
    }

    public void setResult(List<CorePdfAcqAreaResultParamDTO> result) {
        this.result = result;
    }

    public List<List<String>> getData() {
        return this.data;
    }

    public void setData(List<List<String>> data) {
        this.data = data;
    }
}
