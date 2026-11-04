/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.mybatis.bean;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractPersistable;
import com.sunwayworld.framework.support.domain.Persistable;

@Table(value="T_CORE_MYBATIS_STMT_PROPERTY")
public class CoreMybatisStatementPropertyBean
extends AbstractPersistable<Long>
implements Persistable<Long> {
    @Transient
    private static final long serialVersionUID = -5848659608537524575L;
    @Id
    private Long id;
    private Long mybatisStatementId;
    private String property;
    private String name;
    private String sum;

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getMybatisStatementId() {
        return this.mybatisStatementId;
    }

    public void setMybatisStatementId(Long mybatisStatementId) {
        this.mybatisStatementId = mybatisStatementId;
    }

    public String getProperty() {
        return this.property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public String getSum() {
        return this.sum;
    }

    public void setSum(String sum) {
        this.sum = sum;
    }
}
