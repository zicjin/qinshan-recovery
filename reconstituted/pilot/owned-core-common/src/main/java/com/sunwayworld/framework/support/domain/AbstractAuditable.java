/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.support.domain;

import com.sunwayworld.framework.data.annotation.NotNull;
import com.sunwayworld.framework.support.domain.AbstractPersistable;
import com.sunwayworld.framework.support.domain.Auditable;
import java.io.Serializable;

public abstract class AbstractAuditable<ID extends Serializable>
extends AbstractPersistable<ID>
implements Auditable<ID> {
    private static final long serialVersionUID = 7292228337884516484L;
    @NotNull(defaultValue="draft")
    private String processStatus;
    @NotNull(defaultValue="1")
    private Long version;

    @Override
    public String getProcessStatus() {
        return this.processStatus;
    }

    @Override
    public void setProcessStatus(String processStatus) {
        this.processStatus = processStatus;
    }

    @Override
    public Long getVersion() {
        return this.version;
    }

    @Override
    public void setVersion(Long version) {
        this.version = version;
    }
}
