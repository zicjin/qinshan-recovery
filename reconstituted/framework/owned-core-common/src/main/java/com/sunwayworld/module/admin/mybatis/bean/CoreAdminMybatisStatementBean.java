/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.admin.mybatis.bean;

import com.sunwayworld.framework.data.annotation.Clob;
import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.NotNull;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.support.domain.AbstractPersistable;

@Table(value="T_CORE_ADMIN_MYBATIS_STMT")
public class CoreAdminMybatisStatementBean
extends AbstractPersistable<Long> {
    private static final long serialVersionUID = 8541535378040517275L;
    @Id
    private Long id;
    private String menuId;
    private String mapperId;
    private String statementId;
    @NotNull(defaultValue="1")
    private Long version;
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

    public String getStatementId() {
        return this.statementId;
    }

    public void setStatementId(String statementId) {
        this.statementId = statementId;
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

    public Long getVersion() {
        return this.version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public String getStatementXml() {
        return this.statementXml;
    }

    public void setStatementXml(String statementXml) {
        this.statementXml = statementXml;
    }
}
