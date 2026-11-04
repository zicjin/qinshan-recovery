/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.mybatis.mapper;

import java.util.HashMap;

public class GlobalBoundSql
extends HashMap<String, Object> {
    private static final long serialVersionUID = -777728708447584771L;
    private static final String SQL_KEY = "sql";

    private GlobalBoundSql(String sql) {
        this.put(SQL_KEY, sql);
    }

    public static GlobalBoundSql of(String sql) {
        return new GlobalBoundSql(sql);
    }
}
