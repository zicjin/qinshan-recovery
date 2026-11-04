/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.code.bean;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractInsertable;
import com.sunwayworld.framework.support.domain.Insertable;

@Table(value="T_CORE_CODE_CLASS")
public class CoreCodeClassBean
extends AbstractInsertable<String>
implements Insertable<String> {
    @Transient
    private static final long serialVersionUID = 9076312868903976625L;
    @Id
    private String id;
    private String codeClassName;

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    public String getCodeClassName() {
        return this.codeClassName;
    }

    public void setCodeClassName(String codeClassName) {
        this.codeClassName = codeClassName;
    }
}
