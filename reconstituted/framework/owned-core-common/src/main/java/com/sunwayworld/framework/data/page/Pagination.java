/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.data.page;

import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.mybatis.page.PageRowBounds;
import com.sunwayworld.framework.utils.CollectionUtils;
import com.sunwayworld.framework.utils.NumberUtils;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Pagination<T>
implements Page<T> {
    private static final long serialVersionUID = 5715827699736745767L;
    private int total;
    private int pageSize;
    private int pageNumber;
    private List<T> rows;
    private Map<String, Number> totalMap;
    private final Map<String, String> columnTypeMap = new HashMap<String, String>();
    private List<String> optionsList;

    public Pagination() {
        this.total = 0;
        this.pageSize = 0;
        this.pageNumber = 0;
        this.rows = CollectionUtils.emptyList();
        this.totalMap = CollectionUtils.emptyMap();
    }

    public Pagination(List<T> rows) {
        this.total = rows.size();
        this.pageSize = rows.size();
        this.pageNumber = 1;
        this.rows = Objects.requireNonNull(rows);
        this.totalMap = CollectionUtils.emptyMap();
    }

    public Pagination(int total, int pageSize, int pageNumber, List<T> rows) {
        this.total = total;
        this.pageSize = pageSize;
        this.pageNumber = pageNumber;
        this.rows = Objects.requireNonNull(rows);
        this.totalMap = CollectionUtils.emptyMap();
    }

    public Pagination(int total, int pageSize, int pageNumber, List<T> rows, Map<String, Number> totalMap) {
        this.total = total;
        this.pageSize = pageSize;
        this.pageNumber = pageNumber;
        this.rows = Objects.requireNonNull(rows);
        this.totalMap = Objects.requireNonNull(totalMap);
    }

    public Pagination(int pageSize, int pageNumber, org.springframework.data.domain.Page<T> page) {
        this.total = Long.valueOf(page.getTotalElements()).intValue();
        this.pageSize = pageSize;
        this.pageNumber = pageNumber;
        this.rows = page.getContent();
        this.totalMap = CollectionUtils.emptyMap();
    }

    public Pagination(Page<?> otherPagination, List<T> rows) {
        this.total = NumberUtils.max(otherPagination.getTotal(), rows.size());
        this.pageSize = otherPagination.getPageSize();
        this.pageNumber = otherPagination.getPageNumber();
        this.totalMap = otherPagination.getTotalMap();
        this.rows = Objects.requireNonNull(rows);
        this.columnTypeMap.putAll(otherPagination.getColumnTypeMap());
        this.optionsList = otherPagination.getOptionsList();
    }

    public Pagination(PageRowBounds rowBounds, List<T> rows) {
        this(NumberUtils.max(rowBounds.getTotal(), rows.size()), rowBounds.getLimit(), rowBounds.getPageNumber(), rows, rowBounds.getTotalMap());
    }

    @Override
    public int getTotalPages() {
        return Double.valueOf(Math.ceil((double)this.total / (double)this.getPageSize())).intValue();
    }

    @Override
    public int getTotal() {
        return this.total;
    }

    @Override
    public int getNumberOfElements() {
        return this.rows.size();
    }

    @Override
    public boolean isFirst() {
        return this.pageNumber == 1;
    }

    @Override
    public boolean isLast() {
        return !this.hasNext();
    }

    @Override
    public boolean hasNext() {
        return this.pageNumber < this.getTotalPages();
    }

    @Override
    public boolean hasPrevious() {
        return this.pageNumber > 1;
    }

    @Override
    public int getPageNumber() {
        return this.pageNumber;
    }

    @Override
    public List<T> getRows() {
        return this.rows;
    }

    @Override
    public Map<String, Number> getTotalMap() {
        return this.totalMap;
    }

    @Override
    public Map<String, String> getColumnTypeMap() {
        return this.columnTypeMap;
    }

    @Override
    public void setColumnTypeMap(Map<String, String> columnTypeMap) {
        if (columnTypeMap != null) {
            this.columnTypeMap.putAll(columnTypeMap);
        }
    }

    @Override
    public List<String> getOptionsList() {
        return this.optionsList;
    }

    @Override
    public void setOptionsList(List<String> optionsList) {
        this.optionsList = optionsList;
    }

    @Override
    public void setTotal(int total) {
        this.total = total;
    }

    @Override
    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    @Override
    public void setRows(List<T> rows) {
        this.rows = rows;
    }

    @Override
    public void setTotalMap(Map<String, Number> totalMap) {
        this.totalMap = totalMap;
    }

    @Override
    public int getPageSize() {
        return this.pageSize;
    }

    @Override
    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
}
