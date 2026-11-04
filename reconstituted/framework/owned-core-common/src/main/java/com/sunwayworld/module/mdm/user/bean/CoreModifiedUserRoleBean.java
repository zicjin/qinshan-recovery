/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.mdm.user.bean;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.NotNull;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.support.domain.AbstractInsertable;
import com.sunwayworld.framework.support.domain.Insertable;

@Table(value="T_CORE_M_USER_ROLE")
public class CoreModifiedUserRoleBean
extends AbstractInsertable<Long>
implements Insertable<Long> {
    private static final long serialVersionUID = 1205706049699937437L;
    @Id
    private Long id;
    private Long modifiedUserId;
    private Long roleId;
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

    public Long getModifiedUserId() {
        return this.modifiedUserId;
    }

    public void setModifiedUserId(Long modifiedUserId) {
        this.modifiedUserId = modifiedUserId;
    }

    public Long getRoleId() {
        return this.roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public String getDefaultFlag() {
        return this.defaultFlag;
    }

    public void setDefaultFlag(String defaultFlag) {
        this.defaultFlag = defaultFlag;
    }
}
