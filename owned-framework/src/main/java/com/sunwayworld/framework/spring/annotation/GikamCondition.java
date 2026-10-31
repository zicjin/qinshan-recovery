package com.sunwayworld.framework.spring.annotation;

import java.io.IOException;
import java.util.Arrays;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

/** Selects the most specific enabled business bean within the compatibility namespace. */
class GikamCondition implements Condition {
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        if (loader == null) loader = ClassLoader.getSystemClassLoader();
        try {
            Class<?> type = Class.forName(((AnnotationMetadata) metadata).getClassName(), false, loader);
            if (!enabled(context, type)) return false;
            PathMatchingResourcePatternResolver resources = new PathMatchingResourcePatternResolver(loader);
            CachingMetadataReaderFactory readers = new CachingMetadataReaderFactory(resources);
            for (Resource resource : resources.getResources("classpath*:com/sunwayworld/**/*.class")) {
                String name = readers.getMetadataReader(resource).getClassMetadata().getClassName();
                Class<?> candidate = Class.forName(name, false, loader);
                if (candidate != type && type.isAssignableFrom(candidate) && enabled(context, candidate)) return false;
            }
            return true;
        } catch (IOException | ClassNotFoundException failure) {
            throw new IllegalStateException("Unable to select compatibility bean", failure);
        }
    }

    private boolean enabled(ConditionContext context, Class<?> type) {
        // Historical attachment controllers carry component annotations and are
        // discovered by the broad application scan even when the owned module
        // is disabled. Keep that optional route out of the context unless the
        // explicit runtime switch enables the owned attachment boundary.
        if (type.getName().startsWith("com.sunwayworld.emc.module.item.homeattachment.")
                && !"true".equalsIgnoreCase(context.getEnvironment().getProperty("owned.attachments.enabled"))) {
            return false;
        }
        boolean recognized = type.isAnnotationPresent(Controller.class)
                || type.isAnnotationPresent(RestController.class)
                || type.isAnnotationPresent(Component.class)
                || type.isAnnotationPresent(Service.class)
                || type.isAnnotationPresent(Repository.class)
                || type.getPackage().getName().endsWith(".mapper")
                || type.getName().endsWith("Mapper");
        if (!recognized) return false;
        Profile profile = type.getAnnotation(Profile.class);
        // This compatibility contract uses literal, conjunctive active profiles.
        // Spring's expression/OR interpretation would change override selection.
        return profile == null || Arrays.asList(context.getEnvironment().getActiveProfiles()).containsAll(Arrays.asList(profile.value()));
    }
}
