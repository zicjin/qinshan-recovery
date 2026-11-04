/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.mybatis.page;

import com.sunwayworld.framework.data.CaseInsensitiveLinkedMap;
import com.sunwayworld.framework.data.page.Pageable;
import com.sunwayworld.framework.utils.CollectionUtils;
import com.sunwayworld.framework.utils.NumberUtils;
import java.util.Map;
import java.util.Optional;
import org.apache.ibatis.session.RowBounds;

public class PageRowBounds
extends RowBounds {
    private int total;
    private int pageNumber;
    private String[] sumProperties;
    private String[] countProperties;
    private String optionsProperty;
    private Map<String, Number> totalMap;

    public PageRowBounds(Pageable pageable) {
        super(NumberUtils.parseInt(pageable.getOffset()), pageable.getPageSize());
        this.pageNumber = pageable.getPageNumber();
        this.sumProperties = pageable.getSumProperties();
        this.countProperties = pageable.getCountProperties();
        this.optionsProperty = pageable.getOptionsProperty();
    }

    public Map<String, Number> getTotalMap() {
        return Optional.ofNullable(this.totalMap).orElseGet(() -> CollectionUtils.emptyMap());
    }

    public int getTotal() {
        return this.total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getPageNumber() {
        return this.pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public boolean isPageEnabled() {
        return this.getLimit() >= 0 && this.getPageNumber() >= 0;
    }

    public void setTotalMap(Map<String, Number> totalMap) {
        this.totalMap = new CaseInsensitiveLinkedMap<Number>(totalMap);
        if (this.totalMap.containsKey("QTY_")) {
            this.total = NumberUtils.parseInt(this.totalMap.get("QTY_"));
        }
    }

    public String[] getSumProperties() {
        return Optional.ofNullable(this.sumProperties).orElseGet(() -> new String[0]);
    }

    public void setSumProperties(String ... sumProperties) {
        this.sumProperties = sumProperties;
    }

    public String[] getCountProperties() {
        return Optional.ofNullable(this.countProperties).orElseGet(() -> new String[0]);
    }

    public void setCountProperties(String[] countProperties) {
        this.countProperties = countProperties;
    }

    public String getOptionsProperty() {
        return this.optionsProperty;
    }

    public void setOptionsProperty(String optionsProperty) {
        this.optionsProperty = optionsProperty;
    }
}
