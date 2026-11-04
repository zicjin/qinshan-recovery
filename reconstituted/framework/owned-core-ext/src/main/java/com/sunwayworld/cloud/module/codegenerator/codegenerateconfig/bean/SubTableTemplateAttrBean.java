/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.codegenerator.codegenerateconfig.bean;

import com.sunwayworld.cloud.module.codegenerator.codegenerateconfig.bean.CoreCodeGenerateConfigFieldBean;
import java.util.List;

public class SubTableTemplateAttrBean {
    private String subTableName;
    private String path;
    private String i118n;
    private String subTableDesc;
    private String tableDesc;
    private String foreignField;
    private String javaObjectName;
    private String tableObject;
    private List<CoreCodeGenerateConfigFieldBean> fields;

    public String getSubTableName() {
        return this.subTableName;
    }

    public void setSubTableName(String subTableName) {
        this.subTableName = subTableName;
    }

    public String getPath() {
        return this.path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getSubTableDesc() {
        return this.subTableDesc;
    }

    public void setSubTableDesc(String subTableDesc) {
        this.subTableDesc = subTableDesc;
    }

    public String getTableDesc() {
        return this.tableDesc;
    }

    public void setTableDesc(String tableDesc) {
        this.tableDesc = tableDesc;
    }

    public String getForeignField() {
        return this.foreignField;
    }

    public void setForeignField(String foreignField) {
        this.foreignField = foreignField;
    }

    public List<CoreCodeGenerateConfigFieldBean> getFields() {
        return this.fields;
    }

    public String getI118n() {
        return this.i118n;
    }

    public void setI118n(String i118n) {
        this.i118n = i118n;
    }

    public String getTableObject() {
        return this.tableObject;
    }

    public String getJavaObjectName() {
        return this.javaObjectName;
    }

    public void setJavaObjectName(String javaObjectName) {
        this.javaObjectName = javaObjectName;
    }

    public void setTableObject(String tableObject) {
        this.tableObject = tableObject;
    }

    public void setFields(List<CoreCodeGenerateConfigFieldBean> fields) {
        this.fields = fields;
    }
}
