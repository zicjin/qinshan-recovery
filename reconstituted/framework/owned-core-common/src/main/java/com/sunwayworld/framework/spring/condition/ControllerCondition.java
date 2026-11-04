/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.spring.condition;

import com.sunwayworld.framework.utils.ClassUtils;
import java.lang.reflect.Modifier;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

@Deprecated
public class ControllerCondition
implements Condition {
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        AnnotationMetadata visitor = (AnnotationMetadata)metadata;
        Class targetClass = ClassUtils.getClass(visitor.getClassName());
        int targetClassModifiers = targetClass.getModifiers();
        if (!Modifier.isAbstract(targetClassModifiers) && !Modifier.isInterface(targetClassModifiers)) {
            return !ClassUtils.hasAnyMatchClass("com.sunwayworld", t -> targetClass.isAssignableFrom((Class<?>)t) && !t.equals(targetClass) && (t.isAnnotationPresent(Controller.class) || t.isAnnotationPresent(RestController.class)));
        }
        return true;
    }
}
