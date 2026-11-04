/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.data.page;

import com.alibaba.fastjson.JSONObject;
import com.sunwayworld.framework.data.page.Pageable;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.utils.ClassUtils;
import com.sunwayworld.framework.utils.NumberUtils;
import com.sunwayworld.framework.utils.StringUtils;
import java.util.Optional;
import org.springframework.data.domain.Sort;

public class PageRequest
implements Pageable {
    private int pageNumber = 1;
    private int pageSize = 50;
    private String filterJson;
    private String[] sumProperties;
    private String[] countProperties;
    private String optionsProperty;
    private Sort sort;

    public static PageRequest of(RestJsonWrapperBean wrapper) {
        PageRequest pageRequest = new PageRequest();
        pageRequest.setPageNumber(NumberUtils.parseInt(wrapper.getParamValue("n")));
        pageRequest.setPageSize(NumberUtils.parseInt(wrapper.getParamValue("s")));
        pageRequest.setFilterJson(wrapper.getParamValue("f"));
        pageRequest.setSumProperties(wrapper.getSumProperties());
        pageRequest.setCountProperties(wrapper.getCountProperties());
        pageRequest.setOptionsProperty(wrapper.getOptionsProperty());
        return pageRequest;
    }

    public static PageRequest of(int pageNumber, int pageSize) {
        PageRequest pageRequest = new PageRequest();
        pageRequest.setPageNumber(pageNumber);
        pageRequest.setPageSize(pageSize);
        return pageRequest;
    }

    @Override
    public int getPageNumber() {
        if (this.pageNumber == 0) {
            this.pageNumber = 1;
        }
        return this.pageNumber;
    }

    @Override
    public int getPageSize() {
        if (this.pageSize == 0) {
            this.pageSize = 50;
        }
        return this.pageSize;
    }

    @Override
    public long getOffset() {
        if (this.getPageNumber() < 0 || this.getPageSize() < 0) {
            return 0L;
        }
        return (this.getPageNumber() - 1) * this.getPageSize();
    }

    @Override
    public Pageable next() {
        return this.getPageable(this.getPageNumber() + 1);
    }

    @Override
    public Pageable previousOrFirst() {
        return this.getPageable(this.getPageNumber() > 1 ? this.getPageNumber() - 1 : 1);
    }

    @Override
    public Pageable first() {
        return this.getPageable(1);
    }

    @Override
    public Sort getSort() {
        return this.sort;
    }

    @Override
    public void setSort(Sort sort) {
        this.sort = sort;
    }

    @Override
    public boolean hasPrevious() {
        return this.getPageNumber() > 1;
    }

    @Override
    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    @Override
    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    @Override
    public void setFilterJson(String filterJson) {
        this.filterJson = filterJson;
    }

    @Override
    public String getFilterJson() {
        return this.filterJson;
    }

    @Override
    public <T> T map(Class<T> searchTermsClazz) {
        if (StringUtils.isEmpty(this.filterJson)) {
            return ClassUtils.newInstance(searchTermsClazz);
        }
        return JSONObject.parseObject(this.filterJson, searchTermsClazz);
    }

    @Override
    public void setSumProperties(String ... sumProperties) {
        this.sumProperties = sumProperties;
    }

    @Override
    public String[] getSumProperties() {
        return Optional.ofNullable(this.sumProperties).orElseGet(() -> new String[0]);
    }

    @Override
    public String[] getCountProperties() {
        return Optional.ofNullable(this.countProperties).orElseGet(() -> new String[0]);
    }

    @Override
    public void setCountProperties(String ... countProperties) {
        this.countProperties = countProperties;
    }

    @Override
    public String getOptionsProperty() {
        return this.optionsProperty;
    }

    @Override
    public void setOptionsProperty(String optionsProperty) {
        this.optionsProperty = optionsProperty;
    }

    private Pageable getPageable(int pageNumber) {
        PageRequest pageable = new PageRequest();
        pageable.setPageNumber(pageNumber);
        pageable.setPageSize(this.pageSize);
        pageable.setFilterJson(this.filterJson);
        return pageable;
    }
}
