/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.i18n.bean;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.support.domain.AbstractPersistable;
import com.sunwayworld.framework.support.domain.Persistable;

@Table(value="T_CORE_I18N_CONFIG")
public class CoreI18nConfigBean
extends AbstractPersistable<String>
implements Persistable<String> {
    private static final long serialVersionUID = 8407018276241457679L;
    @Id
    private String id;
    private String localeName;

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    public String getLocaleName() {
        return this.localeName;
    }

    public void setLocaleName(String localeName) {
        this.localeName = localeName;
    }
}
