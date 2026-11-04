/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.spring.annotation;

import com.sunwayworld.framework.utils.ClassUtils;
import com.sunwayworld.framework.utils.StringUtils;
import java.util.Arrays;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Profile;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

class GikamCondition
implements Condition {
    GikamCondition() {
    }

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        AnnotationMetadata visitor = (AnnotationMetadata)metadata;
        Class targetClass = ClassUtils.getClass(visitor.getClassName());
        if (!this.isSpringBean(context, targetClass)) {
            return false;
        }
        return !ClassUtils.hasAnyMatchClass("com.sunwayworld", t -> targetClass.isAssignableFrom((Class<?>)t) && !t.equals(targetClass) && this.isSpringBean(context, (Class<?>)t));
    }

    private boolean isSpringBean(ConditionContext context, Class<?> clazz) {
        boolean flag;
        boolean bl = flag = clazz.isAnnotationPresent(Controller.class) || clazz.isAnnotationPresent(RestController.class) || clazz.isAnnotationPresent(Component.class) || clazz.isAnnotationPresent(Service.class) || clazz.isAnnotationPresent(Repository.class) || StringUtils.endsWith(clazz.getPackage().getName(), ".mapper") || StringUtils.endsWith(clazz.getName(), "Mapper");
        if (flag && clazz.isAnnotationPresent(Profile.class)) {
            Profile profile = clazz.getAnnotation(Profile.class);
            for (String value : profile.value()) {
                if (!Arrays.stream(context.getEnvironment().getActiveProfiles()).noneMatch(p -> p.equals(value))) continue;
                flag = false;
            }
        }
        return flag;
    }
}
