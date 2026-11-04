/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.spring.filter;

import com.sunwayworld.framework.spring.filter.GikamFilterConfig;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.Filter;

public enum GikamFilterRegistry {
    INSTANCE;

    private List<GikamFilterConfig> configList = new ArrayList<GikamFilterConfig>();

    public void registerAfter(Filter filter, Class<? extends Filter> afterFilter) {
        this.configList.add(GikamFilterConfig.of(filter, afterFilter, GikamFilterConfig.FilterPos.AFTER));
    }

    public void registerBefore(Filter filter, Class<? extends Filter> beforeFilter) {
        this.configList.add(GikamFilterConfig.of(filter, beforeFilter, GikamFilterConfig.FilterPos.BEFORE));
    }

    public void registerAt(Filter filter, Class<? extends Filter> atFilter) {
        this.configList.add(GikamFilterConfig.of(filter, atFilter, GikamFilterConfig.FilterPos.AT));
    }

    public List<GikamFilterConfig> getConfigList() {
        return this.configList;
    }
}
