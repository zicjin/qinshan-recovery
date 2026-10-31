package com.sunwayworld.framework.mybatis.page;

import java.util.Map;
import java.util.HashMap;
import java.util.Optional;

import org.apache.ibatis.session.RowBounds;

import com.sunwayworld.framework.data.CaseInsensitiveLinkedMap;
import com.sunwayworld.framework.data.page.Pageable;

public class PageRowBounds extends RowBounds {
    private int total;
    private int pageNumber;
    private String[] sumProperties;
    private String[] countProperties;
    private String optionsProperty;
    private Map<String, Number> totalMap;

    public PageRowBounds(Pageable pageable) {
        super((int) pageable.getOffset(), pageable.getPageSize());
        this.pageNumber = pageable.getPageNumber();
        this.sumProperties = pageable.getSumProperties();
        this.countProperties = pageable.getCountProperties();
        this.optionsProperty = pageable.getOptionsProperty();
    }

    public Map<String, Number> getTotalMap() {
        return Optional.ofNullable(totalMap).orElseGet(HashMap<String, Number>::new);
    }
    public int getTotal() { return total; }
    public void setTotal(int value) { total = value; }
    public int getPageNumber() { return pageNumber; }
    public void setPageNumber(int value) { pageNumber = value; }
    public boolean isPageEnabled() { return getLimit() >= 0 && getPageNumber() >= 0; }
    public void setTotalMap(Map<String, Number> values) {
        totalMap = new CaseInsensitiveLinkedMap<Number>(values);
        if (totalMap.containsKey("QTY_")) {
            Number value = totalMap.get("QTY_");
            total = value == null ? 0 : value.intValue();
        }
    }
    public String[] getSumProperties() { return Optional.ofNullable(sumProperties).orElseGet(() -> new String[0]); }
    public void setSumProperties(String... values) { sumProperties = values; }
    public String[] getCountProperties() { return Optional.ofNullable(countProperties).orElseGet(() -> new String[0]); }
    public void setCountProperties(String[] values) { countProperties = values; }
    public String getOptionsProperty() { return optionsProperty; }
    public void setOptionsProperty(String value) { optionsProperty = value; }
}
