/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.bpmn.bean;

import com.sunwayworld.framework.support.domain.AbstractBaseData;
import com.sunwayworld.framework.support.domain.BaseData;
import java.util.List;
import org.springframework.lang.Nullable;

public class CoreBpmnImportDTO
extends AbstractBaseData
implements BaseData {
    private static final long serialVersionUID = -108893518391565218L;
    private String serviceId;
    private String procCode;
    @Nullable
    private String expression;
    private String diagram;
    private String svg;
    private List<String> usedOrgIdList;

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

    public List<String> getUsedOrgIdList() {
        return this.usedOrgIdList;
    }

    public void setUsedOrgIdList(List<String> usedOrgIdList) {
        this.usedOrgIdList = usedOrgIdList;
    }
}
