/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.customds.bean;

import com.sunwayworld.cloud.module.item.customds.bean.CoreCustomDsSqlFieldBean;
import java.io.Serializable;
import java.util.List;

public class CoreCustomDsSqlDTO
implements Serializable {
    private static final long serialVersionUID = 8883748186726968974L;
    private Long id;
    private String sqlName;
    private String sqlContent;
    private List<CoreCustomDsSqlFieldBean> columnList;

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSqlName() {
        return this.sqlName;
    }

    public void setSqlName(String sqlName) {
        this.sqlName = sqlName;
    }

    public String getSqlContent() {
        return this.sqlContent;
    }

    public void setSqlContent(String sqlContent) {
        this.sqlContent = sqlContent;
    }

    public List<CoreCustomDsSqlFieldBean> getColumnList() {
        return this.columnList;
    }

    public void setColumnList(List<CoreCustomDsSqlFieldBean> columnList) {
        this.columnList = columnList;
    }
}
