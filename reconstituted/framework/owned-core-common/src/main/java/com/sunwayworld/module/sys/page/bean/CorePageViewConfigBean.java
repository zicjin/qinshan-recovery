/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.page.bean;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.support.domain.AbstractPersistable;
import com.sunwayworld.framework.support.domain.Persistable;

@Table(value="T_CORE_PAGE_VIEW_CONFIG")
public class CorePageViewConfigBean
extends AbstractPersistable<String>
implements Persistable<String> {
    private static final long serialVersionUID = -3368961083142579323L;
    @Id
    private String id;
    private String config;

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    public String getConfig() {
        return this.config;
    }

    public void setConfig(String config) {
        this.config = config;
    }
}
