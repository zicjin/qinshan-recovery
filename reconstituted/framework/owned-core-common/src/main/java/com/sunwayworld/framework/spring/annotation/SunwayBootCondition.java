/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.spring.annotation;

import com.sunwayworld.framework.spring.SunwayApplication;
import com.sunwayworld.framework.utils.ClassUtils;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.core.type.AnnotationMetadata;

class SunwayBootCondition
implements Condition {
    SunwayBootCondition() {
    }

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        AnnotationMetadata visitor = (AnnotationMetadata)metadata;
        Class targetClass = ClassUtils.getClass(visitor.getClassName());
        return targetClass.equals(SunwayApplication.getSunwayMainApplicationClass());
    }
}
