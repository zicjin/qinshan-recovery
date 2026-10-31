package com.sunwayworld.framework.mybatis.mapper;

import java.util.Optional;

/** Immutable multi-column string filter used by the global mapper. */
public class MultiPropertyFilterPattern {
    private final String[] names;
    private final String value;
    private final boolean caseSensitive;
    public MultiPropertyFilterPattern(String[] names, String value, Boolean caseSensitive) {
        this.names = names;
        this.caseSensitive = Optional.ofNullable(caseSensitive).orElse(Boolean.TRUE).booleanValue();
        this.value = this.caseSensitive ? value : value.toUpperCase();
    }
    public String[] getNames() { return names; }
    public Object getValue() { return value; }
    public boolean getCaseSensitive() { return caseSensitive; }
}
