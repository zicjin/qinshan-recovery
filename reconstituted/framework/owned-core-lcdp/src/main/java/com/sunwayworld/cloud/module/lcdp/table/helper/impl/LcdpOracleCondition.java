package com.sunwayworld.cloud.module.lcdp.table.helper.impl;

import java.util.Arrays;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class LcdpOracleCondition implements Condition {
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        String[] activeProfiles = context.getEnvironment().getActiveProfiles();
        boolean oracleProfile = activeProfiles != null
                && Arrays.asList(activeProfiles).contains("oracle");
        Boolean kingbaseEnabled = context.getEnvironment()
                .getProperty("kingbase.enabled", Boolean.class);
        return oracleProfile && !Boolean.TRUE.equals(kingbaseEnabled);
    }
}
