/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.restful.property;

import com.sunwayworld.framework.restful.property.RestfulResolverDefinition;
import com.sunwayworld.framework.restful.property.RestfulResolverProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RestfulResolverConfiguration {
    @Bean
    public RestfulResolverDefinition restfulResolverDefinition(RestfulResolverProperties properties) {
        return new RestfulResolverDefinition(properties.isRestJsonWrapperEnable());
    }
}
