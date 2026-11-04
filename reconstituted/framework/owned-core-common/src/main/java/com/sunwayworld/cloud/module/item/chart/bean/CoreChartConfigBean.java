/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.chart.bean;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractPersistable;
import com.sunwayworld.framework.support.domain.Persistable;

@Table(value="T_CORE_CHART_CONFIG")
public class CoreChartConfigBean
extends AbstractPersistable<Long>
implements Persistable<Long> {
    @Transient
    private static final long serialVersionUID = -1555060914212619701L;
    @Id
    private Long id;
    private String masterId;
    private String columnTitle;
    private String columnField;
    private String columnType;
    private String columnCategory;
    private String columnParam;
    private String chart;
    private String matchPattern;
    private String color;
    private String orderNo;

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public Long getId() {
        return this.id;
    }

    public String getMasterId() {
        return this.masterId;
    }

    public void setMasterId(String masterId) {
        this.masterId = masterId;
    }

    public String getColumnTitle() {
        return this.columnTitle;
    }

    public void setColumnTitle(String columnTitle) {
        this.columnTitle = columnTitle;
    }

    public String getColumnField() {
        return this.columnField;
    }

    public void setColumnField(String columnField) {
        this.columnField = columnField;
    }

    public String getColumnType() {
        return this.columnType;
    }

    public void setColumnType(String columnType) {
        this.columnType = columnType;
    }

    public String getColumnCategory() {
        return this.columnCategory;
    }

    public void setColumnCategory(String columnCategory) {
        this.columnCategory = columnCategory;
    }

    public String getColumnParam() {
        return this.columnParam;
    }

    public void setColumnParam(String columnParam) {
        this.columnParam = columnParam;
    }

    public String getChart() {
        return this.chart;
    }

    public void setChart(String chart) {
        this.chart = chart;
    }

    public String getMatchPattern() {
        return this.matchPattern;
    }

    public void setMatchPattern(String matchPattern) {
        this.matchPattern = matchPattern;
    }

    public String getColor() {
        return this.color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getOrderNo() {
        return this.orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }
}
