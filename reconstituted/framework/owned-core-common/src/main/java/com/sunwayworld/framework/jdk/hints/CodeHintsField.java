/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.jdk.hints;

import java.io.Serializable;

public class CodeHintsField
implements Serializable {
    private static final long serialVersionUID = 1053357442331916251L;
    private String type;
    private String name;

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
