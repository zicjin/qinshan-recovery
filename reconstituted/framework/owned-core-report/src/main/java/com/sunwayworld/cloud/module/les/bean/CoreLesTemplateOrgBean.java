/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.les.bean;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractInsertable;
import com.sunwayworld.framework.support.domain.Insertable;

@Table(value="T_CORE_LES_TEMPLATE_ORG")
public class CoreLesTemplateOrgBean
extends AbstractInsertable<Long>
implements Insertable<Long> {
    @Transient
    private static final long serialVersionUID = -2286367288943442517L;
    @Id
    private Long id;
    private Long templateId;
    private String usedOrgId;

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

    public String getUsedOrgId() {
        return this.usedOrgId;
    }

    public void setUsedOrgId(String usedOrgId) {
        this.usedOrgId = usedOrgId;
    }
}
