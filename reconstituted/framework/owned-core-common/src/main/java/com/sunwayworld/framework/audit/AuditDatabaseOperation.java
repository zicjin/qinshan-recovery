/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.audit;

public enum AuditDatabaseOperation {
    INSERT("sunway.audit.insert"),
    UPDATE("sunway.audit.update"),
    DELETE("sunway.audit.delete");

    private String propertyKey;

    private AuditDatabaseOperation(String propertyKey) {
        this.propertyKey = propertyKey;
    }

    public String getPropertyKey() {
        return this.propertyKey;
    }
}
