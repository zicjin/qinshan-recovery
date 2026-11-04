/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.role.bean;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.NotNull;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractInsertable;
import com.sunwayworld.framework.support.domain.Insertable;

@Table(value="T_CORE_ROLE_USER")
public class CoreRoleUserBean
extends AbstractInsertable<Long>
implements Insertable<Long> {
    @Transient
    private static final long serialVersionUID = 918783712976163930L;
    @Id
    private Long id;
    private Long roleId;
    private String userId;
    @NotNull(defaultValue="0")
    private String defaultFlag;

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public Long getRoleId() {
        return this.roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDefaultFlag() {
        return this.defaultFlag;
    }

    public void setDefaultFlag(String defaultFlag) {
        this.defaultFlag = defaultFlag;
    }
}
