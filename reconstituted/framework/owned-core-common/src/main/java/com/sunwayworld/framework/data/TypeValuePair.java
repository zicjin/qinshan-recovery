/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.data;

import com.sunwayworld.framework.data.Pair;

public class TypeValuePair<T>
extends Pair<Class<T>, T> {
    private static final long serialVersionUID = 3868603824066272267L;

    public TypeValuePair(Class<T> first, T second) {
        super(first, second);
    }
}
