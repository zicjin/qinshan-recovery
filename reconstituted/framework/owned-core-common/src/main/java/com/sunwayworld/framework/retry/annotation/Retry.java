/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.retry.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.core.annotation.AliasFor;

@Target(value={ElementType.METHOD})
@Retention(value=RetentionPolicy.RUNTIME)
@Inherited
public @interface Retry {
    @AliasFor(value="retryFor")
    public Class<? extends Throwable>[] value() default {};

    @AliasFor(value="value")
    public Class<? extends Throwable>[] retryFor() default {};

    public Class<? extends Throwable>[] noRetryFor() default {};

    public String maxAttempts() default "MAX_ATTEMPTS";
}
