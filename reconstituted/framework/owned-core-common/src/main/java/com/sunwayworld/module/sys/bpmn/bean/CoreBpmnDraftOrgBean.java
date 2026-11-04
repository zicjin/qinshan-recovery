/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.bpmn.bean;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.support.domain.AbstractInsertable;
import com.sunwayworld.framework.support.domain.Insertable;

@Table(value="T_CORE_BPMN_DRAFT_ORG")
public class CoreBpmnDraftOrgBean
extends AbstractInsertable<Long>
implements Insertable<Long> {
    private static final long serialVersionUID = 2271578882561884095L;
    @Id
    private Long id;
    private Long draftId;
    private String usedOrgId;

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public Long getDraftId() {
        return this.draftId;
    }

    public void setDraftId(Long draftId) {
        this.draftId = draftId;
    }

    public String getUsedOrgId() {
        return this.usedOrgId;
    }

    public void setUsedOrgId(String usedOrgId) {
        this.usedOrgId = usedOrgId;
    }
}
