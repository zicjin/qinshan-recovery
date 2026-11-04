/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.lims.reporttemplate.bean;

import com.sunwayworld.framework.data.annotation.NotNull;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.tree.bean.AbstractTreeNode;

@Table(value="T_LIMS_REPORT_TEMPLATE_SOURCE")
public class LimsReportTemplateSourceTreeDTO
extends AbstractTreeNode {
    @Transient
    private static final long serialVersionUID = 1097321280613627220L;
    private String sourceCode;
    private String sourceName;
    private String sourceTable;
    @NotNull(defaultValue="detail")
    private String sourceType;
    private String sql;
    private String param;
    private String parentSourceCode;
    private String resolveFunction;

    public String getSourceCode() {
        return this.sourceCode;
    }

    public void setSourceCode(String sourceCode) {
        this.sourceCode = sourceCode;
    }

    public String getSourceName() {
        return this.sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public String getSourceTable() {
        return this.sourceTable;
    }

    public void setSourceTable(String sourceTable) {
        this.sourceTable = sourceTable;
    }

    public String getSourceType() {
        return this.sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public String getSql() {
        return this.sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public String getParam() {
        return this.param;
    }

    public void setParam(String param) {
        this.param = param;
    }

    public String getParentSourceCode() {
        return this.parentSourceCode;
    }

    public void setParentSourceCode(String parentSourceCode) {
        this.parentSourceCode = parentSourceCode;
    }

    public String getResolveFunction() {
        return this.resolveFunction;
    }

    public void setResolveFunction(String resolveFunction) {
        this.resolveFunction = resolveFunction;
    }
}
