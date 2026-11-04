/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.validator.permission.annotation;

import com.sunwayworld.framework.validator.permission.PermissionValidator;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Retention(value=RetentionPolicy.RUNTIME)
@Target(value={ElementType.METHOD})
public @interface ValidatePermissionWith {
    public Class<? extends PermissionValidator> value();

    public String table();

    public String category() default "normal";

    public int pos() default 0;
}
