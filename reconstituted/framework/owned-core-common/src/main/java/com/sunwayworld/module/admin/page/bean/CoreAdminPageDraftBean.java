/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.admin.page.bean;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.support.domain.AbstractPersistable;

@Table(value="T_CORE_ADMIN_PAGE_DRAFT")
public class CoreAdminPageDraftBean
extends AbstractPersistable<Long> {
    private static final long serialVersionUID = -7108976938560601106L;
    @Id
    private Long id;
    private String requestUri;
    private String menuId;

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getRequestUri() {
        return this.requestUri;
    }

    public void setRequestUri(String requestUri) {
        this.requestUri = requestUri;
    }

    public String getMenuId() {
        return this.menuId;
    }

    public void setMenuId(String menuId) {
        this.menuId = menuId;
    }
}
