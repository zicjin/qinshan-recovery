/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.les.bean;

import com.sunwayworld.framework.data.annotation.Clob;
import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.NotNull;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractInsertable;
import com.sunwayworld.framework.support.domain.Insertable;
import com.sunwayworld.framework.support.domain.Orderable;

@Table(value="T_CORE_LES_TEMPLATE_UNIT")
public class CoreLesTemplateUnitBean
extends AbstractInsertable<Long>
implements Insertable<Long>,
Orderable<Long> {
    @Transient
    private static final long serialVersionUID = -7012837275122331747L;
    @Id
    private Long id;
    private Long templateId;
    private String unitCategory;
    private String unitTitle;
    private Long parentId;
    @Clob
    private String unitBody;
    @Clob
    private String config;
    private String editFlag;
    @NotNull(defaultValue="1")
    private String exportReport;
    private Long orderNo;

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

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

    public String getUnitTitle() {
        return this.unitTitle;
    }

    public void setUnitTitle(String unitTitle) {
        this.unitTitle = unitTitle;
    }

    public Long getParentId() {
        return this.parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getUnitBody() {
        return this.unitBody;
    }

    public void setUnitBody(String unitBody) {
        this.unitBody = unitBody;
    }

    public String getConfig() {
        return this.config;
    }

    public void setConfig(String config) {
        this.config = config;
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

    @Override
    public Long getOrderNo() {
        return this.orderNo;
    }

    @Override
    public void setOrderNo(Long orderNo) {
        this.orderNo = orderNo;
    }
}
