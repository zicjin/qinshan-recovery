/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.database.dialect;

import com.sunwayworld.framework.database.dialect.Dialect;
import com.sunwayworld.framework.database.dialect.DialectRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DialectConfig {
    @Bean
    public Dialect dialect() {
        return DialectRepository.getDialect();
    }
}
