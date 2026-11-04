/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.monitor.monitor.bean;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class CoreMonitorUserDTO
implements Serializable {
    private static final long serialVersionUID = -8277903331606469237L;
    private Integer onlineUserQty;
    private Set<String> concurrentUserSet = new HashSet<String>();

    public Integer getOnlineUserQty() {
        return this.onlineUserQty;
    }

    public void setOnlineUserQty(Integer onlineUserQty) {
        this.onlineUserQty = onlineUserQty;
    }

    public Set<String> getConcurrentUserSet() {
        return this.concurrentUserSet;
    }

    public void setConcurrentUserSet(Set<String> concurrentUserSet) {
        this.concurrentUserSet = concurrentUserSet;
    }
}
