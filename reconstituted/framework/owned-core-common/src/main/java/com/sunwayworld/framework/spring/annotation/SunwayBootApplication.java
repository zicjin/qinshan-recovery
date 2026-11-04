/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.spring.annotation;

import com.sunwayworld.framework.spring.annotation.SunwayBootCondition;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

@Target(value={ElementType.TYPE})
@Retention(value=RetentionPolicy.RUNTIME)
@Documented
@Configuration
@Conditional(value={SunwayBootCondition.class})
public @interface SunwayBootApplication {
}
