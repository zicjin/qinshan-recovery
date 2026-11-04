/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.admin.config.bean;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.support.domain.AbstractPersistable;

@Table(value="T_CORE_ADMIN_CHECK_CONFIG")
public class CoreAdminCheckConfigBean
extends AbstractPersistable<String> {
    private static final long serialVersionUID = 5857738610507922369L;
    @Id
    private String id;
    private String name;
    private String url;
    private String groupDisplay;
    private String cacheable;
    private String displayOther;
    private String otherRequired;

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getGroupDisplay() {
        return this.groupDisplay;
    }

    public void setGroupDisplay(String groupDisplay) {
        this.groupDisplay = groupDisplay;
    }

    public String getCacheable() {
        return this.cacheable;
    }

    public void setCacheable(String cacheable) {
        this.cacheable = cacheable;
    }

    public String getDisplayOther() {
        return this.displayOther;
    }

    public void setDisplayOther(String displayOther) {
        this.displayOther = displayOther;
    }

    public String getOtherRequired() {
        return this.otherRequired;
    }

    public void setOtherRequired(String otherRequired) {
        this.otherRequired = otherRequired;
    }
}
