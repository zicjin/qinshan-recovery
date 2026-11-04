/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.data.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(value=RetentionPolicy.RUNTIME)
@Target(value={ElementType.FIELD, ElementType.METHOD})
public @interface NotNull {
    public String defaultValue() default "";
}
