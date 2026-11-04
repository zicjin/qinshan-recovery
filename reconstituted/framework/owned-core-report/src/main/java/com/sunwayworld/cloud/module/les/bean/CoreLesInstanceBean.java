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
import com.sunwayworld.framework.support.domain.Auditable;
import com.sunwayworld.framework.support.domain.Insertable;

@Table(value="T_CORE_LES_INSTANCE")
public class CoreLesInstanceBean
extends AbstractInsertable<Long>
implements Insertable<Long>,
Auditable<Long> {
    @Transient
    private static final long serialVersionUID = 335633934782412687L;
    @Id
    private Long id;
    private Long templateId;
    private String instanceName;
    private String lesCategory;
    private String bizCategory;
    private String readonlyFlag;
    @Clob
    private String html;
    @NotNull(defaultValue="draft")
    private String processStatus;

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

    public String getInstanceName() {
        return this.instanceName;
    }

    public void setInstanceName(String instanceName) {
        this.instanceName = instanceName;
    }

    @Override
    public String getProcessStatus() {
        return this.processStatus;
    }

    @Override
    public void setProcessStatus(String processStatus) {
        this.processStatus = processStatus;
    }

    public String getLesCategory() {
        return this.lesCategory;
    }

    public void setLesCategory(String lesCategory) {
        this.lesCategory = lesCategory;
    }

    public String getBizCategory() {
        return this.bizCategory;
    }

    public void setBizCategory(String bizCategory) {
        this.bizCategory = bizCategory;
    }

    public String getReadonlyFlag() {
        return this.readonlyFlag;
    }

    public void setReadonlyFlag(String readonlyFlag) {
        this.readonlyFlag = readonlyFlag;
    }

    public String getHtml() {
        return this.html;
    }

    public void setHtml(String html) {
        this.html = html;
    }
}
