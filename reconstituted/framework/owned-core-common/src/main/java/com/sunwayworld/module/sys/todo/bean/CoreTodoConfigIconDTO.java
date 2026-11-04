/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.todo.bean;

import java.io.Serializable;

public class CoreTodoConfigIconDTO
implements Serializable {
    private static final long serialVersionUID = -2273976749152017372L;
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
