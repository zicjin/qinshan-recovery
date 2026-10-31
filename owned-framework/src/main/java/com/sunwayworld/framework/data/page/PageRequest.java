package com.sunwayworld.framework.data.page;

import com.alibaba.fastjson.JSONObject;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.utils.ClassUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.framework.utils.ConvertUtils;
import org.springframework.data.domain.Sort;

/** Mutable, one-based page request value used by the request and MyBatis seams. */
public class PageRequest implements Pageable {
    private int pageNumber = DEFAULT_FIRST_PAGE_NUMBER;
    private int pageSize = DEFAULT_PAGE_SIZE;
    private String filterJson;
    private String[] sumProperties;
    private String[] countProperties;
    private String optionsProperty;
    private Sort sort;

    public static PageRequest of(RestJsonWrapperBean wrapper) {
        if (wrapper == null) throw new NullPointerException("wrapper");
        PageRequest request = new PageRequest();
        request.setPageNumber(ConvertUtils.convert(wrapper.getParamValue("n"), int.class));
        request.setPageSize(ConvertUtils.convert(wrapper.getParamValue("s"), int.class));
        request.setFilterJson(wrapper.getParamValue("f"));
        request.setSumProperties(wrapper.getSumProperties());
        request.setCountProperties(wrapper.getCountProperties());
        request.setOptionsProperty(wrapper.getOptionsProperty());
        return request;
    }

    public static PageRequest of(int pageNumber, int pageSize) {
        PageRequest request = new PageRequest();
        request.setPageNumber(pageNumber);
        request.setPageSize(pageSize);
        return request;
    }

    public PageRequest() { }

    public int getPageNumber() {
        if (pageNumber == 0) pageNumber = DEFAULT_FIRST_PAGE_NUMBER;
        return pageNumber;
    }

    public int getPageSize() {
        if (pageSize == 0) pageSize = DEFAULT_PAGE_SIZE;
        return pageSize;
    }

    public long getOffset() {
        if (getPageNumber() < 0 || getPageSize() < 0) return 0L;
        return (getPageNumber() - 1) * getPageSize();
    }

    public Pageable next() { return getPageable(getPageNumber() + 1); }

    public Pageable previousOrFirst() {
        return getPageable(getPageNumber() > DEFAULT_FIRST_PAGE_NUMBER ? getPageNumber() - 1 : DEFAULT_FIRST_PAGE_NUMBER);
    }

    public Pageable first() { return getPageable(DEFAULT_FIRST_PAGE_NUMBER); }

    public Sort getSort() { return sort; }

    public void setSort(Sort value) { sort = value; }

    public boolean hasPrevious() { return getPageNumber() > DEFAULT_FIRST_PAGE_NUMBER; }

    public void setPageNumber(int value) { pageNumber = value; }

    public void setPageSize(int value) { pageSize = value; }

    public void setFilterJson(String value) { filterJson = value; }

    public String getFilterJson() { return filterJson; }

    public <T> T map(Class<T> type) {
        if (StringUtils.isEmpty(filterJson)) return ClassUtils.newInstance(type);
        return JSONObject.parseObject(filterJson, type);
    }

    public void setSumProperties(String... values) { sumProperties = values; }

    public String[] getSumProperties() { return sumProperties == null ? new String[0] : sumProperties; }

    public String[] getCountProperties() { return countProperties == null ? new String[0] : countProperties; }

    public void setCountProperties(String... values) { countProperties = values; }

    public String getOptionsProperty() { return optionsProperty; }

    public void setOptionsProperty(String value) { optionsProperty = value; }

    private Pageable getPageable(int number) {
        PageRequest request = new PageRequest();
        request.setPageNumber(number);
        request.setPageSize(pageSize);
        request.setFilterJson(filterJson);
        return request;
    }
}
