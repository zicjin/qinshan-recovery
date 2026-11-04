/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.attorney.bean;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.support.domain.AbstractBaseData;
import com.sunwayworld.framework.support.domain.Persistable;

@Table(value="T_CORE_ATTORNEY_MENU")
public class CoreAttorneyMenuBean
extends AbstractBaseData
implements Persistable<Long> {
    private static final long serialVersionUID = 6624699978151270941L;
    @Id
    private Long id;
    private Long attorneyId;
    private String menuId;

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public Long getAttorneyId() {
        return this.attorneyId;
    }

    public void setAttorneyId(Long attorneyId) {
        this.attorneyId = attorneyId;
    }

    public String getMenuId() {
        return this.menuId;
    }

    public void setMenuId(String menuId) {
        this.menuId = menuId;
    }
}
