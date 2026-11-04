/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.validator.data.annotation;

import com.sunwayworld.framework.validator.data.DataValidator;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Retention(value=RetentionPolicy.RUNTIME)
@Target(value={ElementType.METHOD})
@Inherited
public @interface ValidateDataWith {
    public Class<? extends DataValidator> value();
}
