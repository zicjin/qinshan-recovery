/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.jdk.hints;

import java.io.Serializable;

public class CodeHintsMethodParameter
implements Serializable {
    private static final long serialVersionUID = -9029521425696149847L;
    private String name;
    private String type;

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
