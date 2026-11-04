/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.samplescheme.bean;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractPersistable;
import com.sunwayworld.framework.support.domain.Persistable;

@Table(value="T_CORE_SAMPLE_SCHEME")
public class CoreSampleSchemeBean
extends AbstractPersistable<Long>
implements Persistable<Long> {
    @Transient
    private static final long serialVersionUID = -4851886031081922370L;
    @Id
    private Long id;
    private String sampleQtyCode;
    private Double aql;
    private Long acceptQty;
    private Long refuseQty;

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getSampleQtyCode() {
        return this.sampleQtyCode;
    }

    public void setSampleQtyCode(String sampleQtyCode) {
        this.sampleQtyCode = sampleQtyCode;
    }

    public Double getAql() {
        return this.aql;
    }

    public void setAql(Double aql) {
        this.aql = aql;
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
}
