/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.mybatis.mapper;

import java.util.Optional;

public class MultiPropertyFilterPattern {
    private final String[] names;
    private final String value;
    private final boolean caseSensitive;

    public MultiPropertyFilterPattern(String[] names, String value, Boolean caseSensitive) {
        this.names = names;
        this.caseSensitive = Optional.ofNullable(caseSensitive).orElse(true);
        this.value = this.caseSensitive ? value : value.toUpperCase();
    }

    public String[] getNames() {
        return this.names;
    }

    public Object getValue() {
        return this.value;
    }

    public boolean getCaseSensitive() {
        return this.caseSensitive;
    }
}
