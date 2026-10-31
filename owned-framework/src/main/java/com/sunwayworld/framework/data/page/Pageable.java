package com.sunwayworld.framework.data.page;

import org.springframework.data.domain.Sort;

public interface Pageable extends org.springframework.data.domain.Pageable {
    int DEFAULT_FIRST_PAGE_NUMBER = 1;
    int DEFAULT_PAGE_SIZE = 50;
    void setPageNumber(int value);
    void setPageSize(int value);
    void setSort(Sort value);
    void setSumProperties(String... values);
    String[] getSumProperties();
    void setCountProperties(String... values);
    String[] getCountProperties();
    void setOptionsProperty(String value);
    String getOptionsProperty();
    void setFilterJson(String value);
    String getFilterJson();
    <T> T map(Class<T> type);
}
