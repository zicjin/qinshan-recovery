/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.menu.bean;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.support.domain.AbstractPersistable;
import com.sunwayworld.framework.support.domain.Persistable;

@Table(value="T_CORE_MENU_ACCESS_LOG")
public class CoreMenuAccessLogBean
extends AbstractPersistable<Long>
implements Persistable<Long> {
    private static final long serialVersionUID = 5740978826061450673L;
    @Id
    private Long id;
    private String menuId;
    private String userId;
    private Long accessTimes;

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

    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Long getAccessTimes() {
        return this.accessTimes;
    }

    public void setAccessTimes(Long accessTimes) {
        this.accessTimes = accessTimes;
    }
}
