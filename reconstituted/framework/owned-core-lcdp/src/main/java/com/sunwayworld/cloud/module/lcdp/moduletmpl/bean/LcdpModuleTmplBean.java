/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.moduletmpl.bean;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractInsertable;
import com.sunwayworld.framework.support.domain.Persistable;

@Table(value="T_LCDP_MODULE_TMPL")
public class LcdpModuleTmplBean
extends AbstractInsertable<Long>
implements Persistable<Long> {
    @Transient
    private static final long serialVersionUID = -82566196618711043L;
    @Id
    private Long id;
    private String moduleTmplName;
    private String tmplClassId;
    private String tmplType;

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getModuleTmplName() {
        return this.moduleTmplName;
    }

    public void setModuleTmplName(String moduleTmplName) {
        this.moduleTmplName = moduleTmplName;
    }

    public String getTmplClassId() {
        return this.tmplClassId;
    }

    public void setTmplClassId(String tmplClassId) {
        this.tmplClassId = tmplClassId;
    }

    public String getTmplType() {
        return this.tmplType;
    }

    public void setTmplType(String tmplType) {
        this.tmplType = tmplType;
    }
}
