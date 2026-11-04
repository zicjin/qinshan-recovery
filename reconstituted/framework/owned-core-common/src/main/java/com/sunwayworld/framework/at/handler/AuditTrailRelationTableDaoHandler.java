/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.at.handler;

import com.sunwayworld.framework.at.handler.RelationTableHandlerContext;
import java.util.Objects;

public interface AuditTrailRelationTableDaoHandler {
    public void handleInsert(RelationTableHandlerContext var1);

    public void handleDelete(RelationTableHandlerContext var1);

    public String tableName();

    default public boolean match(String tableName) {
        return Objects.equals(tableName, this.tableName());
    }
}
