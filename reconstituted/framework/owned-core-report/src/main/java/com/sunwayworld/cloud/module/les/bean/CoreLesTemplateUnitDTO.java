/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.les.bean;

import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.tree.bean.AbstractTreeNode;

public class CoreLesTemplateUnitDTO
extends AbstractTreeNode {
    @Transient
    private static final long serialVersionUID = 4330758545933077524L;
    private Long templateId;
    private String unitCategory;
    private String config;
    private String unitTitle;
    private String unitBody;
    private String editFlag;
    private String exportReport;
    private Long orderNo;

    public Long getTemplateId() {
        return this.templateId;
    }

    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
    }

    public String getUnitCategory() {
        return this.unitCategory;
    }

    public void setUnitCategory(String unitCategory) {
        this.unitCategory = unitCategory;
    }

    public String getConfig() {
        return this.config;
    }

    public void setConfig(String config) {
        this.config = config;
    }

    public String getUnitTitle() {
        return this.unitTitle;
    }

    public void setUnitTitle(String unitTitle) {
        this.unitTitle = unitTitle;
    }

    public String getUnitBody() {
        return this.unitBody;
    }

    public void setUnitBody(String unitBody) {
        this.unitBody = unitBody;
    }

    public String getEditFlag() {
        return this.editFlag;
    }

    public void setEditFlag(String editFlag) {
        this.editFlag = editFlag;
    }

    public String getExportReport() {
        return this.exportReport;
    }

    public void setExportReport(String exportReport) {
        this.exportReport = exportReport;
    }

    public Long getOrderNo() {
        return this.orderNo;
    }

    public void setOrderNo(Long orderNo) {
        this.orderNo = orderNo;
    }
}
