/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.data;

import java.io.Serializable;

public class Pair<S, T>
implements Serializable {
    private static final long serialVersionUID = 3393565154819792077L;
    private S first;
    private T second;

    public static <S, T> Pair<S, T> of(S first, T second) {
        return new Pair<S, T>(first, second);
    }

    protected Pair(S first, T second) {
        this.first = first;
        this.second = second;
    }

    public S getFirst() {
        return this.first;
    }

    public T getSecond() {
        return this.second;
    }

    public void setFirst(S first) {
        this.first = first;
    }

    public void setSecond(T second) {
        this.second = second;
    }
}
