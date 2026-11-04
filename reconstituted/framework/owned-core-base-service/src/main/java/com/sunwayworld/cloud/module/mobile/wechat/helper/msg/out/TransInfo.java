/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.wechat.helper.msg.out;

import java.io.Serializable;

public class TransInfo
implements Serializable {
    private String KfAccount;

    public TransInfo() {
    }

    public TransInfo(String kfAccount) {
        this.KfAccount = kfAccount;
    }

    public String getKfAccount() {
        return this.KfAccount;
    }

    public void setKfAccount(String kfAccount) {
        this.KfAccount = kfAccount;
    }
}
