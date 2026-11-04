/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.data.page;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public interface Page<T>
extends Serializable {
    public int getTotalPages();

    public int getNumberOfElements();

    public boolean isFirst();

    public boolean isLast();

    public boolean hasNext();

    public boolean hasPrevious();

    public void setTotal(int var1);

    public int getTotal();

    public void setPageSize(int var1);

    public int getPageSize();

    public void setPageNumber(int var1);

    public int getPageNumber();

    public void setRows(List<T> var1);

    public List<T> getRows();

    public void setColumnTypeMap(Map<String, String> var1);

    public Map<String, String> getColumnTypeMap();

    public void setOptionsList(List<String> var1);

    public List<String> getOptionsList();

    public void setTotalMap(Map<String, Number> var1);

    public Map<String, Number> getTotalMap();
}
