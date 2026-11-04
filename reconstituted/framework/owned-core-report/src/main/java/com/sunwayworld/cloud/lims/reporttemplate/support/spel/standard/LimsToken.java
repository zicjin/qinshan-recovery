/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.lims.reporttemplate.support.spel.standard;

import com.sunwayworld.cloud.lims.reporttemplate.support.spel.standard.LimsTokenKind;
import org.springframework.lang.Nullable;

public class LimsToken {
    LimsTokenKind kind;
    @Nullable
    String data;
    int startPos;
    int endPos;

    LimsToken(LimsTokenKind limsTokenKind, int startPos, int endPos) {
        this.kind = limsTokenKind;
        this.startPos = startPos;
        this.endPos = endPos;
    }

    LimsToken(LimsTokenKind limsTokenKind, char[] tokenData, int startPos, int endPos) {
        this(limsTokenKind, startPos, endPos);
        this.data = new String(tokenData);
    }

    public LimsTokenKind getKind() {
        return this.kind;
    }

    public boolean isIdentifier() {
        return this.kind == LimsTokenKind.IDENTIFIER;
    }

    public boolean isNumericRelationalOperator() {
        return this.kind == LimsTokenKind.GT || this.kind == LimsTokenKind.GE || this.kind == LimsTokenKind.LT || this.kind == LimsTokenKind.LE || this.kind == LimsTokenKind.EQ || this.kind == LimsTokenKind.NE;
    }

    public String stringValue() {
        return this.data != null ? this.data : "";
    }

    public LimsToken asInstanceOfToken() {
        return new LimsToken(LimsTokenKind.INSTANCEOF, this.startPos, this.endPos);
    }

    public LimsToken asMatchesToken() {
        return new LimsToken(LimsTokenKind.MATCHES, this.startPos, this.endPos);
    }

    public LimsToken asBetweenToken() {
        return new LimsToken(LimsTokenKind.BETWEEN, this.startPos, this.endPos);
    }

    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("[").append(this.kind.toString());
        if (this.kind.hasPayload()) {
            s.append(":").append(this.data);
        }
        s.append("]");
        s.append("(").append(this.startPos).append(",").append(this.endPos).append(")");
        return s.toString();
    }
}
