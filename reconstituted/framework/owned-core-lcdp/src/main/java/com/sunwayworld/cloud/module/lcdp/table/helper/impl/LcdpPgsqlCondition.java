package com.sunwayworld.cloud.module.lcdp.table.helper.impl;

import java.util.Arrays;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class LcdpPgsqlCondition implements Condition {
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        String[] activeProfiles = context.getEnvironment().getActiveProfiles();
        boolean postgresqlProfile = activeProfiles != null
                && Arrays.asList(activeProfiles).contains("postgresql");
        Boolean kingbaseEnabled = context.getEnvironment()
                .getProperty("kingbase.enabled", Boolean.class);
        return postgresqlProfile || Boolean.TRUE.equals(kingbaseEnabled);
    }
}
