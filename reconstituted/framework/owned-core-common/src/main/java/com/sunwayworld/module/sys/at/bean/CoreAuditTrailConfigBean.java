/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.at.bean;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.NotNull;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractInsertable;
import com.sunwayworld.framework.support.domain.Insertable;

@Table(value="T_CORE_AT_CONFIG")
public class CoreAuditTrailConfigBean
extends AbstractInsertable<String>
implements Insertable<String> {
    @Transient
    private static final long serialVersionUID = -3865212091332287663L;
    @Id
    private String id;
    @NotNull(defaultValue="1")
    private String auditTrail;

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    public String getAuditTrail() {
        return this.auditTrail;
    }

    public void setAuditTrail(String auditTrail) {
        this.auditTrail = auditTrail;
    }
}
