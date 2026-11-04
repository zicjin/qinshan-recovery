/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.mybatis.mapper;

import javax.annotation.Nullable;

public class AdvancedFilterPattern {
    private boolean groupStart = false;
    private boolean groupEnd = false;
    @Nullable
    private String groupLink;
    @Nullable
    private String ingroupLink;
    private String name;
    private Object value;
    private String matchPattern;

    public AdvancedFilterPattern(String name, Object value, String matchPattern) {
        this.name = name;
        this.value = value;
        this.matchPattern = matchPattern;
    }

    public boolean isGroupStart() {
        return this.groupStart;
    }

    public void setGroupStart(boolean groupStart) {
        this.groupStart = groupStart;
    }

    public boolean isGroupEnd() {
        return this.groupEnd;
    }

    public void setGroupEnd(boolean groupEnd) {
        this.groupEnd = groupEnd;
    }

    public String getGroupLink() {
        return this.groupLink;
    }

    public void setGroupLink(String groupLink) {
        this.groupLink = groupLink;
    }

    public String getIngroupLink() {
        return this.ingroupLink;
    }

    public void setIngroupLink(String ingroupLink) {
        this.ingroupLink = ingroupLink;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getValue() {
        return this.value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String getMatchPattern() {
        return this.matchPattern;
    }

    public void setMatchPattern(String matchPattern) {
        this.matchPattern = matchPattern;
    }
}
