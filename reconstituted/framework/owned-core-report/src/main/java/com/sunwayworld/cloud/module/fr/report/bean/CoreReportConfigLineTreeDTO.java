/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.fr.report.bean;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.tree.bean.AbstractTreeNode;

public class CoreReportConfigLineTreeDTO
extends AbstractTreeNode {
    @Transient
    private static final long serialVersionUID = 4483235312726361021L;
    @Id
    private String id;
    private String parentId;
    private String text;
    private String reportName;
    private String reportConfigLineNo;
    private String fileName;
    private String param;
    private String defaultParam;

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public String getParentId() {
        return this.parentId;
    }

    @Override
    public String getText() {
        return this.text;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    @Override
    public void setText(String text) {
        this.text = text;
    }

    public String getReportName() {
        return this.reportName;
    }

    public void setReportName(String reportName) {
        this.reportName = reportName;
    }

    public String getReportConfigLineNo() {
        return this.reportConfigLineNo;
    }

    public void setReportConfigLineNo(String reportConfigLineNo) {
        this.reportConfigLineNo = reportConfigLineNo;
    }

    public String getFileName() {
        return this.fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getParam() {
        return this.param;
    }

    public void setParam(String param) {
        this.param = param;
    }

    public String getDefaultParam() {
        return this.defaultParam;
    }

    public void setDefaultParam(String defaultParam) {
        this.defaultParam = defaultParam;
    }
}
