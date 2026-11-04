/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.metadata.bean;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractPersistable;
import com.sunwayworld.framework.support.domain.Persistable;

@Table(value="T_CORE_TABLE")
public class CoreTableBean
extends AbstractPersistable<String>
implements Persistable<String> {
    @Transient
    private static final long serialVersionUID = -6157635559493863765L;
    @Id
    private String id;
    private String auditable;
    private String attachment;

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    public String getAuditable() {
        return this.auditable;
    }

    public void setAuditable(String auditable) {
        this.auditable = auditable;
    }

    public String getAttachment() {
        return this.attachment;
    }

    public void setAttachment(String attachment) {
        this.attachment = attachment;
    }
}
