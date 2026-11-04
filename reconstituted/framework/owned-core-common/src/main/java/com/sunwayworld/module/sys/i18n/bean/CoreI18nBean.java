/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.i18n.bean;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.support.domain.AbstractInsertable;
import com.sunwayworld.framework.support.domain.Insertable;

@Table(value="T_CORE_I18N")
public class CoreI18nBean
extends AbstractInsertable<Long>
implements Insertable<Long> {
    private static final long serialVersionUID = 7952796197963832183L;
    @Id
    private Long id;
    private String code;
    private String description;
    private String defaultMessage;

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDefaultMessage() {
        return this.defaultMessage;
    }

    public void setDefaultMessage(String defaultMessage) {
        this.defaultMessage = defaultMessage;
    }
}
