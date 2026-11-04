/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.i18n.bean;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.support.domain.AbstractInsertable;
import com.sunwayworld.framework.support.domain.Insertable;

@Table(value="T_CORE_I18N_MESSAGE")
public class CoreI18nMessageBean
extends AbstractInsertable<Long>
implements Insertable<Long> {
    private static final long serialVersionUID = -1395322137206747139L;
    @Id
    private Long id;
    private Long i18nId;
    private String i18nConfigId;
    private String message;

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public Long getI18nId() {
        return this.i18nId;
    }

    public void setI18nId(Long i18nId) {
        this.i18nId = i18nId;
    }

    public String getI18nConfigId() {
        return this.i18nConfigId;
    }

    public void setI18nConfigId(String i18nConfigId) {
        this.i18nConfigId = i18nConfigId;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
