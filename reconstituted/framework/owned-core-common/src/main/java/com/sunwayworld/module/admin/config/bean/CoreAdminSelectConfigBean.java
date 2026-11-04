/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.admin.config.bean;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.support.domain.AbstractPersistable;

@Table(value="T_CORE_ADMIN_SELECT_CONFIG")
public class CoreAdminSelectConfigBean
extends AbstractPersistable<Long> {
    private static final long serialVersionUID = 5857738610507922369L;
    @Id
    private Long id;
    private String name;
    private String category;
    private String url;
    private String valueField;
    private String textField;
    private String cacheable;

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getCategory() {
        return this.category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getValueField() {
        return this.valueField;
    }

    public void setValueField(String valueField) {
        this.valueField = valueField;
    }

    public String getTextField() {
        return this.textField;
    }

    public void setTextField(String textField) {
        this.textField = textField;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCacheable() {
        return this.cacheable;
    }

    public void setCacheable(String cacheable) {
        this.cacheable = cacheable;
    }
}
