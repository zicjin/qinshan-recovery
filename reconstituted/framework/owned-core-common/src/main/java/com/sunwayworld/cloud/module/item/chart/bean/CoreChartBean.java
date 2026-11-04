/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.chart.bean;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.NotNull;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractAuditable;

@Table(value="T_CORE_CHART")
public class CoreChartBean
extends AbstractAuditable<String> {
    @Transient
    private static final long serialVersionUID = -8204766155595623294L;
    @Id
    private String id;
    private String chartName;
    private String chartType;
    private String searchSql;
    private String searchRule;
    @NotNull(defaultValue="0")
    private String chartStatus;
    private String remark;

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    public String getChartName() {
        return this.chartName;
    }

    public void setChartName(String chartName) {
        this.chartName = chartName;
    }

    public String getChartType() {
        return this.chartType;
    }

    public void setChartType(String chartType) {
        this.chartType = chartType;
    }

    public String getSearchSql() {
        return this.searchSql;
    }

    public void setSearchSql(String searchSql) {
        this.searchSql = searchSql;
    }

    public String getSearchRule() {
        return this.searchRule;
    }

    public void setSearchRule(String searchRule) {
        this.searchRule = searchRule;
    }

    public String getChartStatus() {
        return this.chartStatus;
    }

    public void setChartStatus(String chartStatus) {
        this.chartStatus = chartStatus;
    }

    public String getRemark() {
        return this.remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
