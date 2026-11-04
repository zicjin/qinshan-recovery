/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.fastdfs.config;

import com.sunwayworld.framework.fastdfs.loadbalancer.FastDfsRoundRule;
import com.sunwayworld.framework.fastdfs.loadbalancer.FastDfsRule;
import com.sunwayworld.framework.fastdfs.properties.FastDfsProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix="sunway.fastdfs", name={"enabled"}, havingValue="true")
@EnableConfigurationProperties(value={FastDfsProperties.class})
public class FastDfsAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean
    public FastDfsRule fastDfsRule(FastDfsProperties dfsProperties) {
        return new FastDfsRoundRule(dfsProperties);
    }
}
