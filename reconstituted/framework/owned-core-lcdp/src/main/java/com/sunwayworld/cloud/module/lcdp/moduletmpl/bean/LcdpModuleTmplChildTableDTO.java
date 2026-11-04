/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.moduletmpl.bean;

import com.sunwayworld.framework.support.domain.AbstractBaseData;

public class LcdpModuleTmplChildTableDTO
extends AbstractBaseData {
    private static final long serialVersionUID = -5654134058479616201L;
    private String tableName;
    private String associatedField;
    private String scriptPath;
    private String functionName;
    private String compName;
    private String childUrlName;

    public String getTableName() {
        return this.tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getAssociatedField() {
        return this.associatedField;
    }

    public void setAssociatedField(String associatedField) {
        this.associatedField = associatedField;
    }

    public String getScriptPath() {
        return this.scriptPath;
    }

    public void setScriptPath(String scriptPath) {
        this.scriptPath = scriptPath;
    }

    public String getFunctionName() {
        return this.functionName;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

    public String getCompName() {
        return this.compName;
    }

    public void setCompName(String compName) {
        this.compName = compName;
    }

    public String getChildUrlName() {
        return this.childUrlName;
    }

    public void setChildUrlName(String childUrlName) {
        this.childUrlName = childUrlName;
    }
}
