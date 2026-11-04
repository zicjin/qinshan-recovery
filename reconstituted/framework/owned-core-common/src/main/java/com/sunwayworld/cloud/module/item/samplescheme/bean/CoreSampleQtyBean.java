/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.samplescheme.bean;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractPersistable;
import com.sunwayworld.framework.support.domain.Persistable;

@Table(value="T_CORE_SAMPLE_QTY")
public class CoreSampleQtyBean
extends AbstractPersistable<String>
implements Persistable<String> {
    @Transient
    private static final long serialVersionUID = 2092602800858011397L;
    @Id
    private String id;
    private Long sampleQty;

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    public Long getSampleQty() {
        return this.sampleQty;
    }

    public void setSampleQty(Long sampleQty) {
        this.sampleQty = sampleQty;
    }
}
