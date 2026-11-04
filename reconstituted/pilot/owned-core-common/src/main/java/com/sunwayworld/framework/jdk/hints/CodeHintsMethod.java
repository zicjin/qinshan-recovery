/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.jdk.hints;

import com.sunwayworld.framework.jdk.hints.CodeHintsMethodParameter;
import java.io.Serializable;

public class CodeHintsMethod
implements Serializable {
    private static final long serialVersionUID = -8035326543280508805L;
    private String name;
    private String fullName;
    private String insertText;
    private String returnType;
    private CodeHintsMethodParameter[] parameters;

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFullName() {
        return this.fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getInsertText() {
        return this.insertText;
    }

    public void setInsertText(String insertText) {
        this.insertText = insertText;
    }

    public String getReturnType() {
        return this.returnType;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    public CodeHintsMethodParameter[] getParameters() {
        return this.parameters;
    }

    public void setParameters(CodeHintsMethodParameter[] parameters) {
        this.parameters = parameters;
    }
}
