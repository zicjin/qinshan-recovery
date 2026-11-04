/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.table.bean;

import com.sunwayworld.framework.mybatis.page.PageRowBounds;
import com.sunwayworld.framework.utils.CollectionUtils;
import com.sunwayworld.framework.utils.NumberUtils;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.springframework.data.domain.Page;

public class LcdpDynamicPagination<T>
implements Serializable {
    private static final long serialVersionUID = -3762826401234226920L;
    private int total;
    private int pageSize;
    private int pageNumber;
    private List<T> rows;
    private Map<String, Number> totalMap;
    private final Map<String, String> columnTypeMap = new LinkedHashMap<String, String>();
    private final Map<String, String> columnDescMap = new LinkedHashMap<String, String>();
    private List<String> optionsList;

    public LcdpDynamicPagination() {
        this.total = 0;
        this.pageSize = 0;
        this.pageNumber = 0;
        this.rows = CollectionUtils.emptyList();
        this.totalMap = CollectionUtils.emptyMap();
    }

    public LcdpDynamicPagination(List<T> rows) {
        this.total = rows.size();
        this.pageSize = rows.size();
        this.pageNumber = 1;
        this.rows = Objects.requireNonNull(rows);
        this.totalMap = CollectionUtils.emptyMap();
    }

    public LcdpDynamicPagination(int total, int pageSize, int pageNumber, List<T> rows) {
        this.total = total;
        this.pageSize = pageSize;
        this.pageNumber = pageNumber;
        this.rows = Objects.requireNonNull(rows);
        this.totalMap = CollectionUtils.emptyMap();
    }

    public LcdpDynamicPagination(int total, int pageSize, int pageNumber, List<T> rows, Map<String, Number> totalMap) {
        this.total = total;
        this.pageSize = pageSize;
        this.pageNumber = pageNumber;
        this.rows = Objects.requireNonNull(rows);
        this.totalMap = Objects.requireNonNull(totalMap);
    }

    public LcdpDynamicPagination(int pageSize, int pageNumber, Page<T> page) {
        this.total = Long.valueOf(page.getTotalElements()).intValue();
        this.pageSize = pageSize;
        this.pageNumber = pageNumber;
        this.rows = page.getContent();
        this.totalMap = CollectionUtils.emptyMap();
    }

    public LcdpDynamicPagination(com.sunwayworld.framework.data.page.Page<?> otherPagination, List<T> rows) {
        this.total = NumberUtils.max(otherPagination.getTotal(), rows.size());
        this.pageSize = otherPagination.getPageSize();
        this.pageNumber = otherPagination.getPageNumber();
        this.totalMap = otherPagination.getTotalMap();
        this.rows = Objects.requireNonNull(rows);
        this.columnTypeMap.putAll(otherPagination.getColumnTypeMap());
        this.optionsList = otherPagination.getOptionsList();
    }

    public LcdpDynamicPagination(PageRowBounds rowBounds, List<T> rows) {
        this(NumberUtils.max(rowBounds.getTotal(), rows.size()), rowBounds.getLimit(), rowBounds.getPageNumber(), rows, rowBounds.getTotalMap());
    }

    public int getTotalPages() {
        return Double.valueOf(Math.ceil((double)this.total / (double)this.getPageSize())).intValue();
    }

    public int getTotal() {
        return this.total;
    }

    public int getNumberOfElements() {
        return this.rows.size();
    }

    public boolean isFirst() {
        return this.pageNumber == 1;
    }

    public boolean isLast() {
        return !this.hasNext();
    }

    public boolean hasNext() {
        return this.pageNumber < this.getTotalPages();
    }

    public boolean hasPrevious() {
        return this.pageNumber > 1;
    }

    public int getPageNumber() {
        return this.pageNumber;
    }

    public List<T> getRows() {
        return this.rows;
    }

    public Map<String, Number> getTotalMap() {
        return this.totalMap;
    }

    public Map<String, String> getColumnTypeMap() {
        return this.columnTypeMap;
    }

    public void setColumnTypeMap(Map<String, String> columnTypeMap) {
        if (columnTypeMap != null) {
            this.columnTypeMap.putAll(columnTypeMap);
        }
    }

    public Map<String, String> getColumnDescMap() {
        return this.columnDescMap;
    }

    public void setColumnDescMap(Map<String, String> columnDescMap) {
        if (columnDescMap != null) {
            this.columnDescMap.putAll(columnDescMap);
        }
    }

    public List<String> getOptionsList() {
        return this.optionsList;
    }

    public void setOptionsList(List<String> optionsList) {
        this.optionsList = optionsList;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public void setRows(List<T> rows) {
        this.rows = rows;
    }

    public void setTotalMap(Map<String, Number> totalMap) {
        this.totalMap = totalMap;
    }

    public int getPageSize() {
        return this.pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
}
