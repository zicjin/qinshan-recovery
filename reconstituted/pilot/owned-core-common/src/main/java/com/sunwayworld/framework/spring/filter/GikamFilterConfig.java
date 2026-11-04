/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.spring.filter;

import javax.servlet.Filter;

public class GikamFilterConfig {
    private Filter filter;
    private Class<? extends Filter> filterClass;
    private FilterPos filterPos;

    public static GikamFilterConfig of(Filter filter, Class<? extends Filter> filterClass, FilterPos filterPos) {
        GikamFilterConfig instance = new GikamFilterConfig();
        instance.setFilter(filter);
        instance.setFilterClass(filterClass);
        instance.setFilterPos(filterPos);
        return instance;
    }

    public Filter getFilter() {
        return this.filter;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }

    public Class<? extends Filter> getFilterClass() {
        return this.filterClass;
    }

    public void setFilterClass(Class<? extends Filter> filterClass) {
        this.filterClass = filterClass;
    }

    public FilterPos getFilterPos() {
        return this.filterPos;
    }

    public void setFilterPos(FilterPos filterPos) {
        this.filterPos = filterPos;
    }

    public static enum FilterPos {
        BEFORE,
        AT,
        AFTER;

    }
}
