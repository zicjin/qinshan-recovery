/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.audit.bean;

import com.sunwayworld.framework.data.annotation.Clob;
import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.NotNull;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.support.domain.AbstractPersistable;
import com.sunwayworld.framework.support.domain.Persistable;

@Table(value="T_CORE_AUDIT_CONFIG")
public class CoreAuditConfigBean
extends AbstractPersistable<String>
implements Persistable<String> {
    private static final long serialVersionUID = -3865321091336427663L;
    @Id
    private String id;
    private String name;
    private String category;
    @Clob
    private String selectDescSql;
    @Clob
    private String selectParentSql;
    @NotNull(defaultValue="0")
    private String auditable;

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return this.category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSelectDescSql() {
        return this.selectDescSql;
    }

    public void setSelectDescSql(String selectDescSql) {
        this.selectDescSql = selectDescSql;
    }

    public String getSelectParentSql() {
        return this.selectParentSql;
    }

    public void setSelectParentSql(String selectParentSql) {
        this.selectParentSql = selectParentSql;
    }

    public String getAuditable() {
        return this.auditable;
    }

    public void setAuditable(String auditable) {
        this.auditable = auditable;
    }
}
