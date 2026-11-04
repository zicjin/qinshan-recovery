/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.audit.handler;

import com.sunwayworld.framework.audit.handler.AuditV2RelationTableHandlerContext;
import java.util.Objects;

public interface AuditV2RelationTableDaoHandler {
    public void handleInsert(AuditV2RelationTableHandlerContext var1);

    public void handleDelete(AuditV2RelationTableHandlerContext var1);

    public String tableName();

    default public boolean match(String tableName) {
        return Objects.equals(tableName, this.tableName());
    }
}
