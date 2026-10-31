package com.sunwayworld.framework.data.page;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/** Package-preserving page contract from the recovered application. */
public interface Page<T> extends Serializable {
    int getTotalPages();
    int getNumberOfElements();
    boolean isFirst();
    boolean isLast();
    boolean hasNext();
    boolean hasPrevious();
    void setTotal(int total);
    int getTotal();
    void setPageSize(int pageSize);
    int getPageSize();
    void setPageNumber(int pageNumber);
    int getPageNumber();
    void setRows(List<T> rows);
    List<T> getRows();
    void setColumnTypeMap(Map<String, String> values);
    Map<String, String> getColumnTypeMap();
    void setOptionsList(List<String> values);
    List<String> getOptionsList();
    void setTotalMap(Map<String, Number> values);
    Map<String, Number> getTotalMap();
}
