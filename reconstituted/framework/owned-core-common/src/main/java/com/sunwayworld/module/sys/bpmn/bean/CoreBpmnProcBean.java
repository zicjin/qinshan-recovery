/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.bpmn.bean;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.NotNull;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.support.domain.AbstractInsertable;
import com.sunwayworld.framework.support.domain.Insertable;
import org.springframework.lang.Nullable;

@Table(value="T_CORE_BPMN_PROC")
public class CoreBpmnProcBean
extends AbstractInsertable<Long>
implements Insertable<Long> {
    private static final long serialVersionUID = 8731362359855549210L;
    @Id
    private Long id;
    private String serviceId;
    @NotNull
    private String procCode;
    @Nullable
    private String expression;
    private Long procVersion;
    private Long diagramId;

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getServiceId() {
        return this.serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getProcCode() {
        return this.procCode;
    }

    public void setProcCode(String procCode) {
        this.procCode = procCode;
    }

    public String getExpression() {
        return this.expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public Long getProcVersion() {
        return this.procVersion;
    }

    public void setProcVersion(Long procVersion) {
        this.procVersion = procVersion;
    }

    public Long getDiagramId() {
        return this.diagramId;
    }

    public void setDiagramId(Long diagramId) {
        this.diagramId = diagramId;
    }
}
