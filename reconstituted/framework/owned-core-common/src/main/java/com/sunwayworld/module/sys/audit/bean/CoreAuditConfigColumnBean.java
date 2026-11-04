/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.audit.bean;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.NotNull;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.support.domain.AbstractPersistable;
import com.sunwayworld.framework.support.domain.Persistable;
import org.springframework.lang.Nullable;

@Table(value="T_CORE_AUDIT_CONFIG_COLUMN")
public class CoreAuditConfigColumnBean
extends AbstractPersistable<Long>
implements Persistable<Long> {
    private static final long serialVersionUID = 6385880521388619419L;
    @Id
    private Long id;
    private String configId;
    private String column;
    @Nullable
    private String selectValueSql;
    @NotNull(defaultValue="0")
    private String auditable;

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getConfigId() {
        return this.configId;
    }

    public void setConfigId(String configId) {
        this.configId = configId;
    }

    public String getColumn() {
        return this.column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public String getAuditable() {
        return this.auditable;
    }

    public void setAuditable(String auditable) {
        this.auditable = auditable;
    }

    public String getSelectValueSql() {
        return this.selectValueSql;
    }

    public void setSelectValueSql(String selectValueSql) {
        this.selectValueSql = selectValueSql;
    }
}
