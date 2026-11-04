/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.menu.bean;

import java.io.Serializable;

public class CoreMenuIconDTO
implements Serializable {
    private static final long serialVersionUID = 5238117523988999955L;
    private String iconName;
    private String iconUrl;

    public String getIconName() {
        return this.iconName;
    }

    public void setIconName(String iconName) {
        this.iconName = iconName;
    }

    public String getIconUrl() {
        return this.iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }
}
