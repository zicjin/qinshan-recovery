/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.item.diagram.bean;

import com.sunwayworld.framework.data.annotation.Clob;
import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractInsertable;
import com.sunwayworld.framework.support.domain.Insertable;

@Table(value="T_CORE_DIAGRAM")
public class CoreDiagramBean
extends AbstractInsertable<Long>
implements Insertable<Long> {
    @Transient
    private static final long serialVersionUID = 7233904966950025641L;
    @Id
    private Long id;
    private String targetId;
    @Clob
    private String diagram;

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

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public String getDiagram() {
        return this.diagram;
    }

    public void setDiagram(String diagram) {
        this.diagram = diagram;
    }
}
