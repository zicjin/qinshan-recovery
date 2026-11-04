/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.addomain.bean;

import com.sunwayworld.framework.data.annotation.Id;
import com.sunwayworld.framework.data.annotation.Table;
import com.sunwayworld.framework.data.annotation.Transient;
import com.sunwayworld.framework.support.domain.AbstractInsertable;
import com.sunwayworld.framework.support.domain.Insertable;

@Table(value="T_CORE_AD_DOMAIN_USER")
public class CoreAdDomainUserBean
extends AbstractInsertable<String>
implements Insertable<String> {
    @Transient
    private static final long serialVersionUID = 7577065998659477472L;
    @Id
    private String id;
    private String adUserId;

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    public String getAdUserId() {
        return this.adUserId;
    }

    public void setAdUserId(String adUserId) {
        this.adUserId = adUserId;
    }
}
