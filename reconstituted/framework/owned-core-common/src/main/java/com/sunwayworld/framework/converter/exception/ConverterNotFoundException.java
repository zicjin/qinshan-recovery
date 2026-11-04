/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.converter.exception;

public class ConverterNotFoundException
extends RuntimeException {
    private static final long serialVersionUID = 8978766151677873124L;

    public ConverterNotFoundException(Class<?> sourceType, Class<?> targetType) {
        super("No converter found capable of converting from type [" + sourceType.getName() + "] to type [" + targetType.getName() + "]");
    }
}
