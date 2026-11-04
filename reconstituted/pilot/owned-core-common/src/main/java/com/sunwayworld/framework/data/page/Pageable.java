/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.data.page;

import org.springframework.data.domain.Sort;

public interface Pageable
extends org.springframework.data.domain.Pageable {
    public static final int DEFAULT_FIRST_PAGE_NUMBER = 1;
    public static final int DEFAULT_PAGE_SIZE = 50;

    public void setPageNumber(int var1);

    public void setPageSize(int var1);

    public void setSort(Sort var1);

    public void setSumProperties(String ... var1);

    public String[] getSumProperties();

    public void setCountProperties(String ... var1);

    public String[] getCountProperties();

    public void setOptionsProperty(String var1);

    public String getOptionsProperty();

    public void setFilterJson(String var1);

    public String getFilterJson();

    public <T> T map(Class<T> var1);
}
