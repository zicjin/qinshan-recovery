/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.zwdd.utils.domain;

import java.io.Serializable;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class UserDTO
implements Serializable {
    private static final long serialVersionUID = -1262029894532511744L;
    private String userid;
    private String name;
    private String avatar;

    public String getUserid() {
        return this.userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatar() {
        return this.avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
