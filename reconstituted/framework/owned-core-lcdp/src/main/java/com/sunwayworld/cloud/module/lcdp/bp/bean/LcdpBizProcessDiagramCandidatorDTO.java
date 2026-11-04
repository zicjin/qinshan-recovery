/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.bp.bean;

import com.sunwayworld.framework.data.annotation.Transient;
import java.io.Serializable;

public class LcdpBizProcessDiagramCandidatorDTO
implements Serializable {
    @Transient
    private static final long serialVersionUID = 2414592362058661174L;
    private String bpNodeName;
    private String role;
    private String user;

    public String getBpNodeName() {
        return this.bpNodeName;
    }

    public void setBpNodeName(String bpNodeName) {
        this.bpNodeName = bpNodeName;
    }

    public String getRole() {
        return this.role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getUser() {
        return this.user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
