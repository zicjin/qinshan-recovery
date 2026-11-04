/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.jdk.hints;

import com.sunwayworld.framework.jdk.hints.CodeHintsField;
import com.sunwayworld.framework.jdk.hints.CodeHintsMethod;
import java.io.Serializable;

public class CodeHintsClass
implements Serializable {
    private static final long serialVersionUID = -5615008459313802675L;
    private String name;
    private String simpleName;
    private CodeHintsField[] fields;
    private CodeHintsMethod[] methods;

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSimpleName() {
        return this.simpleName;
    }

    public void setSimpleName(String simpleName) {
        this.simpleName = simpleName;
    }

    public CodeHintsField[] getFields() {
        return this.fields;
    }

    public void setFields(CodeHintsField[] fields) {
        this.fields = fields;
    }

    public CodeHintsMethod[] getMethods() {
        return this.methods;
    }

    public void setMethods(CodeHintsMethod[] methods) {
        this.methods = methods;
    }
}
