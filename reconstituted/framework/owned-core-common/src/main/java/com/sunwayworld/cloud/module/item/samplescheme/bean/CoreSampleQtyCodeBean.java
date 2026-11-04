/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.samplescheme.bean;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractPersistable;
import com.sunwayworld.framework.support.domain.Persistable;

@Table(value="T_CORE_SAMPLE_QTY_CODE")
public class CoreSampleQtyCodeBean
extends AbstractPersistable<Long>
implements Persistable<Long> {
    @Transient
    private static final long serialVersionUID = -5828106459147202746L;
    @Id
    private Long id;
    private Long batchQtyUpperLimit;
    private Long batchQtyLowerLimit;
    private String inspection;
    private String sampleQtyCode;

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public Long getBatchQtyUpperLimit() {
        return this.batchQtyUpperLimit;
    }

    public void setBatchQtyUpperLimit(Long batchQtyUpperLimit) {
        this.batchQtyUpperLimit = batchQtyUpperLimit;
    }

    public Long getBatchQtyLowerLimit() {
        return this.batchQtyLowerLimit;
    }

    public void setBatchQtyLowerLimit(Long batchQtyLowerLimit) {
        this.batchQtyLowerLimit = batchQtyLowerLimit;
    }

    public String getInspection() {
        return this.inspection;
    }

    public void setInspection(String inspection) {
        this.inspection = inspection;
    }

    public String getSampleQtyCode() {
        return this.sampleQtyCode;
    }

    public void setSampleQtyCode(String sampleQtyCode) {
        this.sampleQtyCode = sampleQtyCode;
    }
}
