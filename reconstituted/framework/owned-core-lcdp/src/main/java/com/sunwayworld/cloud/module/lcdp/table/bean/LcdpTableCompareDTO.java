/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.table.bean;

import com.sunwayworld.framework.support.domain.AbstractBaseData;
import java.util.List;

public class LcdpTableCompareDTO<T>
extends AbstractBaseData {
    private static final long serialVersionUID = -7126448101069059815L;
    private T currentTable;
    private T previousTable;
    private List<T> historyTableList;

    public T getCurrentTable() {
        return this.currentTable;
    }

    public void setCurrentTable(T currentTable) {
        this.currentTable = currentTable;
    }

    public T getPreviousTable() {
        return this.previousTable;
    }

    public void setPreviousTable(T previousTable) {
        this.previousTable = previousTable;
    }

    public List<T> getHistoryTableList() {
        return this.historyTableList;
    }

    public void setHistoryTableList(List<T> historyTableList) {
        this.historyTableList = historyTableList;
    }
}
