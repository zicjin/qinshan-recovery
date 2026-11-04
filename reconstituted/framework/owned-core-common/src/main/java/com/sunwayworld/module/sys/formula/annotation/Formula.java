/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.formula.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value={ElementType.METHOD})
@Retention(value=RetentionPolicy.RUNTIME)
@Inherited
public @interface Formula {
    public String itemId() default "";

    public String itemName() default "";

    public String testId() default "";

    public String orderId() default "";

    public String repeatNo() default "";

    public boolean folder() default false;
}
