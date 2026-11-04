/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.chart.bean;

import com.sunwayworld.cloud.module.item.chart.bean.CoreChartConfigBean;
import com.sunwayworld.cloud.module.item.chart.bean.CoreChartSearchConfigBean;
import com.sunwayworld.framework.data.annotation.Transient;
import java.io.Serializable;
import java.util.List;

public class CoreChartLinesRepresentation
implements Serializable {
    @Transient
    private static final long serialVersionUID = -5506790992028041901L;
    public List<CoreChartConfigBean> configList;
    public List<CoreChartSearchConfigBean> searchConfigList;

    public List<CoreChartConfigBean> getConfigList() {
        return this.configList;
    }

    public void setConfigList(List<CoreChartConfigBean> configList) {
        this.configList = configList;
    }

    public List<CoreChartSearchConfigBean> getSearchConfigList() {
        return this.searchConfigList;
    }

    public void setSearchConfigList(List<CoreChartSearchConfigBean> searchConfigList) {
        this.searchConfigList = searchConfigList;
    }
}
