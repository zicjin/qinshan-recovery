/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.database.sql;

import java.util.Objects;

public class Order {
    private final String column;
    private final String direction;

    private Order(String column, String direction) {
        this.column = column;
        this.direction = direction;
    }

    public static final Order asc(String column) {
        return new Order(Objects.requireNonNull(column), " ASC");
    }

    public static final Order desc(String column) {
        return new Order(Objects.requireNonNull(column), " DESC");
    }

    public String getColumn() {
        return this.column;
    }

    public String getDirection() {
        return this.direction;
    }
}
