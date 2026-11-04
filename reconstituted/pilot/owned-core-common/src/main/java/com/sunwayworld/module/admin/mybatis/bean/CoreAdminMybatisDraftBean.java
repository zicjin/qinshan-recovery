/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.admin.mybatis.bean;

import com.sunwayworld.framework.data.annotation.Clob;
import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.support.domain.AbstractPersistable;

@Table(value="T_CORE_ADMIN_MYBATIS_DRAFT")
public class CoreAdminMybatisDraftBean
extends AbstractPersistable<Long> {
    private static final long serialVersionUID = -3685237595106202516L;
    @Id
    private Long id;
    private String menuId;
    private String mapperId;
    private String statementId;
    @Clob
    private String statementXml;

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getMenuId() {
        return this.menuId;
    }

    public void setMenuId(String menuId) {
        this.menuId = menuId;
    }

    public String getMapperId() {
        return this.mapperId;
    }

    public void setMapperId(String mapperId) {
        this.mapperId = mapperId;
    }

    public String getStatementId() {
        return this.statementId;
    }

    public void setStatementId(String statementId) {
        this.statementId = statementId;
    }

    public String getStatementXml() {
        return this.statementXml;
    }

    public void setStatementXml(String statementXml) {
        this.statementXml = statementXml;
    }
}
