/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.document.bean;

import com.sunwayworld.framework.data.annotation.Clob;
import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractPersistable;
import com.sunwayworld.framework.support.domain.Persistable;

@Table(value="T_CORE_DOCUMENT")
public class CoreDocumentBean
extends AbstractPersistable<Long>
implements Persistable<Long> {
    @Transient
    private static final long serialVersionUID = -2136885636231538501L;
    @Id
    private Long id;
    private String targetId;
    private String targetType;
    @Clob
    private String content;

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getTargetId() {
        return this.targetId;
    }

    public String getTargetType() {
        return this.targetType;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
