/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.formula;

import java.util.ArrayList;
import java.util.List;

public enum GikamFormulaRegistry {
    INSTANCE;

    List<Class<?>> REPOSITORY = new ArrayList();

    public GikamFormulaRegistry register(Class<?> clazz) {
        this.REPOSITORY.add(clazz);
        return this;
    }

    public List<Class<?>> getRepository() {
        return this.REPOSITORY;
    }
}
