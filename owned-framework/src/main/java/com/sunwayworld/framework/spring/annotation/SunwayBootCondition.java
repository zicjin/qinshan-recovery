package com.sunwayworld.framework.spring.annotation;

import com.sunwayworld.framework.spring.SunwayApplication;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.core.type.AnnotationMetadata;

class SunwayBootCondition implements Condition {
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        String name = ((AnnotationMetadata)metadata).getClassName();
        Class<?> main = SunwayApplication.getSunwayMainApplicationClass();
        return main != null && name.equals(main.getName());
    }
}
