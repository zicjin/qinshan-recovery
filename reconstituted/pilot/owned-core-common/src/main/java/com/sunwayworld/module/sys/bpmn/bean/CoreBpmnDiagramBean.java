/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.bpmn.bean;

import com.sunwayworld.framework.data.annotation.Clob;
import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractInsertable;
import com.sunwayworld.framework.support.domain.Insertable;

@Table(value="T_CORE_BPMN_DIAGRAM")
public class CoreBpmnDiagramBean
extends AbstractInsertable<Long>
implements Insertable<Long> {
    @Transient
    private static final long serialVersionUID = 2271578882561884095L;
    @Id
    private Long id;
    @Clob
    private String diagram;
    @Clob
    private String svg;

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getDiagram() {
        return this.diagram;
    }

    public void setDiagram(String diagram) {
        this.diagram = diagram;
    }

    public String getSvg() {
        return this.svg;
    }

    public void setSvg(String svg) {
        this.svg = svg;
    }
}
