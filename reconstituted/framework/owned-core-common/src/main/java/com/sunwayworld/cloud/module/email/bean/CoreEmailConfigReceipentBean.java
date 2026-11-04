/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.email.bean;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractInsertable;
import com.sunwayworld.framework.support.domain.Insertable;

@Table(value="T_CORE_EMAIL_CONFIG_RECEIPENT")
public class CoreEmailConfigReceipentBean
extends AbstractInsertable<Long>
implements Insertable<Long> {
    @Transient
    private static final long serialVersionUID = -1590463202298892197L;
    @Id
    private Long id;
    private String configId;
    private String receipentId;

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getConfigId() {
        return this.configId;
    }

    public void setConfigId(String configId) {
        this.configId = configId;
    }

    public String getReceipentId() {
        return this.receipentId;
    }

    public void setReceipentId(String receipentId) {
        this.receipentId = receipentId;
    }
}
