/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.support.table;

import com.sunwayworld.framework.utils.ArrayUtils;
import com.sunwayworld.framework.utils.ConvertUtils;
import com.sunwayworld.framework.utils.ReflectionUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class TableCellDescriptor<T> {
    private Function<T, String> parseCellKeyFunc;
    private Function<T, String> parseCellValueFunc;
    private Function<T, String> parseIdFunc;
    private String cellFieldName;
    private List<String> pivotList = new ArrayList<String>();
    private List<String> extList = new ArrayList<String>();

    public TableCellDescriptor(String key, String value) {
        this.parseCellKeyFunc = t -> ConvertUtils.convert(ReflectionUtils.invokeReadMethod(t, key), String.class);
        this.parseCellValueFunc = t -> ConvertUtils.convert(ReflectionUtils.invokeReadMethod(t, value), String.class);
        this.parseIdFunc = t -> ConvertUtils.convert(ReflectionUtils.invokeReadMethod(t, "id"), String.class);
        this.cellFieldName = value;
    }

    public Function<T, String> getParseCellKeyFunc() {
        return this.parseCellKeyFunc;
    }

    public void setParseCellKeyFunc(Function<T, String> parseCellKeyFunc) {
        this.parseCellKeyFunc = parseCellKeyFunc;
    }

    public Function<T, String> getParseCellValueFunc() {
        return this.parseCellValueFunc;
    }

    public void setParseCellValueFunc(Function<T, String> parseCellValueFunc) {
        this.parseCellValueFunc = parseCellValueFunc;
    }

    public List<String> getPivotList() {
        return this.pivotList;
    }

    public void addPivot(String ... pivots) {
        this.pivotList.addAll(ArrayUtils.asList(pivots));
    }

    public List<String> getExtList() {
        return this.extList;
    }

    public void addExt(String ... exts) {
        this.extList.addAll(ArrayUtils.asList(exts));
    }

    public String getCellFieldName() {
        return this.cellFieldName;
    }

    public void setCellFieldName(String cellFieldName) {
        this.cellFieldName = cellFieldName;
    }

    public Function<T, String> getParseIdFunc() {
        return this.parseIdFunc;
    }
}
