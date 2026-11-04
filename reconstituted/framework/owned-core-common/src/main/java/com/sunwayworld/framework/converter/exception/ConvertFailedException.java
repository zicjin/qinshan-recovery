/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.converter.exception;

public class ConvertFailedException
extends RuntimeException {
    private static final long serialVersionUID = -2709756554265671726L;

    public ConvertFailedException(Class<?> sourceType, Class<?> targetType, Throwable cause) {
        super("Converting from type [" + sourceType.getName() + "] to type [" + targetType.getName() + "]", cause);
    }

    public ConvertFailedException(Class<?> sourceType, Class<?> targetType, Object source) {
        super("Converting from type [" + sourceType.getName() + "] value [" + source.toString() + "] to type [" + targetType.getName() + "]");
    }
}
