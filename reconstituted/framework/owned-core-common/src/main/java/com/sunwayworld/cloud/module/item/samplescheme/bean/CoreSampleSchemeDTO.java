/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.samplescheme.bean;

public class CoreSampleSchemeDTO {
    private Long sampleQty;
    private Long acceptQty;
    private Long refuseQty;
    private Double aql;
    private String sampleQtyCode;

    public Long getSampleQty() {
        return this.sampleQty;
    }

    public void setSampleQty(Long sampleQty) {
        this.sampleQty = sampleQty;
    }

    public Long getAcceptQty() {
        return this.acceptQty;
    }

    public void setAcceptQty(Long acceptQty) {
        this.acceptQty = acceptQty;
    }

    public Long getRefuseQty() {
        return this.refuseQty;
    }

    public void setRefuseQty(Long refuseQty) {
        this.refuseQty = refuseQty;
    }

    public Double getAql() {
        return this.aql;
    }

    public void setAql(Double aql) {
        this.aql = aql;
    }

    public String getSampleQtyCode() {
        return this.sampleQtyCode;
    }

    public void setSampleQtyCode(String sampleQtyCode) {
        this.sampleQtyCode = sampleQtyCode;
    }
}
