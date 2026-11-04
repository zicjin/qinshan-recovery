/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.password.bean;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.support.domain.AbstractInsertable;

@Table(value="T_CORE_PASSWORD_HIS")
public class CorePasswordHisBean
extends AbstractInsertable<Long> {
    private static final long serialVersionUID = -7810141274107499125L;
    @Id
    private Long id;
    private String userId;
    private String password;

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
