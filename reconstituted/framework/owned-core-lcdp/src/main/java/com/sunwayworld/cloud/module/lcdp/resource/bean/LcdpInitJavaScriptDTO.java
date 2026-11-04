/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.resource.bean;

import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractBaseData;

public class LcdpInitJavaScriptDTO
extends AbstractBaseData {
    @Transient
    private static final long serialVersionUID = 1856120058759271906L;
    private String className;
    private String tableName;
    private String mapperNamespace;

    public static LcdpInitJavaScriptDTO of(String className, String tableName, String mapperNamespace) {
        LcdpInitJavaScriptDTO initJavaScriptDTO = new LcdpInitJavaScriptDTO();
        initJavaScriptDTO.setClassName(className);
        initJavaScriptDTO.setTableName(tableName);
        initJavaScriptDTO.setMapperNamespace(mapperNamespace);
        return initJavaScriptDTO;
    }

    public String getClassName() {
        return this.className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getTableName() {
        return this.tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getMapperNamespace() {
        return this.mapperNamespace;
    }

    public void setMapperNamespace(String mapperNamespace) {
        this.mapperNamespace = mapperNamespace;
    }
}
