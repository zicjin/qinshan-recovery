/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.mdm.user.bean;

import java.io.Serializable;
import java.util.List;

public class CoreUserOrgDTO
implements Serializable {
    private static final long serialVersionUID = 3553149741216643556L;
    private String id;
    private List<String> orgList;

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getOrgList() {
        return this.orgList;
    }

    public void setOrgList(List<String> orgList) {
        this.orgList = orgList;
    }
}
