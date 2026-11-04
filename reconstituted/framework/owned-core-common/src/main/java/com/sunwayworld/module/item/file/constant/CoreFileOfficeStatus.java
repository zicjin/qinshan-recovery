/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.item.file.constant;

public enum CoreFileOfficeStatus {
    NOTFOUND(0),
    EDIT(1),
    EXIT_SAVE(2),
    SAVE_ERROR(3),
    EXIT(4),
    SAVE(6),
    FORCE_SAVE_ERROR(7);

    private int statusCode;

    private CoreFileOfficeStatus(int statusCode) {
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return this.statusCode;
    }
}
