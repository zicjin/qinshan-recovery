package com.sunwayworld.framework.data.page;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class Pagination<T> implements Page<T> {
    private List<T> rows = new ArrayList<T>(); private int total; private int pageNumber; private int pageSize;
    private Map<String, String> columnTypeMap = new HashMap<String, String>(); private List<String> optionsList; private Map<String, Number> totalMap = new HashMap<String, Number>();
    public Pagination() { }
    public Pagination(List<T> rows) { this.rows = rows; this.total = rows.size(); this.pageNumber = 1; this.pageSize = rows.size(); }
    /** Supplier parameter order is (total, pageSize, pageNumber, rows). */
    public Pagination(int total, int pageSize, int pageNumber, List<T> rows) { if (rows == null) throw new NullPointerException(); this.total = total; this.pageSize = pageSize; this.pageNumber = pageNumber; this.rows = rows; }
    public Pagination(int total, int pageSize, int pageNumber, List<T> rows, Map<String, Number> totalMap) {
        this(total, pageSize, pageNumber, rows);
        if (totalMap == null) throw new NullPointerException();
        this.totalMap = totalMap;
    }
    public Pagination(int pageSize, int pageNumber, org.springframework.data.domain.Page<T> source) {
        this.total = (int) source.getTotalElements();
        this.pageSize = pageSize;
        this.pageNumber = pageNumber;
        this.rows = source.getContent();
    }
    public Pagination(com.sunwayworld.framework.mybatis.page.PageRowBounds bounds, List<T> rows) {
        this(Math.max(bounds.getTotal(), rows.size()), bounds.getLimit(), bounds.getPageNumber(), rows, bounds.getTotalMap());
    }
    public Pagination(Page<?> source, List<T> rows) {
        if (source == null) throw new NullPointerException();
        this.total = Math.max(source.getTotal(), rows.size()); this.pageSize = source.getPageSize(); this.pageNumber = source.getPageNumber();
        this.rows = rows;
        this.totalMap = source.getTotalMap(); this.columnTypeMap = new HashMap<String, String>(source.getColumnTypeMap()); this.optionsList = source.getOptionsList();
    }
    public int getTotalPages() {
        return (int)Math.ceil((double)total / pageSize);
    }
    public int getNumberOfElements() { return rows.size(); }
    public boolean isFirst() { return pageNumber == 1; }
    public boolean isLast() { return !hasNext(); }
    public boolean hasNext() { return pageNumber < getTotalPages(); }
    public boolean hasPrevious() { return pageNumber > 1; }
    public void setTotal(int total) { this.total = total; }
    public int getTotal() { return total; }
    public void setPageSize(int pageSize) { this.pageSize = pageSize; }
    public int getPageSize() { return pageSize; }
    public void setPageNumber(int pageNumber) { this.pageNumber = pageNumber; }
    public int getPageNumber() { return pageNumber; }
    public void setRows(List<T> rows) { this.rows = rows; }
    public List<T> getRows() { return rows; }
    public void setColumnTypeMap(Map<String, String> values) { if (values != null) columnTypeMap.putAll(values); }
    public Map<String, String> getColumnTypeMap() { return columnTypeMap; }
    public void setOptionsList(List<String> values) { this.optionsList = values; }
    public List<String> getOptionsList() { return optionsList; }
    public void setTotalMap(Map<String, Number> values) { this.totalMap = values; }
    public Map<String, Number> getTotalMap() { return totalMap; }
}
