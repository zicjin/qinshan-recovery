/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.audit.handler;

import com.sunwayworld.framework.support.domain.Persistable;
import java.util.List;

public class AuditV2RelationTableHandlerContext {
    private final List<? extends Persistable<?>> items;

    public AuditV2RelationTableHandlerContext(List<? extends Persistable<?>> items) {
        this.items = items;
    }

    @SuppressWarnings("unchecked")
    public <T extends Persistable<?>> List<T> getItems() {
        return (List<T>)this.items;
    }
}
