/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.log.annotation;

import com.sunwayworld.framework.log.annotation.LogType;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.apache.logging.log4j.spi.StandardLevel;

@Target(value={ElementType.METHOD})
@Retention(value=RetentionPolicy.RUNTIME)
@Inherited
public @interface Log {
    public String value();

    public LogType type() default LogType.DEFAULT;

    public StandardLevel level() default StandardLevel.INFO;
}
