/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.jdk.tools;

import java.io.Serializable;

public class DiagnosticMessage
implements Serializable {
    private static final long serialVersionUID = 5566011475024139221L;
    private String kind;
    private Long lineNumber;
    private Long columnNumber;
    private String message;

    public String getKind() {
        return this.kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public Long getLineNumber() {
        return this.lineNumber;
    }

    public void setLineNumber(Long lineNumber) {
        this.lineNumber = lineNumber;
    }

    public Long getColumnNumber() {
        return this.columnNumber;
    }

    public void setColumnNumber(Long columnNumber) {
        this.columnNumber = columnNumber;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
